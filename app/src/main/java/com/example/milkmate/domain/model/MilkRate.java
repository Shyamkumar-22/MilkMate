package com.example.milkmate.domain.model;

/**
 * Domain model for milk rate.
 * Supports Cow, Buffalo, Toned with effectiveFrom date logic.
 */
public class MilkRate {

    public static final String TYPE_COW = "Cow";
    public static final String TYPE_BUFFALO = "Buffalo";
    public static final String TYPE_TONED = "Toned";

    public String id;
    public String milkType;
    public double rate;
    public long effectiveFrom;
    public long effectiveTo;
    public long createdAt;

    public MilkRate() {}

    public MilkRate(String id, String milkType, double rate,
                    long effectiveFrom, long effectiveTo, long createdAt) {
        this.id = id;
        this.milkType = milkType;
        this.rate = rate;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.createdAt = createdAt;
    }
}
