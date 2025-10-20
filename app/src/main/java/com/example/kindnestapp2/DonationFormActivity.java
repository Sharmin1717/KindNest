package com.example.kindnestapp2;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DonationFormActivity extends AppCompatActivity {

    private EditText amountEt, mobileEt, purposeEt;
    private LinearLayout dummyBkashBtn, dummyNagadBtn, dummyRocketBtn;
    private TextView titleTextView;
    private WebView paymentWebView;

    private FirebaseAuth mAuth;
    private DatabaseReference donationsRef;

    private String ngoName, subCategory;
    private double amount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_form);

        mAuth = FirebaseAuth.getInstance();
        donationsRef = FirebaseDatabase.getInstance().getReference("donations");

        amountEt = findViewById(R.id.amountEt);
        mobileEt = findViewById(R.id.mobileEt);
        purposeEt = findViewById(R.id.purposeEt);
        dummyBkashBtn = findViewById(R.id.dummyBkashBtn);
        dummyNagadBtn = findViewById(R.id.dummyNagadBtn);
        dummyRocketBtn = findViewById(R.id.dummyRocketBtn);
        paymentWebView = findViewById(R.id.paymentWebView);
        titleTextView = findViewById(R.id.titleTextView);

        ngoName = getIntent().getStringExtra("NGO_NAME");
        subCategory = getIntent().getStringExtra("SUBCATEGORY");
        amount = getIntent().getDoubleExtra("AMOUNT", 0);

        titleTextView.setText("Donate to " + ngoName);
        amountEt.setText(String.valueOf(amount));
        purposeEt.setText(subCategory);

        fetchUserPhoneNumber();

        View.OnClickListener clickListener = v -> handleDonation();
        findViewById(R.id.payBtn).setOnClickListener(clickListener);
        dummyBkashBtn.setOnClickListener(clickListener);
        dummyNagadBtn.setOnClickListener(clickListener);
        dummyRocketBtn.setOnClickListener(clickListener);
    }

    private void fetchUserPhoneNumber() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    String phone = snapshot.child("phone").getValue(String.class);
                    if (phone != null && !phone.isEmpty()) {
                        mobileEt.setText(phone);
                        mobileEt.setEnabled(false);
                    }
                }
            });
        }
    }

    private void handleDonation() {
        String enteredAmount = amountEt.getText().toString().trim();
        String mobile = mobileEt.getText().toString().trim();
        String purpose = purposeEt.getText().toString().trim();

        if (enteredAmount.isEmpty() || mobile.isEmpty() || purpose.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save donation with status "Pending"
        saveDonationToFirebase(Double.parseDouble(enteredAmount), purpose, "TXN_" + System.currentTimeMillis());
        Toast.makeText(this, "Donation Successful 💙", Toast.LENGTH_LONG).show();
    }

    private void saveDonationToFirebase(double amount, String purpose, String transactionId) {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "anonymous";

        String donationId = donationsRef.push().getKey();
        if (donationId != null) {
            Donation donation = new Donation(
                    ngoName,
                    purpose,
                    amount,
                    System.currentTimeMillis(),
                    userId,
                    transactionId,
                    subCategory
            );
            donation.setId(donationId);
            donation.setStatus("pending"); // Default status

            donationsRef.child(donationId).setValue(donation)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "Donation recorded successfully", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }
}
