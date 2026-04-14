package com.example.milkmate;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etHouse, etEmail, etPassword;
    Spinner spinnerRole;
    Button btnRegister;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etHouse = findViewById(R.id.etHouse);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnRegister = findViewById(R.id.btnRegister);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.user_roles,
                        android.R.layout.simple_spinner_item
                );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String name = etName.getText().toString().trim();
        String houseNumber = etHouse.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString(); // User / Milkman

        if (name.isEmpty() || houseNumber.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    String uid = authResult.getUser().getUid();

                    // 🔹 USER DOCUMENT
                    Map<String, Object> user = new HashMap<>();
                    user.put("uid", uid);
                    user.put("name", name);
                    user.put("email", email);
                    user.put("houseNumber", houseNumber);
                    user.put("role", role);
                    user.put("isActive", true);
                    user.put("createdAt", System.currentTimeMillis());

                    db.collection("users").document(uid).set(user);

                    // 🔹 AUTO HOUSE CREATION (ONLY FOR USER)
                    if (role.equalsIgnoreCase("User")) {

                        Map<String, Object> house = new HashMap<>();
                        house.put("houseNumber", houseNumber);
                        house.put("ownerName", name);
                        house.put("userId", uid);
                        house.put("isActive", true);
                        house.put("createdAt", System.currentTimeMillis());

                        db.collection("houses")
                                .document(uid) // SAME UID
                                .set(house)
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(this,
                                                "House created automatically",
                                                Toast.LENGTH_SHORT).show()
                                );
                    }

                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
