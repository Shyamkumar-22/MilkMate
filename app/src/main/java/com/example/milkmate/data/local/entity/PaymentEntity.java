package com.example.milkmate.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Monthly payment/bill record.
 * status: PENDING, PAID
 */
@Entity(
    tableName = "payments",
    foreignKeys = @ForeignKey(
        entity = CustomerEntity.class,
        parentColumns = "id",
        childColumns = "customerId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {
        @Index(value = {"customerId"}),
        @Index(value = {"monthYear"}),
        @Index(value = {"status"}),
        @Index(value = {"customerId", "monthYear"}, unique = true)
    }
)
public class PaymentEntity {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PAID = "PAID";

    @PrimaryKey
    @NonNull
    public String id;

    public String customerId;
    public String monthYear;
    public double totalAmount;
    public double totalMilk;
    public String status;
    public long paidAt;

    public long createdAt;
    public long updatedAt;
    public String firestoreId;

    public PaymentEntity() {
        this.id = "";
    }

    public PaymentEntity(@NonNull String id, String customerId, String monthYear,
                         double totalAmount, double totalMilk, String status, long paidAt,
                         long createdAt, long updatedAt, String firestoreId) {
        this.id = id;
        this.customerId = customerId;
        this.monthYear = monthYear;
        this.totalAmount = totalAmount;
        this.totalMilk = totalMilk;
        this.status = status;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.firestoreId = firestoreId;
    }
}
