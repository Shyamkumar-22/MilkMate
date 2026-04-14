package com.example.milkmate.utils;

import com.example.milkmate.data.local.entity.MilkEntryEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Billing engine - calculates monthly bills from milk entries.
 * totalMilk = sum(morningQty + eveningQty)
 * totalAmount = sum((morningQty + eveningQty) * rateAtTime)
 * IMPORTANT: rateAtTime is stored per entry - old data never changes when rates update.
 */
public final class BillingEngine {

    /**
     * Calculate monthly totals for a single customer.
     */
    public static BillResult calculateForCustomer(String customerId, String monthYear,
                                                   List<MilkEntryEntity> entries) {
        double totalMilk = 0;
        double totalAmount = 0;

        for (MilkEntryEntity e : entries) {
            if (!e.customerId.equals(customerId)) continue;
            if (!e.isDelivered) continue; // Skip non-delivered entries for billing

            double qty = e.morningQty + e.eveningQty;
            totalMilk += qty;
            totalAmount += qty * e.rateAtTime;
        }

        return new BillResult(customerId, monthYear, totalMilk, totalAmount);
    }

    /**
     * Calculate monthly bills for all customers from their entries.
     * Groups entries by customerId.
     */
    public static java.util.List<BillResult> calculateMonthlyBills(String monthYear,
                                                                    List<MilkEntryEntity> allEntries) {
        java.util.Map<String, java.util.List<MilkEntryEntity>> byCustomer = new java.util.HashMap<>();

        for (MilkEntryEntity e : allEntries) {
            if (!e.isDelivered) continue;
            if (!byCustomer.containsKey(e.customerId)) {
                byCustomer.put(e.customerId, new java.util.ArrayList<>());
            }
            byCustomer.get(e.customerId).add(e);
        }

        java.util.List<BillResult> results = new java.util.ArrayList<>();
        for (Map.Entry<String, java.util.List<MilkEntryEntity>> entry : byCustomer.entrySet()) {
            BillResult r = calculateForCustomer(entry.getKey(), monthYear, entry.getValue());
            if (r.totalMilk > 0) {
                results.add(r);
            }
        }
        return results;
    }

    public static class BillResult {
        public final String customerId;
        public final String monthYear;
        public final double totalMilk;
        public final double totalAmount;
        public final String id;

        public BillResult(String customerId, String monthYear, double totalMilk, double totalAmount) {
            this.id = UUID.randomUUID().toString();
            this.customerId = customerId;
            this.monthYear = monthYear;
            this.totalMilk = totalMilk;
            this.totalAmount = totalAmount;
        }
    }
}
