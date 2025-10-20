package com.example.kindnestapp2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminLoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String ADMIN_EMAIL = "admin@kindnest.com"; //actual firebase admin email
    private EditText emailEditText, passwordEditText;
    private Button loginButton;

    private static final String PREF_NAME = "AdminPrefs";
    private static final String KEY_IS_LOGGED_IN = "isAdminLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.passcode);
        loginButton = findViewById(R.id.login_button);

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);


        if (isLoggedIn && mAuth.getCurrentUser() != null) {
            startActivity(new Intent(AdminLoginActivity.this, AdminActivity.class));
            finish();
            return;
        }


        emailEditText.setText(ADMIN_EMAIL);
        emailEditText.setEnabled(false);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            //authentication admin
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && ADMIN_EMAIL.equals(user.getEmail())) {
                                // Save session
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean(KEY_IS_LOGGED_IN, true);
                                editor.apply();

                                Toast.makeText(this, "Admin Login Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, AdminActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Unauthorized account", Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                            }
                        } else {
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() : "Authentication failed.";
                            Toast.makeText(this, "Login Failed: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
