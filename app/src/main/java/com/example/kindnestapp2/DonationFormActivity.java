// ✅ Step 1: DonationFormActivity.java
package com.example.kindnestapp2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class DonationFormActivity extends AppCompatActivity {

    EditText amountEt, phoneEt, transactionIdEt;
    RadioGroup methodGroup;
    Button submitBtn;
    ProgressDialog progressDialog;

    FirebaseAuth auth;
    DatabaseReference donationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_form);

        amountEt = findViewById(R.id.editTextAmount);
        phoneEt = findViewById(R.id.editTextPhone);
        transactionIdEt = findViewById(R.id.editTextTransactionId);
        methodGroup = findViewById(R.id.radioGroupMethod);
        submitBtn = findViewById(R.id.buttonSubmit);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting donation...");

        auth = FirebaseAuth.getInstance();
        donationRef = FirebaseDatabase.getInstance().getReference("Donations");

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitDonation();
            }
        });
    }

    private void submitDonation() {
        String amount = amountEt.getText().toString().trim();
        String phone = phoneEt.getText().toString().trim();
        String trxId = transactionIdEt.getText().toString().trim();
        int selectedMethodId = methodGroup.getCheckedRadioButtonId();

        if (TextUtils.isEmpty(amount) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(trxId) || selectedMethodId == -1) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedMethod = findViewById(selectedMethodId);
        String method = selectedMethod.getText().toString();

        progressDialog.show();

        String donationId = donationRef.push().getKey();
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        String userId = auth.getCurrentUser().getUid();

        HashMap<String, Object> donationMap = new HashMap<>();
        donationMap.put("id", donationId);
        donationMap.put("userId", userId);
        donationMap.put("amount", amount);
        donationMap.put("phone", phone);
        donationMap.put("transactionId", trxId);
        donationMap.put("method", method);
        donationMap.put("status", "pending");
        donationMap.put("date", date);

        donationRef.child(donationId).setValue(donationMap)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        showSuccessDialog();
                    } else {
                        Toast.makeText(this, "Submission failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Thank You")
                .setMessage("Your donation was submitted successfully and is pending approval.")
                .setPositiveButton("OK", (dialog, which) -> finish())
                .show();
    }
}