package com.example.kindnestapp2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.ssl.commerz.SSLCommerz;
import com.ssl.commerz.TransactionResponseValidator;
import com.ssl.commerz.Utility.ParameterBuilder;

import java.util.HashMap;
import java.util.Map;

public class DonationFormActivity extends AppCompatActivity {

    private EditText amountEt, mobileEt, purposeEt;
    private Button payBtn;
    private WebView paymentWebView;

    private static final String STORE_ID = "kindn689cad3c0e549";
    private static final String STORE_PASSWORD = "kindn689cad3c0e549@ssl";
    private static final boolean TEST_MODE = true;

    private static final String SUCCESS_URL = "https://yourdomain.com/success";
    private static final String FAIL_URL = "https://yourdomain.com/fail";
    private static final String CANCEL_URL = "https://yourdomain.com/cancel";

    private String ngoName, subCategory;
    private double amount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_form);

        amountEt = findViewById(R.id.amountEt);
        mobileEt = findViewById(R.id.mobileEt);
        purposeEt = findViewById(R.id.purposeEt);
        payBtn = findViewById(R.id.payBtn);
        paymentWebView = findViewById(R.id.paymentWebView);
        paymentWebView.getSettings().setJavaScriptEnabled(true);

        // --- Receive data from intent ---
        ngoName = getIntent().getStringExtra("NGO_NAME");
        subCategory = getIntent().getStringExtra("SUBCATEGORY");
        amount = getIntent().getDoubleExtra("AMOUNT", 0);

        // Prefill fields
        amountEt.setText(String.valueOf(amount));
        purposeEt.setText(subCategory);
        setTitle("Donate to " + ngoName + " - " + subCategory);

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
        new Thread(() -> {
            try {
                Map<String, String> postData = ParameterBuilder.constructRequestParameters();
                postData.put("total_amount", amountStr);
                postData.put("cus_phone", mobile);
                postData.put("value_a", purpose);
                postData.put("success_url", SUCCESS_URL);
                postData.put("fail_url", FAIL_URL);
                postData.put("cancel_url", CANCEL_URL);

                SSLCommerz sslcz = new SSLCommerz(STORE_ID, STORE_PASSWORD, TEST_MODE);
                String paymentUrl = sslcz.initiateTransaction(postData, false);

                // ✅ Convert amountStr to double before passing
                double amountDouble = Double.parseDouble(amountStr);

                runOnUiThread(() -> openWebView(paymentUrl, amountDouble));

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Payment init failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void openWebView(String url, double amount) {
        paymentWebView.setVisibility(View.VISIBLE); // ✅ Make WebView visible
        paymentWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String redirectUrl = request.getUrl().toString();

                if (redirectUrl.startsWith(SUCCESS_URL)) {
                    handleSuccess(redirectUrl, amount);
                    return true;
                } else if (redirectUrl.startsWith(FAIL_URL)) {
                    Toast.makeText(DonationFormActivity.this, "Payment Failed", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (redirectUrl.startsWith(CANCEL_URL)) {
                    Toast.makeText(DonationFormActivity.this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        paymentWebView.loadUrl(url);
    }

    private void handleSuccess(String redirectUrl, double amount) {
        Map<String, String> paramsMap = new HashMap<>();
        String[] parts = redirectUrl.split("\\?");
        if (parts.length > 1) {
            String[] params = parts[1].split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length > 1) paramsMap.put(keyValue[0], keyValue[1]);
            }
        }

        try {
            TransactionResponseValidator validator = new TransactionResponseValidator();
            boolean valid = validator.receiveSuccessResponse(paramsMap, amount); // pass dynamic amount

            if (valid) {
                Toast.makeText(this, "Donation successful!", Toast.LENGTH_LONG).show();

                String transactionId = paramsMap.get("tran_id");
                Donation donation = new Donation(
                        ngoName,
                        subCategory,
                        amount,
                        System.currentTimeMillis(),
                        FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        transactionId
                );

                FirebaseDatabase.getInstance().getReference("donations")
                        .push().setValue(donation)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Donation saved successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Failed to save donation!", Toast.LENGTH_SHORT).show();
                            }
                        });

            } else {
                Toast.makeText(this, "Payment validation failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error validating payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
