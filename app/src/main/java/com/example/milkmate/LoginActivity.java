package com.example.milkmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin, btnRegister;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        btnLogin.setOnClickListener(v -> loginUser());
    }

    // 🔥 THIS IS THE KEY PART
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            redirectUser(user.getUid());
        }
    }

    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult ->
                        redirectUser(authResult.getUser().getUid())
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void redirectUser(String uid) {

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) return;

                    String role = doc.getString("role");

                    if ("Milk Man".equalsIgnoreCase(role)) {
                        startActivity(new Intent(this, MilkmanDashboardActivity.class));
                    } else {
                        startActivity(new Intent(this, UserDashboardActivity.class));
                    }

                    finish(); // 🔥 Prevent back to login
                });
    }
}
