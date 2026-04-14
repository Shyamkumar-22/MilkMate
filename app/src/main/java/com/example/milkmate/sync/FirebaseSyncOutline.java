package com.example.milkmate.sync;

import com.example.milkmate.data.local.dao.CustomerDao;
import com.example.milkmate.data.local.dao.MilkEntryDao;
import com.example.milkmate.data.local.dao.MilkRateDao;
import com.example.milkmate.data.local.dao.PaymentDao;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Firebase Firestore sync outline for MilkMate.
 *
 * Architecture:
 * - Room = source of truth on device (offline-first).
 * - Firestore = cloud backup, multi-device, customer dashboard.
 *
 * Sync flow:
 * 1. PUSH (Milkman device → Firestore):
 *    - On data change: batch unsynced entities, map to Firestore docs, write.
 *    - Collections: customers, milk_entries, milk_rates, payments.
 *    - Use firestoreId on entities to track sync status.
 *
 * 2. PULL (optional, for conflict resolution):
 *    - Periodic snapshot listener on /milkmen/{userId}/... for real-time updates.
 *
 * 3. Customer dashboard reads directly from Firestore (customer's device has no Room data).
 */
public class FirebaseSyncOutline {

    private final CustomerDao customerDao;
    private final MilkEntryDao milkEntryDao;
    private final MilkRateDao milkRateDao;
    private final PaymentDao paymentDao;
    private final FirebaseFirestore firestore;

    public FirebaseSyncOutline(CustomerDao customerDao, MilkEntryDao milkEntryDao,
                               MilkRateDao milkRateDao, PaymentDao paymentDao,
                               FirebaseFirestore firestore) {
        this.customerDao = customerDao;
        this.milkEntryDao = milkEntryDao;
        this.milkRateDao = milkRateDao;
        this.paymentDao = paymentDao;
        this.firestore = firestore;
    }

    /**
     * Push unsynced milk entries to Firestore.
     * Call from WorkManager or after local save.
     */
    public void pushMilkEntries(long sinceTimestamp) {
        // List<MilkEntryEntity> unsynced = milkEntryDao.getUnsyncedEntries(sinceTimestamp);
        // For each: map to Firestore doc, set firestoreId on success, update local.
        // firestore.collection("milk_entries").document(docId).set(data)
        //   .addOnSuccessListener(() -> updateLocalFirestoreId(...));
    }

    /**
     * Map Room entity to Firestore document.
     */
    private Map<String, Object> milkEntryToMap(com.example.milkmate.data.local.entity.MilkEntryEntity e) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", e.id);
        m.put("customerId", e.customerId);
        m.put("date", e.date);
        m.put("morningQty", e.morningQty);
        m.put("eveningQty", e.eveningQty);
        m.put("rateAtTime", e.rateAtTime);
        m.put("milkType", e.milkType != null ? e.milkType : "Cow");
        m.put("isDelivered", e.isDelivered);
        m.put("updatedAt", e.updatedAt);
        return m;
    }
}
