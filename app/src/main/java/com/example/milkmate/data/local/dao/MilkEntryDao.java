package com.example.milkmate.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.milkmate.data.local.entity.MilkEntryEntity;

import java.util.List;

/**
 * DAO for MilkEntry operations.
 * Critical: rateAtTime is stored per entry for correct billing history.
 */
@Dao
public interface MilkEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MilkEntryEntity entry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MilkEntryEntity> entries);

    @Update
    void update(MilkEntryEntity entry);

    @Query("DELETE FROM milk_entries WHERE id = :id")
    void deleteById(String id);

    @Query("SELECT * FROM milk_entries WHERE id = :id")
    MilkEntryEntity getById(String id);

    @Query("SELECT * FROM milk_entries WHERE customerId = :customerId AND date = :date")
    MilkEntryEntity getByCustomerAndDate(String customerId, String date);

    @Query("SELECT * FROM milk_entries WHERE customerId = :customerId AND date = :date")
    LiveData<MilkEntryEntity> getByCustomerAndDateLive(String customerId, String date);

    @Query("SELECT * FROM milk_entries WHERE customerId = :customerId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    LiveData<List<MilkEntryEntity>> getByCustomerAndDateRange(String customerId, String startDate, String endDate);

    @Query("SELECT * FROM milk_entries WHERE customerId = :customerId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    List<MilkEntryEntity> getByCustomerAndDateRangeSync(String customerId, String startDate, String endDate);

    @Query("SELECT * FROM milk_entries WHERE date = :date ORDER BY customerId ASC")
    LiveData<List<MilkEntryEntity>> getByDate(String date);

    @Query("SELECT * FROM milk_entries WHERE date = :date ORDER BY customerId ASC")
    List<MilkEntryEntity> getByDateSync(String date);

    @Query("SELECT * FROM milk_entries WHERE customerId IN (SELECT id FROM customers WHERE userId = :userId) AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    LiveData<List<MilkEntryEntity>> getEntriesForUserInRange(String userId, String startDate, String endDate);

    @Query("SELECT * FROM milk_entries WHERE customerId IN (SELECT id FROM customers WHERE userId = :userId) AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    List<MilkEntryEntity> getEntriesForUserInRangeSync(String userId, String startDate, String endDate);

    @Query("SELECT * FROM milk_entries WHERE customerId = :customerId ORDER BY date DESC LIMIT :limit")
    LiveData<List<MilkEntryEntity>> getRecentByCustomer(String customerId, int limit);

    @Query("SELECT * FROM milk_entries WHERE customerId = :customerId AND date < :beforeDate ORDER BY date DESC LIMIT 1")
    MilkEntryEntity getLastEntryBeforeDate(String customerId, String beforeDate);

    @Query("SELECT * FROM milk_entries WHERE updatedAt > :since AND firestoreId IS NOT NULL")
    List<MilkEntryEntity> getUnsyncedEntries(long since);
}
