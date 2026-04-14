package com.example.milkmate;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UserHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    HistoryAdapter adapter;
    List<MilkEntry> entryList;

    TextView tvTotalLiters, tvTotalAmount;

    FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);

        userId = FirebaseAuth.getInstance().getUid();
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerHistory);
        tvTotalLiters = findViewById(R.id.tvTotalLiters);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        entryList = new ArrayList<>();
        adapter = new HistoryAdapter(entryList);
        recyclerView.setAdapter(adapter);

        loadMonthlyHistory();
    }

    private void loadMonthlyHistory() {

        String monthPrefix = DateUtils.currentMonthPrefix(); // yyyy-MM

        db.collection("milk_entries")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(snapshot -> {

                    final double[] totalLiters = {0};
                    final double[] totalAmount = {0};

                    entryList.clear();

                    snapshot.forEach(doc -> {

                        String date = doc.getString("date");
                        if (date == null || !date.startsWith(monthPrefix)) return;

                        Double qtyObj = doc.getDouble("quantity");
                        Double amtObj = doc.getDouble("totalAmount");

                        double qty = qtyObj != null ? qtyObj : 0;
                        double amount = amtObj != null ? amtObj : 0;

                        MilkEntry entry = new MilkEntry();
                        entry.date = date;
                        entry.quantity = qty;
                        entry.totalAmount = amount;

                        entryList.add(entry);

                        totalLiters[0] += qty;
                        totalAmount[0] += amount;
                    });

                    adapter.notifyDataSetChanged();

                    tvTotalLiters.setText("Total Milk: " + totalLiters[0] + " L");
                    tvTotalAmount.setText("Total Amount: ₹ " + totalAmount[0]);
                });
    }
}
