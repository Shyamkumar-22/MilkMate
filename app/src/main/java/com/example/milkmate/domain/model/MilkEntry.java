package com.example.milkmate.domain.model;

/**
 * Domain model for daily milk entry.
 * rateAtTime is critical - old data must not change when rates update.
 */
public class MilkEntry {

    public String id;
    public String customerId;
    public String date;
    public double morningQty;
    public double eveningQty;
    public double rateAtTime;
    public String milkType;
    public boolean isDelivered;
    public String skipReason;
    public long createdAt;
    public long updatedAt;

    public MilkEntry() {}

    public MilkEntry(String id, String customerId, String date, double morningQty, double eveningQty,
                     double rateAtTime, String milkType, boolean isDelivered, String skipReason,
                     long createdAt, long updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.date = date;
        this.morningQty = morningQty;
        this.eveningQty = eveningQty;
        this.rateAtTime = rateAtTime;
        this.milkType = milkType;
        this.isDelivered = isDelivered;
        this.skipReason = skipReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public double getTotalQty() {
        return morningQty + eveningQty;
    }

    public double getTotalAmount() {
        return getTotalQty() * rateAtTime;
    }
}
