package com.example.milkmate.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.milkmate.data.local.entity.CustomerEntity;

import java.util.List;

/**
 * DAO for Customer operations.
 * Supports pagination (Paging 3), search and filter by area.
 */
@Dao
public interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CustomerEntity customer);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CustomerEntity> customers);

    @Update
    void update(CustomerEntity customer);

    @Query("DELETE FROM customers WHERE id = :id")
    void deleteById(String id);

    @Query("SELECT * FROM customers WHERE id = :id")
    LiveData<CustomerEntity> getById(String id);

    @Query("SELECT * FROM customers WHERE id = :id")
    CustomerEntity getByIdSync(String id);

    @Query("SELECT * FROM customers WHERE userId = :userId AND isActive = 1 ORDER BY name ASC")
    LiveData<List<CustomerEntity>> getActiveByUserId(String userId);

    @Query("SELECT * FROM customers WHERE userId = :userId ORDER BY name ASC")
    PagingSource<Integer, CustomerEntity> getPagedByUserId(String userId);

    @Query("SELECT * FROM customers WHERE userId = :userId AND area LIKE '%' || :area || '%' ORDER BY name ASC")
    PagingSource<Integer, CustomerEntity> getPagedByUserIdAndArea(String userId, String area);

    @Query("SELECT * FROM customers WHERE userId = :userId AND (name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%' OR address LIKE '%' || :query || '%') ORDER BY name ASC")
    PagingSource<Integer, CustomerEntity> searchByUserId(String userId, String query);

    @Query("SELECT DISTINCT area FROM customers WHERE userId = :userId AND area IS NOT NULL AND area != '' ORDER BY area ASC")
    LiveData<List<String>> getDistinctAreas(String userId);

    @Query("SELECT COUNT(*) FROM customers WHERE userId = :userId AND isActive = 1")
    LiveData<Integer> getActiveCount(String userId);

    @Query("SELECT * FROM customers WHERE userId = :userId ORDER BY area, name ASC")
    LiveData<List<CustomerEntity>> getAllForRouteOptimization(String userId);

    /** For Daily Delivery Panel: active customers sorted by house number (ascending). */
    @Query("SELECT * FROM customers WHERE userId = :userId AND isActive = 1 ORDER BY houseNumber ASC, name ASC")
    LiveData<List<CustomerEntity>> getDeliveryPanelHouses(String userId);
}
