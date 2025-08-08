package com.example.kindnestapp2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminLoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String ADMIN_EMAIL = "admin@kindnest.com"; // Admin email to validate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.passcode); // Change hint in XML to "Password"
        Button loginButton = findViewById(R.id.login_button);

        // Pre-fill admin email and disable editing
        emailEditText.setText(ADMIN_EMAIL);
        emailEditText.setEnabled(false);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(AdminLoginActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!ADMIN_EMAIL.equals(email)) {
                Toast.makeText(AdminLoginActivity.this, "This login is for administrators only.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Authenticate admin via Firebase Auth
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AdminLoginActivity.this, "Admin Login Successful.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AdminLoginActivity.this, AdminActivity.class));
                            finish();
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Authentication failed.";
                            Toast.makeText(AdminLoginActivity.this, "Login Failed: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
