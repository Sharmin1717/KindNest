package com.example.kindnestapp2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");

        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login_button);
        TextView signupLink = findViewById(R.id.signup_link);
        Button continueAsAdminButton = findViewById(R.id.continueAsAdminButton);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            // Email is verified
                            userRef.child(user.getUid()).get().addOnSuccessListener(snapshot -> {
                                if (snapshot.exists()) {
                                    String role = snapshot.child("role").getValue(String.class);
                                    if ("admin".equals(role)) {
                                        startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                    } else {
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    }
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "User role not found!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Email not verified
                            Toast.makeText(LoginActivity.this,
                                    "Please verify your email before logging in.",
                                    Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Authentication failed. " +
                            task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        signupLink.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
        continueAsAdminButton.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, AdminLoginActivity.class)));
    }
}
