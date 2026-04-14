package com.example.milkmate.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Milk rate history - supports multiple milk types (Cow, Buffalo, Toned).
 * effectiveFrom/effectiveTo determine which rate applies for a given date.
 */
@Entity(
    tableName = "milk_rates",
    indices = {
        @Index(value = {"milkType"}),
        @Index(value = {"effectiveFrom"}),
        @Index(value = {"milkType", "effectiveFrom"})
    }
)
public class MilkRateEntity {

    @PrimaryKey
    @NonNull
    public String id;

    public String milkType;
    public double rate;
    public long effectiveFrom;
    public long effectiveTo;

    public long createdAt;
    public String firestoreId;

    public MilkRateEntity() {
        this.id = "";
    }

    public MilkRateEntity(@NonNull String id, String milkType, double rate,
                          long effectiveFrom, long effectiveTo, long createdAt, String firestoreId) {
        this.id = id;
        this.milkType = milkType;
        this.rate = rate;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.createdAt = createdAt;
        this.firestoreId = firestoreId;
    }
}
