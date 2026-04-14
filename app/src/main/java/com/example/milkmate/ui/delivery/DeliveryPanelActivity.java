package com.example.milkmate.ui.delivery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkmate.DateUtils;
import com.example.milkmate.R;
import com.example.milkmate.ui.milkentry.ChatDeliveryActivity;
import com.example.milkmate.viewmodel.DeliveryPanelViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Daily Delivery Panel - Chat-style house list.
 * Houses sorted by house number. Click to open Chat-Style Delivery Screen.
 */
@AndroidEntryPoint
public class DeliveryPanelActivity extends AppCompatActivity {

    private DeliveryPanelViewModel viewModel;
    private DeliveryPanelAdapter adapter;
    private String userId;
    private TextView tvDate, tvTodayRate, tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_panel);

        userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            finish();
            return;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        tvDate = findViewById(R.id.tvDate);
        tvTodayRate = findViewById(R.id.tvTodayRate);
        RecyclerView recycler = findViewById(R.id.recyclerHouses);
        tvEmpty = findViewById(R.id.tvEmpty);

        String today = DateUtils.today();
        tvDate.setText(today);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeliveryPanelAdapter(this::onHouseClick);
        recycler.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(DeliveryPanelViewModel.class);
        viewModel.load(userId);
        viewModel.getDeliveryItems().observe(this, this::onItemsLoaded);
    }

    private void onItemsLoaded(List<com.example.milkmate.domain.model.DeliveryHouseItem> items) {
        if (items == null || items.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            adapter.submitList(items);
        }
    }

    private void onHouseClick(com.example.milkmate.domain.model.DeliveryHouseItem item) {
        Intent i = new Intent(this, ChatDeliveryActivity.class);
        i.putExtra("customerId", item.customer.id);
        i.putExtra("customerName", item.customer.name);
        i.putExtra("houseNumber", item.customer.houseNumber);
        startActivity(i);
    }
}
