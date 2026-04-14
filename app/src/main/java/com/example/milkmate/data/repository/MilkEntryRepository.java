package com.example.milkmate.data.repository;

import androidx.lifecycle.LiveData;

import com.example.milkmate.data.local.dao.MilkEntryDao;
import com.example.milkmate.data.local.dao.MilkRateDao;
import com.example.milkmate.data.local.entity.MilkEntryEntity;
import com.example.milkmate.data.local.entity.MilkRateEntity;
import com.example.milkmate.data.repository.mapper.EntityMapper;
import com.example.milkmate.domain.model.MilkEntry;
import com.example.milkmate.DateUtils;
import com.example.milkmate.utils.AppExecutors;

import java.util.List;
import java.util.UUID;

/**
 * Repository for milk entry operations.
 * rateAtTime is stored per entry - critical for billing accuracy.
 */
public class MilkEntryRepository {

    private final MilkEntryDao entryDao;
    private final MilkRateDao rateDao;
    private final AppExecutors executors;

    public MilkEntryRepository(MilkEntryDao entryDao, MilkRateDao rateDao, AppExecutors executors) {
        this.entryDao = entryDao;
        this.rateDao = rateDao;
        this.executors = executors;
    }

    public LiveData<List<MilkEntryEntity>> getByDate(String date) {
        return entryDao.getByDate(date);
    }

    public List<MilkEntryEntity> getByDateSync(String date) {
        return entryDao.getByDateSync(date);
    }

    public LiveData<MilkEntryEntity> getByCustomerAndDate(String customerId, String date) {
        return entryDao.getByCustomerAndDateLive(customerId, date);
    }

    public MilkEntryEntity getByCustomerAndDateSync(String customerId, String date) {
        return entryDao.getByCustomerAndDate(customerId, date);
    }

    public LiveData<List<MilkEntryEntity>> getByCustomerAndDateRange(String customerId, String startDate, String endDate) {
        return entryDao.getByCustomerAndDateRange(customerId, startDate, endDate);
    }

    public LiveData<List<MilkEntryEntity>> getEntriesForUserInRange(String userId, String startDate, String endDate) {
        return entryDao.getEntriesForUserInRange(userId, startDate, endDate);
    }

    public List<MilkEntryEntity> getEntriesForUserInRangeSync(String userId, String startDate, String endDate) {
        return entryDao.getEntriesForUserInRangeSync(userId, startDate, endDate);
    }

    /**
     * Save or update milk entry. Auto-fetches rate if not provided.
     */
    public void saveMilkEntry(String customerId, String date, double morningQty, double eveningQty,
                              String milkType, Double rateOverride, OnResult listener) {
        executors.diskIO().execute(() -> {
            double rate = rateOverride != null ? rateOverride : 0;
            if (rate <= 0) {
                long ts = DateUtils.parseToTimestamp(date);
                com.example.milkmate.data.local.entity.MilkRateEntity r = rateDao.getEffectiveRate(milkType != null ? milkType : "Cow", ts);
                rate = r != null ? r.rate : 0;
            }
            long now = System.currentTimeMillis();
            MilkEntryEntity existing = entryDao.getByCustomerAndDate(customerId, date);
            String id;
            if (existing != null) {
                existing.morningQty = morningQty;
                existing.eveningQty = eveningQty;
                existing.rateAtTime = rate;
                existing.milkType = milkType;
                existing.isDelivered = true;
                existing.skipReason = null;
                existing.updatedAt = now;
                entryDao.update(existing);
                id = existing.id;
            } else {
                id = UUID.randomUUID().toString();
                MilkEntryEntity e = new MilkEntryEntity(id, customerId, date, morningQty, eveningQty,
                        rate, milkType, true, null, now, now, null);
                entryDao.insert(e);
            }
            String finalId = id;
            executors.mainThread().execute(() -> {
                if (listener != null) listener.onSuccess(finalId);
            });
        });
    }

