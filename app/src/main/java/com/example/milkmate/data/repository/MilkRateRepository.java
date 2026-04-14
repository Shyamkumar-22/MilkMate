package com.example.milkmate.data.repository;

import androidx.lifecycle.LiveData;

import com.example.milkmate.data.local.dao.MilkRateDao;
import com.example.milkmate.data.local.entity.MilkRateEntity;
import com.example.milkmate.data.repository.mapper.EntityMapper;
import com.example.milkmate.domain.model.MilkRate;
import com.example.milkmate.utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Repository for milk rate operations.
 */
public class MilkRateRepository {

    private final MilkRateDao dao;
    private final AppExecutors executors;

    public MilkRateRepository(MilkRateDao dao, AppExecutors executors) {
        this.dao = dao;
        this.executors = executors;
    }

    public MilkRateEntity getEffectiveRateSync(String milkType, long timestamp) {
        return dao.getEffectiveRate(milkType, timestamp);
    }

    public LiveData<List<MilkRate>> getRateHistory(String milkType) {
        return androidx.lifecycle.Transformations.map(dao.getRateHistory(milkType), entities -> {
            List<MilkRate> list = new ArrayList<>();
            for (MilkRateEntity e : entities) list.add(EntityMapper.toMilkRate(e));
            return list;
        });
    }

    public LiveData<List<MilkRate>> getAll() {
        return androidx.lifecycle.Transformations.map(dao.getAll(), entities -> {
            List<MilkRate> list = new ArrayList<>();
            for (MilkRateEntity e : entities) list.add(EntityMapper.toMilkRate(e));
            return list;
        });
    }

    public LiveData<List<String>> getDistinctMilkTypes() {
        return dao.getDistinctMilkTypes();
    }

    public void insert(MilkRate rate, CustomerRepository.OnResult listener) {
        executors.diskIO().execute(() -> {
            String id = rate.id != null && !rate.id.isEmpty() ? rate.id : UUID.randomUUID().toString();
            MilkRateEntity e = EntityMapper.toMilkRateEntity(rate, null);
            e.id = id;
            dao.insert(e);
            executors.mainThread().execute(() -> {
                if (listener != null) listener.onSuccess(id);
            });
        });
    }
}
