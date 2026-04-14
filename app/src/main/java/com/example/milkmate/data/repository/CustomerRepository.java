package com.example.milkmate.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.PagingSource;

import com.example.milkmate.data.local.dao.CustomerDao;
import com.example.milkmate.data.local.entity.CustomerEntity;
import com.example.milkmate.data.repository.mapper.EntityMapper;
import com.example.milkmate.domain.model.Customer;
import com.example.milkmate.utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Repository for customer operations.
 * Offline-first: Room primary, Firebase sync in background.
 */
public class CustomerRepository {

    private final CustomerDao dao;
    private final AppExecutors executors;

    public CustomerRepository(CustomerDao dao, AppExecutors executors) {
        this.dao = dao;
        this.executors = executors;
    }

    public LiveData<List<Customer>> getActiveByUserId(String userId) {
        return Transformations.map(dao.getActiveByUserId(userId), entities -> {
            List<Customer> list = new ArrayList<>();
            for (CustomerEntity e : entities) list.add(EntityMapper.toCustomer(e));
            return list;
        });
    }

    public LiveData<Integer> getActiveCount(String userId) {
        return dao.getActiveCount(userId);
    }

    public PagingSource<Integer, CustomerEntity> getPagedByUserId(String userId) {
        return dao.getPagedByUserId(userId);
    }

    public PagingSource<Integer, CustomerEntity> getPagedByUserIdAndArea(String userId, String area) {
        return dao.getPagedByUserIdAndArea(userId, area);
    }

    public PagingSource<Integer, CustomerEntity> searchByUserId(String userId, String query) {
        return dao.searchByUserId(userId, query);
    }

    public LiveData<List<String>> getDistinctAreas(String userId) {
        return dao.getDistinctAreas(userId);
    }

    public LiveData<List<CustomerEntity>> getDeliveryPanelHouses(String userId) {
        return dao.getDeliveryPanelHouses(userId);
    }

    public LiveData<List<Customer>> getAllForRouteOptimization(String userId) {
        return Transformations.map(dao.getAllForRouteOptimization(userId), entities -> {
            List<Customer> list = new ArrayList<>();
            for (CustomerEntity e : entities) list.add(EntityMapper.toCustomer(e));
            return list;
        });
    }

    public LiveData<Customer> getById(String id) {
        return Transformations.map(dao.getById(id), EntityMapper::toCustomer);
    }

    public void insert(Customer customer, OnResult listener) {
        executors.diskIO().execute(() -> {
            String id = customer.id != null && !customer.id.isEmpty() ? customer.id : UUID.randomUUID().toString();
            CustomerEntity e = EntityMapper.toCustomerEntity(customer, null);
            e.id = id;
            dao.insert(e);
            executors.mainThread().execute(() -> {
                if (listener != null) listener.onSuccess(id);
            });
        });
    }

    public void update(Customer customer, OnResult listener) {
        executors.diskIO().execute(() -> {
            dao.update(EntityMapper.toCustomerEntity(customer, null));
            executors.mainThread().execute(() -> {
                if (listener != null) listener.onSuccess(customer.id);
            });
        });
    }

    public void delete(String id, OnResult listener) {
        executors.diskIO().execute(() -> {
            dao.deleteById(id);
            executors.mainThread().execute(() -> {
                if (listener != null) listener.onSuccess(id);
            });
        });
    }

    public interface OnResult {
        void onSuccess(String id);
        default void onError(Throwable t) {}
    }
}