    public void insert(MilkEntry entry, OnResult listener) {
        executors.diskIO().execute(() -> {
            String id = entry.id != null && !entry.id.isEmpty() ? entry.id : UUID.randomUUID().toString();
            MilkEntryEntity e = EntityMapper.toMilkEntryEntity(entry, null);
            e.id = id;
            entryDao.insert(e);
            executors.mainThread().execute(() -> {
                if (listener != null) listener.onSuccess(id);
            });
        });
    }

    /**
     * Add quantity to existing entry (or create). Used by quick buttons.
     * Supports morning/evening split. Auto-saves.
     */
    public void addQuantity(String customerId, String date, double morningDelta, double eveningDelta,
                            String milkType, OnResult listener) {
        executors.diskIO().execute(() -> {
            double rate = 0;
            long ts = DateUtils.parseToTimestamp(date);
            com.example.milkmate.data.local.entity.MilkRateEntity r = rateDao.getEffectiveRate(
                    milkType != null ? milkType : "Cow", ts);
            rate = r != null ? r.rate : 0;

            long now = System.currentTimeMillis();
            MilkEntryEntity existing = entryDao.getByCustomerAndDate(customerId, date);
            double newMorning, newEvening;
            if (existing != null) {
                newMorning = existing.morningQty + morningDelta;
                newEvening = existing.eveningQty + eveningDelta;
                existing.morningQty = Math.max(0, newMorning);
                existing.eveningQty = Math.max(0, newEvening);
                existing.rateAtTime = rate;
                existing.isDelivered = true;
                existing.skipReason = null;
                existing.updatedAt = now;
                entryDao.update(existing);
            } else {
                String id = UUID.randomUUID().toString();
                MilkEntryEntity e = new MilkEntryEntity(id, customerId, date,
                        Math.max(0, morningDelta), Math.max(0, eveningDelta), rate, milkType,
                        true, null, now, now, null);
                entryDao.insert(e);
            }
            executors.mainThread().execute(() -> {
                if (listener != null) listener.onSuccess("ok");
            });
        });
    }

    /**
     * Restore entry to previous state (undo last add).
     */
    public void undoToState(String customerId, String date, double morningQty, double eveningQty,
                            OnResult listener) {
        executors.diskIO().execute(() -> {
            MilkEntryEntity existing = entryDao.getByCustomerAndDate(customerId, date);
            if (existing != null) {
                existing.morningQty = Math.max(0, morningQty);
                existing.eveningQty = Math.max(0, eveningQty);
                existing.updatedAt = System.currentTimeMillis();
                if (morningQty <= 0 && eveningQty <= 0) {
                    entryDao.deleteById(existing.id);
                } else {
                    entryDao.update(existing);
                }
            }
            executors.mainThread().execute(() -> {
                if (listener != null) listener.onSuccess("ok");
            });
        });
    }

    /**
     * Mark as not delivered for the date.
     */
    public void markNotDelivered(String customerId, String date, String skipReason, OnResult listener) {
        executors.diskIO().execute(() -> {
            MilkEntryEntity existing = entryDao.getByCustomerAndDate(customerId, date);
            if (existing != null) {
                existing.isDelivered = false;
                existing.skipReason = skipReason;
                existing.updatedAt = System.currentTimeMillis();
                entryDao.update(existing);
            } else {
                String id = UUID.randomUUID().toString();
                long now = System.currentTimeMillis();
                MilkRateEntity r = rateDao.getEffectiveRate("Cow", DateUtils.parseToTimestamp(date));
                double rate = r != null ? r.rate : 0;
                MilkEntryEntity e = new MilkEntryEntity(id, customerId, date, 0, 0, rate, "Cow",
                        false, skipReason, now, now, null);
                entryDao.insert(e);
            }
            executors.mainThread().execute(() -> {
                if (listener != null) listener.onSuccess("ok");
            });
        });
    }

    public void update(MilkEntry entry, OnResult listener) {
        executors.diskIO().execute(() -> {
            entryDao.update(EntityMapper.toMilkEntryEntity(entry, null));
            executors.mainThread().execute(() -> {
                if (listener != null) listener.onSuccess(entry.id);
            });
        });
    }

    public interface OnResult {
        void onSuccess(String id);
        default void onError(Throwable t) {}
    }
}
