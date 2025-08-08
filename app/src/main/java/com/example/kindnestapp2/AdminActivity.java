package com.example.kindnestapp2;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    private Button viewPendingButton, viewApprovedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        viewPendingButton = findViewById(R.id.viewPendingButton);
        viewApprovedButton = findViewById(R.id.viewApprovedButton);

        // Open Pending Requests Activity
        viewPendingButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, PendingRequestsActivity.class);
            startActivity(intent);
        });

        // Open Approved Donations Activity
        viewApprovedButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, ApprovedDonationsActivity.class);
            startActivity(intent);
        });
    }
}
