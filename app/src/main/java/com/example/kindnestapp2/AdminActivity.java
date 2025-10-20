package com.example.kindnestapp2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.card.MaterialCardView;

public class AdminActivity extends AppCompatActivity {

    private MaterialCardView manageUsersCard, manageDonationsCard, acknowledgedDonationsCard, logoutCard;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mAuth = FirebaseAuth.getInstance();

        manageUsersCard = findViewById(R.id.manageUsersCard);
        manageDonationsCard = findViewById(R.id.manageDonationsCard);
        acknowledgedDonationsCard = findViewById(R.id.acknowledgedDonationsCard);
        logoutCard = findViewById(R.id.logoutCard);

        manageUsersCard.setOnClickListener(v ->
                startActivity(new Intent(AdminActivity.this, UserManagementActivity.class))
        );

        manageDonationsCard.setOnClickListener(v ->
                startActivity(new Intent(AdminActivity.this, DonationManagementActivity.class))
        );

        // Open acknowledged donations
        acknowledgedDonationsCard.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AcknowledgedDonationsActivity.class);
            intent.putExtra("IS_ADMIN", true); // important!
            startActivity(intent);
        });


        logoutCard.setOnClickListener(v -> {
            mAuth.signOut();
            new AdminSessionManager(this).logoutAdmin();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
