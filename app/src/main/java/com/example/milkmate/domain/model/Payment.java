package com.example.milkmate.domain.model;

/**
 * Domain model for monthly payment/bill.
 */
public class Payment {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PAID = "PAID";

    public String id;
    public String customerId;
    public String monthYear;
    public double totalAmount;
    public double totalMilk;
    public String status;
    public long paidAt;
    public long createdAt;
    public long updatedAt;

    public Payment() {}

    public Payment(String id, String customerId, String monthYear,
                   double totalAmount, double totalMilk, String status, long paidAt,
                   long createdAt, long updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.monthYear = monthYear;
        this.totalAmount = totalAmount;
        this.totalMilk = totalMilk;
        this.status = status;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
