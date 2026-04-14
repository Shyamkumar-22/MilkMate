package com.example.milkmate.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Room entity for Customer.
 * Indexed on phone and userId for efficient search and filtering.
 */
@Entity(
    tableName = "customers",
    indices = {
        @Index(value = {"phone"}),
        @Index(value = {"userId"}),
        @Index(value = {"area"}),
        @Index(value = {"isActive"}),
        @Index(value = {"houseNumber"})
    }
)
public class CustomerEntity {

    @PrimaryKey
    @NonNull
    public String id;

    /** House number for delivery route sorting (e.g. "101", "A-5"). */
    public String houseNumber;
    public String name;
    public String phone;
    public String address;
    public String area;
    public String milkType;
    public boolean isActive;
    public String userId;

    public long createdAt;
    public long updatedAt;
    public String firestoreId;

    public CustomerEntity() {
        this.id = "";
        this.houseNumber = "";
    }

    public CustomerEntity(@NonNull String id, String houseNumber, String name, String phone, String address,
                          String area, String milkType, boolean isActive, String userId,
                          long createdAt, long updatedAt, String firestoreId) {
        this.id = id;
        this.houseNumber = houseNumber != null ? houseNumber : "";
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.area = area;
        this.milkType = milkType;
        this.isActive = isActive;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.firestoreId = firestoreId;
    }
}
