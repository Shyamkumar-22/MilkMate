package com.example.milkmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.milkmate.data.local.entity.CustomerEntity;
import com.example.milkmate.data.local.entity.MilkEntryEntity;
import com.example.milkmate.data.repository.CustomerRepository;
import com.example.milkmate.data.repository.MilkEntryRepository;
import com.example.milkmate.DateUtils;
import com.example.milkmate.domain.model.DeliveryHouseItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dagger.hilt.android.lifecycle.HiltViewModel;

import javax.inject.Inject;

/**
 * ViewModel for Daily Delivery Panel (chat-style house list).
 * Combines customers (sorted by house number) with today's milk entries.
 */
@HiltViewModel
public class DeliveryPanelViewModel extends ViewModel {

    private final CustomerRepository customerRepo;
    private final MilkEntryRepository milkEntryRepo;
    private final MediatorLiveData<List<DeliveryHouseItem>> deliveryItems = new MediatorLiveData<>();
    private final MutableLiveData<String> todayRate = new MutableLiveData<>();

    @Inject
    public DeliveryPanelViewModel(CustomerRepository customerRepo, MilkEntryRepository milkEntryRepo) {
        this.customerRepo = customerRepo;
        this.milkEntryRepo = milkEntryRepo;
    }

    private List<CustomerEntity> customersCache;
    private List<MilkEntryEntity> entriesCache;

    /**
     * Load delivery panel. Call once with milkman's userId.
     */
    public void load(String userId) {
        String today = DateUtils.today();
        customersCache = null;
        entriesCache = null;

        deliveryItems.addSource(customerRepo.getDeliveryPanelHouses(userId), list -> {
            customersCache = list;
            combine();
        });
        deliveryItems.addSource(milkEntryRepo.getEntriesForUserInRange(userId, today, today), list -> {
            entriesCache = list;
            combine();
        });
    }

    private void combine() {
        if (customersCache == null) return;

        Map<String, MilkEntryEntity> entryMap = new HashMap<>();
        if (entriesCache != null) {
            for (MilkEntryEntity e : entriesCache) {
                entryMap.put(e.customerId, e);
            }
        }

        List<DeliveryHouseItem> items = new ArrayList<>();
        for (CustomerEntity c : customersCache) {
            MilkEntryEntity todayEntry = entryMap.get(c.id);
            String lastStatus = "";
            items.add(new DeliveryHouseItem(c, todayEntry, lastStatus));
        }
        deliveryItems.setValue(items);
    }

    public LiveData<List<DeliveryHouseItem>> getDeliveryItems() {
        return deliveryItems;
    }
}
