package com.example.milkmate.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Room entity for daily milk entry.
 * rateAtTime is stored per entry so historical bills remain correct when rates change.
 */
@Entity(
    tableName = "milk_entries",
    foreignKeys = @ForeignKey(
        entity = CustomerEntity.class,
        parentColumns = "id",
        childColumns = "customerId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {
        @Index(value = {"customerId"}),
        @Index(value = {"date"}),
        @Index(value = {"customerId", "date"}, unique = true)
    }
)
public class MilkEntryEntity {

    @PrimaryKey
    @NonNull
    public String id;

    public String customerId;
    public String date;
    public double morningQty;
    public double eveningQty;

    /**
     * Rate at time of entry - critical for billing accuracy.
     * Old entries must NOT change when rates are updated.
     */
    public double rateAtTime;
    public String milkType;

    /** True if delivered; false if skipped/not delivered. */
    public boolean isDelivered;
    /** Optional reason when isDelivered=false. */
    public String skipReason;

    public long createdAt;
    public long updatedAt;
    public String firestoreId;

    public MilkEntryEntity() {
        this.id = "";
    }

    public MilkEntryEntity(@NonNull String id, String customerId, String date,
                           double morningQty, double eveningQty, double rateAtTime,
                           String milkType, boolean isDelivered, String skipReason,
                           long createdAt, long updatedAt, String firestoreId) {
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
        this.firestoreId = firestoreId;
    }
}
