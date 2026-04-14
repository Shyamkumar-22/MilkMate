package com.example.milkmate.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkmate.R;
import com.example.milkmate.domain.model.Customer;
import com.example.milkmate.ui.milkentry.ChatDeliveryActivity;
import com.example.milkmate.viewmodel.CustomerListViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Customer list with search. Uses Room (offline-first).
 */
@AndroidEntryPoint
public class CustomerListActivity extends AppCompatActivity {

    private CustomerListViewModel viewModel;
    private CustomerAdapter adapter;
    private List<Customer> allCustomers = new ArrayList<>();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(CustomerListViewModel.class);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        EditText etSearch = findViewById(R.id.etSearch);
        RecyclerView recycler = findViewById(R.id.recyclerCustomers);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomerAdapter(new ArrayList<>(), this::onCustomerClick);
        recycler.setAdapter(adapter);

        viewModel.getActiveByUserId(userId).observe(this, customers -> {
            allCustomers = customers != null ? customers : new ArrayList<>();
            filterAndUpdate(etSearch.getText().toString());
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterAndUpdate(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, AddEditCustomerActivity.class));
        });
    }

    private void filterAndUpdate(String query) {
        String q = query.trim().toLowerCase();
        List<Customer> filtered = new ArrayList<>();
        for (Customer c : allCustomers) {
            if (q.isEmpty() ||
                    (c.name != null && c.name.toLowerCase().contains(q)) ||
                    (c.phone != null && c.phone.contains(q)) ||
                    (c.address != null && c.address.toLowerCase().contains(q)) ||
                    (c.area != null && c.area.toLowerCase().contains(q))) {
                filtered.add(c);
            }
        }
        adapter.updateList(filtered);
    }

    private void onCustomerClick(Customer c) {
        Intent i = new Intent(this, ChatDeliveryActivity.class);
        i.putExtra("customerId", c.id);
        i.putExtra("customerName", c.name);
        startActivity(i);
    }
}
