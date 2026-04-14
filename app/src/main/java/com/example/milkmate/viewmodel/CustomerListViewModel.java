package com.example.milkmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagingData;

import com.example.milkmate.data.local.entity.CustomerEntity;
import com.example.milkmate.data.repository.CustomerRepository;
import com.example.milkmate.domain.model.Customer;

import java.util.List;

import dagger.hilt.android.lifecycle.HiltViewModel;

import javax.inject.Inject;

/**
 * ViewModel for Customer list with search and filter.
 */
@HiltViewModel
public class CustomerListViewModel extends ViewModel {

    private final CustomerRepository customerRepo;

    @Inject
    public CustomerListViewModel(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    public LiveData<List<Customer>> getActiveByUserId(String userId) {
        return customerRepo.getActiveByUserId(userId);
    }

    public LiveData<Integer> getActiveCount(String userId) {
        return customerRepo.getActiveCount(userId);
    }

    public LiveData<List<String>> getDistinctAreas(String userId) {
        return customerRepo.getDistinctAreas(userId);
    }

    public LiveData<List<Customer>> getAllForRouteOptimization(String userId) {
        return customerRepo.getAllForRouteOptimization(userId);
    }
}
