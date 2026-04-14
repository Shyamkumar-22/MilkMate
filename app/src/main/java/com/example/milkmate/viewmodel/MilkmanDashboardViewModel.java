package com.example.milkmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.milkmate.data.local.entity.MilkEntryEntity;
import com.example.milkmate.data.repository.CustomerRepository;
import com.example.milkmate.data.repository.MilkEntryRepository;
import com.example.milkmate.data.repository.PaymentRepository;
import com.example.milkmate.domain.model.Customer;
import com.example.milkmate.DateUtils;
import com.example.milkmate.utils.PredictionEngine;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.lifecycle.HiltViewModel;

import javax.inject.Inject;

/**
 * ViewModel for Milkman Dashboard.
 * Provides summary data: customer count, today's milk, revenue, prediction.
 */
@HiltViewModel
public class MilkmanDashboardViewModel extends ViewModel {

    private final CustomerRepository customerRepo;
    private final MilkEntryRepository milkEntryRepo;
    private final PaymentRepository paymentRepo;

    @Inject
    public MilkmanDashboardViewModel(CustomerRepository customerRepo,
                                     MilkEntryRepository milkEntryRepo,
                                     PaymentRepository paymentRepo) {
        this.customerRepo = customerRepo;
        this.milkEntryRepo = milkEntryRepo;
        this.paymentRepo = paymentRepo;
    }

    public LiveData<Integer> getActiveCustomerCount(String userId) {
        return customerRepo.getActiveCount(userId);
    }

    public LiveData<List<MilkEntryEntity>> getTodayEntries(String userId) {
        String today = DateUtils.today();
        return milkEntryRepo.getEntriesForUserInRange(userId, today, today);
    }

    public LiveData<Double> getMonthlyRevenue(String userId) {
        String month = DateUtils.currentMonthPrefix();
        return paymentRepo.getTotalRevenueForMonth(userId, month);
    }

    /**
     * AI prediction: moving average of last 3 months.
     */
    public String getPredictionMessage(String userId) {
        List<Double> monthlyTotals = new ArrayList<>();
        String month = DateUtils.currentMonthPrefix();
        for (int i = 1; i <= 3; i++) {
            String m = DateUtils.monthAgo(i);
            String start = DateUtils.firstDayOfMonth(m);
            String end = DateUtils.lastDayOfMonth(m);
            List<MilkEntryEntity> entries = milkEntryRepo.getEntriesForUserInRangeSync(userId, start, end);
            double total = 0;
            for (MilkEntryEntity e : entries) total += e.morningQty + e.eveningQty;
            monthlyTotals.add(total);
        }
        double predicted = PredictionEngine.predictNextMonth(monthlyTotals);
        return PredictionEngine.getPredictionMessage(predicted);
    }
}
