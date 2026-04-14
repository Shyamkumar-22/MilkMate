package com.example.milkmate.ui.milkentry;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.milkmate.DateUtils;
import com.example.milkmate.R;
import com.example.milkmate.data.repository.MilkEntryRepository;
import com.example.milkmate.viewmodel.MilkEntryViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Daily milk entry - morning + evening quantity.
 * rateAtTime is auto-fetched from MilkRate for the date.
 */
@AndroidEntryPoint
public class MilkEntryActivity extends AppCompatActivity {

    private MilkEntryViewModel viewModel;
    private String customerId;
    private String customerName;
    private EditText etMorning, etEvening;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milk_entry);

        customerId = getIntent().getStringExtra("customerId");
        customerName = getIntent().getStringExtra("customerName");
        if (customerId == null) {
            Toast.makeText(this, "Customer required", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(MilkEntryViewModel.class);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tvTitle = findViewById(R.id.tvCustomerTitle);
        tvTitle.setText("Milk Entry" + (customerName != null ? " - " + customerName : ""));

        TextView tvDate = findViewById(R.id.tvDate);
        String today = DateUtils.today();
        tvDate.setText(today);

        etMorning = findViewById(R.id.etMorning);
        etEvening = findViewById(R.id.etEvening);
        MaterialButton btnSave = findViewById(R.id.btnSave);

        viewModel.getByCustomerAndDate(customerId, today).observe(this, entry -> {
            if (entry != null) {
                etMorning.setText(String.valueOf(entry.morningQty));
                etEvening.setText(String.valueOf(entry.eveningQty));
            } else {
                etMorning.setText("");
                etEvening.setText("");
            }
        });

        btnSave.setOnClickListener(v -> save());
    }

    private void save() {
        String mornStr = etMorning.getText().toString().trim();
        String eveStr = etEvening.getText().toString().trim();

        double morning = 0;
        double evening = 0;
        try {
            if (!mornStr.isEmpty()) morning = Double.parseDouble(mornStr);
            if (!eveStr.isEmpty()) evening = Double.parseDouble(eveStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        if (morning <= 0 && evening <= 0) {
            Toast.makeText(this, "Enter at least one quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        String today = DateUtils.today();
        viewModel.saveMilkEntry(customerId, today, morning, evening, "Cow", null, id -> {
            Toast.makeText(this, "Entry saved", Toast.LENGTH_SHORT).show();
        });
    }
}
