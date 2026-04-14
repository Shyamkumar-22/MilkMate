package com.example.milkmate;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class HouseDetailActivity extends AppCompatActivity {

    FirebaseFirestore db;
    String houseId, houseNumber, userId;
    double selectedLiters = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_detail);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();

        houseId = getIntent().getStringExtra("houseId");
        houseNumber = getIntent().getStringExtra("houseNumber");

        TextView tvHouseTitle = findViewById(R.id.tvHouseTitle);
        tvHouseTitle.setText("House " + houseNumber);

        GridLayout gridMilk = findViewById(R.id.gridMilk);
        Button btnSave = findViewById(R.id.btnSave);

        // Milk option buttons
        for (int i = 0; i < gridMilk.getChildCount(); i++) {
            Button btn = (Button) gridMilk.getChildAt(i);
            btn.setOnClickListener(v -> {
                selectedLiters = Double.parseDouble(
                        btn.getText().toString().replace(" L", "")
                );
                Toast.makeText(this,
                        selectedLiters + " L selected",
                        Toast.LENGTH_SHORT).show();
            });
        }

        btnSave.setOnClickListener(v -> {
            if (selectedLiters == 0) {
                Toast.makeText(this, "Select milk quantity", Toast.LENGTH_SHORT).show();
                return;
            }
            saveMilkEntry();
        });
    }

    // ✅ SINGLE SOURCE OF TRUTH
    private void saveMilkEntry() {

        String todayDate = DateUtils.today();

        // 1️⃣ Get milk price
        db.collection("config")
                .document("milk_settings")
                .get()
                .addOnSuccessListener(priceDoc -> {

                    final double price =
                            priceDoc.getDouble("pricePerLiter") != null
                                    ? priceDoc.getDouble("pricePerLiter")
                                    : 0.0;

                    final double totalAmount = selectedLiters * price;

                    // 2️⃣ Check if already added today
                    db.collection("milk_entries")
                            .whereEqualTo("houseId", houseId)
                            .whereEqualTo("date", todayDate)
                            .get()
                            .addOnSuccessListener(snapshot -> {

                                if (!snapshot.isEmpty()) {
                                    // 🔁 UPDATE
                                    String docId = snapshot.getDocuments().get(0).getId();

                                    db.collection("milk_entries")
                                            .document(docId)
                                            .update(
                                                    "quantity", selectedLiters,
                                                    "totalAmount", totalAmount,
                                                    "timestamp", System.currentTimeMillis()
                                            );

                                    Toast.makeText(this,
                                            "Milk updated for today",
                                            Toast.LENGTH_SHORT).show();

                                } else {
                                    // ➕ CREATE
                                    Map<String, Object> entry = new HashMap<>();
                                    entry.put("userId", userId);
                                    entry.put("houseId", houseId);
                                    entry.put("houseNumber", houseNumber);
                                    entry.put("quantity", selectedLiters);
                                    entry.put("pricePerLiter", price);
                                    entry.put("totalAmount", totalAmount);
                                    entry.put("date", todayDate);
                                    entry.put("timestamp", System.currentTimeMillis());

                                    db.collection("milk_entries")
                                            .add(entry);

                                    Toast.makeText(this,
                                            "Milk added for today",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                });
    }
}
