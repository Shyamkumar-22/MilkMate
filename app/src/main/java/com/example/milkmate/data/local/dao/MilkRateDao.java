package com.example.milkmate.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.milkmate.data.local.entity.MilkRateEntity;

import java.util.List;

/**
 * DAO for MilkRate operations.
 * effectiveFrom/effectiveTo determine rate applicability for a date.
 */
@Dao
public interface MilkRateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MilkRateEntity rate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MilkRateEntity> rates);

    @Query("SELECT * FROM milk_rates WHERE milkType = :milkType AND effectiveFrom <= :timestamp AND (effectiveTo IS NULL OR effectiveTo = 0 OR effectiveTo >= :timestamp) ORDER BY effectiveFrom DESC LIMIT 1")
    MilkRateEntity getEffectiveRate(String milkType, long timestamp);

    @Query("SELECT * FROM milk_rates WHERE milkType = :milkType ORDER BY effectiveFrom DESC")
    LiveData<List<MilkRateEntity>> getRateHistory(String milkType);

    @Query("SELECT * FROM milk_rates WHERE effectiveFrom <= :timestamp AND (effectiveTo IS NULL OR effectiveTo = 0 OR effectiveTo >= :timestamp) ORDER BY milkType")
    LiveData<List<MilkRateEntity>> getAllEffectiveRates(long timestamp);

    @Query("SELECT * FROM milk_rates ORDER BY milkType, effectiveFrom DESC")
    LiveData<List<MilkRateEntity>> getAll();

    @Query("SELECT DISTINCT milkType FROM milk_rates ORDER BY milkType")
    LiveData<List<String>> getDistinctMilkTypes();

    @Query("DELETE FROM milk_rates WHERE id = :id")
    void deleteById(String id);
}
