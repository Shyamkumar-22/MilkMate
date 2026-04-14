package com.example.milkmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.milkmate.data.local.entity.MilkEntryEntity;
import com.example.milkmate.ui.customer.AddEditCustomerActivity;
import com.example.milkmate.ui.customer.CustomerListActivity;
import com.example.milkmate.ui.delivery.DeliveryPanelActivity;
import com.example.milkmate.viewmodel.MilkmanDashboardViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Professional Milkman Dashboard.
 * Summary cards, AI prediction, quick actions.
 */
@AndroidEntryPoint
public class MilkmanDashboardActivity extends AppCompatActivity {

    private MilkmanDashboardViewModel viewModel;
    private String userId;
    private TextView tvCustomerCount, tvTodayMilk, tvPrediction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milkman_dashboard);

        userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            finish();
            return;
        }

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewModel = new ViewModelProvider(this).get(MilkmanDashboardViewModel.class);

        tvCustomerCount = findViewById(R.id.tvCustomerCount);
        tvTodayMilk = findViewById(R.id.tvTodayMilk);
        tvPrediction = findViewById(R.id.tvPrediction);

        viewModel.getActiveCustomerCount(userId).observe(this, count -> {
            tvCustomerCount.setText(String.valueOf(count != null ? count : 0));
        });

        viewModel.getTodayEntries(userId).observe(this, entries -> {
            double total = 0;
            if (entries != null) {
                for (MilkEntryEntity e : entries) total += e.morningQty + e.eveningQty;
            }
            tvTodayMilk.setText(String.format("%.1f L", total));
        });

        new Thread(() -> {
            String pred = viewModel.getPredictionMessage(userId);
            runOnUiThread(() -> tvPrediction.setText(pred));
        }).start();

        MaterialCardView cardCustomers = findViewById(R.id.cardCustomers);
        MaterialCardView cardMilkEntry = findViewById(R.id.cardMilkEntry);
        MaterialCardView cardHouses = findViewById(R.id.cardHouses);
        MaterialCardView cardSettings = findViewById(R.id.cardSettings);

        cardCustomers.setOnClickListener(v -> startActivity(new Intent(this, CustomerListActivity.class)));
        cardMilkEntry.setOnClickListener(v -> startActivity(new Intent(this, DeliveryPanelActivity.class)));
        cardHouses.setOnClickListener(v -> loadHousesLegacy());
        cardSettings.setOnClickListener(v -> Toast.makeText(this, "Settings coming soon", Toast.LENGTH_SHORT).show());
    }

    private void loadHousesLegacy() {
        Toast.makeText(this, "Houses - use Customers for new entries", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            showLogoutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Do you want to logout?")
                .setPositiveButton("Yes", (d, w) -> logout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
