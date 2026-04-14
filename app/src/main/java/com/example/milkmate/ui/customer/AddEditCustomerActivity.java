package com.example.milkmate.ui.customer;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.milkmate.R;
import com.example.milkmate.data.repository.CustomerRepository;
import com.example.milkmate.domain.model.Customer;
import com.example.milkmate.domain.model.MilkRate;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Add or edit customer. Fields: name, phone, address, area, milkType, isActive.
 */
@AndroidEntryPoint
public class AddEditCustomerActivity extends AppCompatActivity {

    @Inject
    CustomerRepository customerRepo;

    private EditText etHouseNumber, etName, etPhone, etAddress, etArea;
    private Spinner spinnerMilkType;
    private Button btnSave;
    private String userId;
    private String editId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_customer);

        userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            finish();
            return;
        }

        editId = getIntent().getStringExtra("customerId");

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(editId != null ? "Edit Customer" : "Add Customer");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        etHouseNumber = findViewById(R.id.etHouseNumber);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etArea = findViewById(R.id.etArea);
        spinnerMilkType = findViewById(R.id.spinnerMilkType);
        btnSave = findViewById(R.id.btnSave);

        String[] milkTypes = {MilkRate.TYPE_COW, MilkRate.TYPE_BUFFALO, MilkRate.TYPE_TONED};
        spinnerMilkType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, milkTypes));

        if (editId != null) {
            customerRepo.getById(editId).observe(this, customer -> {
                if (customer != null) {
                    etHouseNumber.setText(customer.houseNumber);
                    etName.setText(customer.name);
                    etPhone.setText(customer.phone);
                    etAddress.setText(customer.address);
                    etArea.setText(customer.area);
                    if (customer.milkType != null) {
                        for (int i = 0; i < milkTypes.length; i++) {
                            if (milkTypes[i].equals(customer.milkType)) {
                                spinnerMilkType.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            });
        }

        btnSave.setOnClickListener(v -> save());
    }

    private void save() {
        String houseNumber = etHouseNumber.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String area = etArea.getText().toString().trim();
        String milkType = (String) spinnerMilkType.getSelectedItem();

        if (name.isEmpty()) {
            Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.isEmpty()) {
            Toast.makeText(this, "Enter phone", Toast.LENGTH_SHORT).show();
            return;
        }

        long now = System.currentTimeMillis();
        Customer c = new Customer();
        c.id = editId;
        c.houseNumber = houseNumber != null ? houseNumber : "";
        c.name = name;
        c.phone = phone;
        c.address = address;
        c.area = area;
        c.milkType = milkType != null ? milkType : MilkRate.TYPE_COW;
        c.isActive = true;
        c.userId = userId;
        c.createdAt = now;
        c.updatedAt = now;

        CustomerRepository.OnResult listener = id -> {
            Toast.makeText(this, "Customer saved", Toast.LENGTH_SHORT).show();
            finish();
        };

        if (editId != null) {
            customerRepo.update(c, listener);
        } else {
            customerRepo.insert(c, listener);
        }
    }
}
