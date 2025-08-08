// src/main/java/com/example/kindnestapp2/ProfileActivity.java

package com.example.kindnestapp2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, phoneEditText, passwordEditText;
    private Button editProfileButton, paymentMethodButton, donationHistoryButton;
    private ImageView backButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Bind all views from the XML
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText); // Bind the password field
        editProfileButton = findViewById(R.id.editProfileButton);
        paymentMethodButton = findViewById(R.id.paymentMethodButton);
        donationHistoryButton = findViewById(R.id.donationHistoryButton);
        backButton = findViewById(R.id.backButton);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // No user is signed in, go back to login
            Toast.makeText(this, "Please log in again.", Toast.LENGTH_SHORT).show();
            // Optional: startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        currentUserId = currentUser.getUid();

        loadUserProfile();

        // Back Button Click Listener
        backButton.setOnClickListener(v -> finish());

        // Edit Profile Button Click Listener
        editProfileButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();

            if (!name.isEmpty() && !email.isEmpty() && !phone.isEmpty()) {
                updateProfile(name, email, phone);
            } else {
                Toast.makeText(ProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        // FIXED: This button now goes to the main NGO list to start a donation
        paymentMethodButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Donation History Button Click Listener
        donationHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, DonationHistoryActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserProfile() {
        mDatabase.child("users").child(currentUserId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        nameEditText.setText(user.getUsername());
                        emailEditText.setText(user.getEmail());
                        phoneEditText.setText(user.getPhone());
                    }
                }
            } else {
                Toast.makeText(ProfileActivity.this, "Failed to load profile data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile(String name, String email, String phone) {
        // Create updated User object
        User updatedUser = new User(name, email, phone, "user");

        // Update the user data in Firebase
        mDatabase.child("users").child(currentUserId).setValue(updatedUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Profile update failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}