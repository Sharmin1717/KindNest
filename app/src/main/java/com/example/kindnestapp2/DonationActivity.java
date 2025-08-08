// CORRECTED and DYNAMIC DonationActivity.java

package com.example.kindnestapp2;

import android.os.Bundle;
import android.widget.Toast;
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
    private NGO selectedNgo; // The NGO object received from the previous screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

        // --- FIX #1: Get the selected NGO object from the Intent ---
        // The key "NGO_OBJECT" matches what you send from MainActivity
        selectedNgo = (NGO) getIntent().getSerializableExtra("NGO_OBJECT");

        // --- CRITICAL: Check if data was received correctly ---
        if (selectedNgo == null) {
            Toast.makeText(this, "Could not load NGO data. Please try again.", Toast.LENGTH_LONG).show();
            finish(); // Close the activity if data is missing
            return;
        }
        ngoName = selectedNgo.getName();

        // --- Setup the Toolbar ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Donate to " + ngoName); // Set title dynamically
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // --- Setup the RecyclerView ---
        recyclerView = findViewById(R.id.donation_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // --- Prepare data dynamically and setup the adapter ---
        setupDynamicCategoryList();
    }

    private void setupDynamicCategoryList() {
        displayList = new ArrayList<>();
        // --- FIX #2: Get categories from the REAL NGO object, not hardcoded ones ---
        Map<String, Map<String, Boolean>> categories = selectedNgo.getCategories();

        if (categories == null || categories.isEmpty()) {
            Toast.makeText(this, "This NGO has no donation categories listed.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Loop through the NGO's actual categories from Firebase
        for (Map.Entry<String, Map<String, Boolean>> categoryEntry : categories.entrySet()) {
            // Add category title (e.g., "Health")
            displayList.add(categoryEntry.getKey());

            Map<String, Boolean> subCategories = categoryEntry.getValue();
            if (subCategories != null) {
                // Add all its subcategories (e.g., "Medicine Fund", "Hospital Equipment")
                for (String subCategoryName : subCategories.keySet()) {
                    displayList.add(new DonationAdapter.SubCategoryItem(subCategoryName));
                }
            }
        }

        // Create the adapter with the dynamically built list
        donationAdapter = new DonationAdapter(this, displayList, ngoName);
        recyclerView.setAdapter(donationAdapter);
    }

    public void initiatePayment(DonationAdapter.SubCategoryItem item, double amount) {
        String message = "SUCCESS: Preparing to donate BDT " + amount +
                " for '" + item.name + "' to " + ngoName;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}