package com.example.milkmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.milkmate.data.local.entity.MilkEntryEntity;
import com.example.milkmate.data.repository.MilkEntryRepository;

import java.util.List;

import dagger.hilt.android.lifecycle.HiltViewModel;

import javax.inject.Inject;

/**
 * ViewModel for milk entry screen.
 */
@HiltViewModel
public class MilkEntryViewModel extends ViewModel {

    private final MilkEntryRepository milkEntryRepo;

    @Inject
    public MilkEntryViewModel(MilkEntryRepository milkEntryRepo) {
        this.milkEntryRepo = milkEntryRepo;
    }

    public LiveData<MilkEntryEntity> getByCustomerAndDate(String customerId, String date) {
        return milkEntryRepo.getByCustomerAndDate(customerId, date);
    }

    public LiveData<List<MilkEntryEntity>> getByDate(String date) {
        return milkEntryRepo.getByDate(date);
    }

    public void saveMilkEntry(String customerId, String date, double morningQty, double eveningQty,
                              String milkType, Double rateOverride,
                              MilkEntryRepository.OnResult listener) {
        milkEntryRepo.saveMilkEntry(customerId, date, morningQty, eveningQty, milkType,
                rateOverride, listener);
    }

    public void addQuantity(String customerId, String date, double morningDelta, double eveningDelta,
                            String milkType, MilkEntryRepository.OnResult listener) {
        milkEntryRepo.addQuantity(customerId, date, morningDelta, eveningDelta, milkType, listener);
    }

    public void undoToState(String customerId, String date, double morningQty, double eveningQty,
                            MilkEntryRepository.OnResult listener) {
        milkEntryRepo.undoToState(customerId, date, morningQty, eveningQty, listener);
    }

    public void markNotDelivered(String customerId, String date, String skipReason,
                                 MilkEntryRepository.OnResult listener) {
        milkEntryRepo.markNotDelivered(customerId, date, skipReason, listener);
    }
}
