package com.example.milkmate.data.repository;

import androidx.lifecycle.LiveData;

import com.example.milkmate.data.local.dao.PaymentDao;
import com.example.milkmate.data.local.entity.PaymentEntity;
import com.example.milkmate.data.repository.mapper.EntityMapper;
import com.example.milkmate.domain.model.Payment;
import com.example.milkmate.utils.AppExecutors;
import com.example.milkmate.utils.BillingEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for payment/billing operations.
 */
public class PaymentRepository {

    private final PaymentDao dao;
    private final MilkEntryRepository entryRepo;
    private final AppExecutors executors;

    public PaymentRepository(PaymentDao dao, MilkEntryRepository entryRepo, AppExecutors executors) {
        this.dao = dao;
        this.entryRepo = entryRepo;
        this.executors = executors;
    }

    public LiveData<List<Payment>> getByUserIdAndMonth(String userId, String monthYear) {
        return androidx.lifecycle.Transformations.map(dao.getByUserIdAndMonth(userId, monthYear), entities -> {
            List<Payment> list = new ArrayList<>();
            for (PaymentEntity e : entities) list.add(EntityMapper.toPayment(e));
            return list;
        });
    }

    public LiveData<List<Payment>> getPendingByUserId(String userId) {
        return androidx.lifecycle.Transformations.map(dao.getPendingByUserId(userId), entities -> {
            List<Payment> list = new ArrayList<>();
            for (PaymentEntity e : entities) list.add(EntityMapper.toPayment(e));
            return list;
        });
    }

    public LiveData<Double> getTotalRevenueForMonth(String userId, String monthYear) {
        return dao.getTotalRevenueForMonth(userId, monthYear);
    }

    public void generateMonthlyBills(String userId, String monthYear, OnResult listener) {
        executors.diskIO().execute(() -> {
            String start = com.example.milkmate.DateUtils.firstDayOfMonth(monthYear);
            String end = com.example.milkmate.DateUtils.lastDayOfMonth(monthYear);
            List<com.example.milkmate.data.local.entity.MilkEntryEntity> entries =
                    entryRepo.getEntriesForUserInRangeSync(userId, start, end);
            List<BillingEngine.BillResult> results = BillingEngine.calculateMonthlyBills(monthYear, entries);
            for (BillingEngine.BillResult r : results) {
                PaymentEntity existing = dao.getByCustomerAndMonth(r.customerId, r.monthYear);
                if (existing == null) {
                    long now = System.currentTimeMillis();
                    PaymentEntity p = new PaymentEntity(r.id, r.customerId, r.monthYear, r.totalAmount,
                            r.totalMilk, PaymentEntity.STATUS_PENDING, 0, now, now, null);
                    dao.insert(p);
                }
            }
            executors.mainThread().execute(() -> {
                if (listener != null) listener.onSuccess(results.size());
            });
        });
    }

    public void markPaid(String paymentId, OnResult listener) {
        executors.diskIO().execute(() -> {
            PaymentEntity p = dao.getByIdSync(paymentId);
            if (p != null) {
                p.status = PaymentEntity.STATUS_PAID;
                p.paidAt = System.currentTimeMillis();
                p.updatedAt = p.paidAt;
                dao.update(p);
            }
            executors.mainThread().execute(() -> {
                if (listener != null) listener.onSuccess(paymentId);
            });
        });
    }

    public interface OnResult {
        void onSuccess(Object result);
        default void onError(Throwable t) {}
    }
}
