package com.example.milkmate.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.milkmate.data.local.entity.PaymentEntity;

import java.util.List;

/**
 * DAO for Payment/Billing operations.
 */
@Dao
public interface PaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PaymentEntity payment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PaymentEntity> payments);

    @Update
    void update(PaymentEntity payment);

    @Query("SELECT * FROM payments WHERE id = :id")
    LiveData<PaymentEntity> getById(String id);

    @Query("SELECT * FROM payments WHERE id = :id")
    PaymentEntity getByIdSync(String id);

    @Query("SELECT * FROM payments WHERE customerId = :customerId AND monthYear = :monthYear")
    PaymentEntity getByCustomerAndMonth(String customerId, String monthYear);

    @Query("SELECT * FROM payments WHERE customerId = :customerId ORDER BY monthYear DESC")
    LiveData<List<PaymentEntity>> getByCustomer(String customerId);

    @Query("SELECT * FROM payments WHERE customerId IN (SELECT id FROM customers WHERE userId = :userId) AND monthYear = :monthYear ORDER BY status ASC, customerId ASC")
    LiveData<List<PaymentEntity>> getByUserIdAndMonth(String userId, String monthYear);

    @Query("SELECT * FROM payments WHERE customerId IN (SELECT id FROM customers WHERE userId = :userId) AND status = 'PENDING' ORDER BY monthYear DESC")
    LiveData<List<PaymentEntity>> getPendingByUserId(String userId);

    @Query("SELECT SUM(totalAmount) FROM payments WHERE customerId IN (SELECT id FROM customers WHERE userId = :userId) AND monthYear = :monthYear AND status = 'PAID'")
    LiveData<Double> getTotalRevenueForMonth(String userId, String monthYear);

    @Query("SELECT * FROM payments WHERE updatedAt > :since AND firestoreId IS NOT NULL")
    List<PaymentEntity> getUnsyncedPayments(long since);
}
