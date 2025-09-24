package com.example.kindnestapp2;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.help5g.uddoktapaysdk.UddoktaPay;

import java.util.HashMap;
import java.util.Map;

public class DonationFormActivity extends AppCompatActivity {

    private EditText amountEt, mobileEt, purposeEt;
    private Button payBtn;
    private WebView paymentWebView;
    private TextView titleTextView;

    private String ngoName, subCategory;
    private double amount;

    // --- Sandbox credentials ---
    private static final String API_KEY = "982d381360a69d419689740d9f2e26ce36fb7a50";  // Public sandbox key
    private static final String CHECKOUT_URL = "https://sandbox.uddoktapay.com/api/checkout-v2";
    private static final String VERIFY_PAYMENT_URL = "https://sandbox.uddoktapay.com/api/verify-payment";
    private static final String REDIRECT_URL = "https://example.com/success"; // dummy redirect for sandbox
    private static final String CANCEL_URL = "https://example.com/cancel";    // dummy cancel URL

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_form);

        amountEt = findViewById(R.id.amountEt);
        mobileEt = findViewById(R.id.mobileEt);
        purposeEt = findViewById(R.id.purposeEt);
        payBtn = findViewById(R.id.payBtn);
        paymentWebView = findViewById(R.id.paymentWebView);
        titleTextView = findViewById(R.id.titleTextView);

        ngoName = getIntent().getStringExtra("NGO_NAME");
        subCategory = getIntent().getStringExtra("SUBCATEGORY");
        amount = getIntent().getDoubleExtra("AMOUNT", 0);

        titleTextView.setText("Donate to " + ngoName);
        amountEt.setText(String.valueOf(amount));
        purposeEt.setText(subCategory);

        payBtn.setOnClickListener(v -> {
            String enteredAmount = amountEt.getText().toString().trim();
            String mobile = mobileEt.getText().toString().trim();
            String purpose = purposeEt.getText().toString().trim();

            if (enteredAmount.isEmpty() || mobile.isEmpty() || purpose.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            initiatePayment(enteredAmount, mobile, purpose);
        });
    }

    private void initiatePayment(String amountStr, String mobile, String purpose) {
        paymentWebView.setVisibility(View.VISIBLE);

        // --- Metadata (optional) ---
        Map<String, String> metadataMap = new HashMap<>();
        metadataMap.put("donor_mobile", mobile);
        metadataMap.put("donation_purpose", purpose);

        // --- Callback after payment ---
        UddoktaPay.PaymentCallback callback = (status, fullName, userEmail, paidAmount,
                                               invoiceId, paymentMethod, senderNumber,
                                               transactionId, date, metadataValues,
                                               fee, chargeAmount) -> runOnUiThread(() -> {
            if ("COMPLETED".equals(status)) {
                Toast.makeText(DonationFormActivity.this, "Donation Successful! Thank you 💙", Toast.LENGTH_LONG).show();
                paymentWebView.setVisibility(View.GONE);
                finish();
            } else if ("PENDING".equals(status)) {
                Toast.makeText(DonationFormActivity.this, "Donation Pending...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DonationFormActivity.this, "Donation Failed!", Toast.LENGTH_SHORT).show();
                paymentWebView.setVisibility(View.GONE);
            }
        });

        // --- Start UddoktaPay Payment ---
        UddoktaPay uddoktapay = new UddoktaPay(paymentWebView, callback);
        uddoktapay.loadPaymentForm(
                API_KEY,
                ngoName,
                "donor@email.com",  // dummy email for sandbox
                amountStr,
                CHECKOUT_URL,
                VERIFY_PAYMENT_URL,
                REDIRECT_URL,
                CANCEL_URL,
                metadataMap
        );
    }
}
