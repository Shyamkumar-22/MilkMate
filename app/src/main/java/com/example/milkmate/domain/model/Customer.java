package com.example.milkmate.domain.model;

/**
 * Domain model for Customer.
 */
public class Customer {

    public String id;
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

    public Customer() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer c = (Customer) o;
        return id != null && id.equals(c.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Customer(String id, String houseNumber, String name, String phone, String address,
                    String area, String milkType, boolean isActive, String userId,
                    long createdAt, long updatedAt) {
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
    }
}
