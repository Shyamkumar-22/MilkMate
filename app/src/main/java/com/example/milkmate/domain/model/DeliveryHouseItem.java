package com.example.milkmate.domain.model;

import com.example.milkmate.data.local.entity.CustomerEntity;
import com.example.milkmate.data.local.entity.MilkEntryEntity;

/**
 * Item for Daily Delivery Panel - combines customer with today's entry and last delivery status.
 * Used in chat-style RecyclerView.
 */
public class DeliveryHouseItem {

    public final CustomerEntity customer;
    /** Today's milk entry, or null if not yet entered. */
    public final MilkEntryEntity todayEntry;
    /** Last delivery status text (e.g. "Yesterday: 2L"). */
    public final String lastDeliveryStatus;

    public DeliveryHouseItem(CustomerEntity customer, MilkEntryEntity todayEntry, String lastDeliveryStatus) {
        this.customer = customer;
        this.todayEntry = todayEntry;
        this.lastDeliveryStatus = lastDeliveryStatus != null ? lastDeliveryStatus : "";
    }

    /** Today's status: Delivered, Pending, or Not Delivered. */
    public String getTodayStatus() {
        if (todayEntry == null) return "Pending";
        if (!todayEntry.isDelivered) return "Not Delivered";
        double total = todayEntry.morningQty + todayEntry.eveningQty;
        return total > 0 ? "Delivered" : "Pending";
    }

    public double getTodayTotalQty() {
        return todayEntry != null ? todayEntry.morningQty + todayEntry.eveningQty : 0;
    }
}
