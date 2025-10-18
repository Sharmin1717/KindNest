package com.example.kindnestapp2;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DonationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DonationAdapter donationAdapter;
    private List<Object> displayList;
    private String ngoName;
    private NGO selectedNgo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

        selectedNgo = (NGO) getIntent().getSerializableExtra("NGO_OBJECT");
        if (selectedNgo == null) {
            Toast.makeText(this, "Could not load NGO data.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        ngoName = selectedNgo.getName();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Donate to " + ngoName);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.donation_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupDynamicCategoryList();
    }

    private void setupDynamicCategoryList() {
        displayList = new ArrayList<>();
        Map<String, Map<String, Boolean>> categories = selectedNgo.getCategories();

        if (categories == null || categories.isEmpty()) {
            Toast.makeText(this, "This NGO has no donation categories listed.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Populate displayList with categories and subcategories
        for (Map.Entry<String, Map<String, Boolean>> categoryEntry : categories.entrySet()) {
            displayList.add(categoryEntry.getKey());
            Map<String, Boolean> subCategories = categoryEntry.getValue();
            if (subCategories != null) {
                for (String subCategoryName : subCategories.keySet()) {
                    displayList.add(new DonationAdapter.SubCategoryItem(subCategoryName));
                }
            }
        }

        // Fix: pass ngoName as first argument
        donationAdapter = new DonationAdapter(ngoName, displayList, item -> showAmountDialog(item));
        recyclerView.setAdapter(donationAdapter);
    }

    private void showAmountDialog(DonationAdapter.SubCategoryItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Donate for: " + item.name.replace('_', ' '));
        builder.setMessage("Enter the amount you wish to donate:");

        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Amount in BDT"); // hint shows BDT, input is still numeric only
        builder.setView(input);

        builder.setPositiveButton("Proceed to Pay", (dialog, which) -> {
            String amountStr = input.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                double amount = Double.parseDouble(amountStr); // numeric only
                Intent intent = new Intent(DonationActivity.this, DonationFormActivity.class);
                intent.putExtra("NGO_NAME", ngoName);
                intent.putExtra("SUBCATEGORY", item.name.replace('_', ' '));
                intent.putExtra("AMOUNT", amount);
                startActivity(intent);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

}
