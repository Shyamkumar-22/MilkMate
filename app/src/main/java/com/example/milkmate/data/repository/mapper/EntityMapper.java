package com.example.milkmate.data.repository.mapper;

import com.example.milkmate.data.local.entity.CustomerEntity;
import com.example.milkmate.data.local.entity.MilkEntryEntity;
import com.example.milkmate.data.local.entity.MilkRateEntity;
import com.example.milkmate.data.local.entity.PaymentEntity;
import com.example.milkmate.domain.model.Customer;
import com.example.milkmate.domain.model.MilkEntry;
import com.example.milkmate.domain.model.MilkRate;
import com.example.milkmate.domain.model.Payment;

/**
 * Entity <-> Domain mappers.
 */
public final class EntityMapper {

    private EntityMapper() {}

    public static Customer toCustomer(CustomerEntity e) {
        if (e == null) return null;
        return new Customer(e.id, e.houseNumber, e.name, e.phone, e.address, e.area, e.milkType,
                e.isActive, e.userId, e.createdAt, e.updatedAt);
    }

    public static CustomerEntity toCustomerEntity(Customer c, String firestoreId) {
        if (c == null) return null;
        long now = System.currentTimeMillis();
        String id = (c.id != null && !c.id.isEmpty()) ? c.id : java.util.UUID.randomUUID().toString();
        return new CustomerEntity(id, c.houseNumber, c.name, c.phone, c.address, c.area, c.milkType,
                c.isActive, c.userId, c.createdAt > 0 ? c.createdAt : now,
                c.updatedAt > 0 ? c.updatedAt : now, firestoreId);
    }

    public static MilkEntry toMilkEntry(MilkEntryEntity e) {
        if (e == null) return null;
        return new MilkEntry(e.id, e.customerId, e.date, e.morningQty, e.eveningQty,
                e.rateAtTime, e.milkType, e.isDelivered, e.skipReason, e.createdAt, e.updatedAt);
    }

    public static MilkEntryEntity toMilkEntryEntity(MilkEntry m, String firestoreId) {
        if (m == null) return null;
        long now = System.currentTimeMillis();
        return new MilkEntryEntity(m.id != null ? m.id : java.util.UUID.randomUUID().toString(),
                m.customerId, m.date, m.morningQty, m.eveningQty, m.rateAtTime, m.milkType,
                m.isDelivered, m.skipReason,
                m.createdAt > 0 ? m.createdAt : now, m.updatedAt > 0 ? m.updatedAt : now, firestoreId);
    }

    public static MilkRate toMilkRate(MilkRateEntity e) {
        if (e == null) return null;
        return new MilkRate(e.id, e.milkType, e.rate, e.effectiveFrom, e.effectiveTo, e.createdAt);
    }

    public static MilkRateEntity toMilkRateEntity(MilkRate r, String firestoreId) {
        if (r == null) return null;
        long now = System.currentTimeMillis();
        return new MilkRateEntity(r.id != null ? r.id : java.util.UUID.randomUUID().toString(),
                r.milkType, r.rate, r.effectiveFrom, r.effectiveTo, r.createdAt > 0 ? r.createdAt : now, firestoreId);
    }

    public static Payment toPayment(PaymentEntity e) {
        if (e == null) return null;
        return new Payment(e.id, e.customerId, e.monthYear, e.totalAmount, e.totalMilk,
                e.status, e.paidAt, e.createdAt, e.updatedAt);
    }

    public static PaymentEntity toPaymentEntity(Payment p, String firestoreId) {
        if (p == null) return null;
        long now = System.currentTimeMillis();
        return new PaymentEntity(p.id != null ? p.id : java.util.UUID.randomUUID().toString(),
                p.customerId, p.monthYear, p.totalAmount, p.totalMilk, p.status, p.paidAt,
                p.createdAt > 0 ? p.createdAt : now, p.updatedAt > 0 ? p.updatedAt : now, firestoreId);
    }
}
