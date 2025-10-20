package com.example.kindnestapp2;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DonationManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminDonationAdapter adapter;
    private List<Donation> donationList;
    private DatabaseReference donationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_management);

        donationsRef = FirebaseDatabase.getInstance().getReference("donations");
        recyclerView = findViewById(R.id.recyclerViewDonations);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        donationList = new ArrayList<>();

        adapter = new AdminDonationAdapter(donationList, donation -> acknowledgeDonation(donation));
        recyclerView.setAdapter(adapter);

        loadDonations();
    }

    private void loadDonations() {
        donationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                donationList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Donation donation = ds.getValue(Donation.class);
                    if (donation != null) {
                        donation.setId(ds.getKey());
                        donationList.add(donation);
                    }
                }
                adapter.notifyDataSetChanged();

                if (donationList.isEmpty()) {
                    Toast.makeText(DonationManagementActivity.this,
                            "No donations found",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DonationManagementActivity.this,
                        "Error loading donations: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void acknowledgeDonation(Donation donation) {
        if (donation.getId() == null) return;

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "acknowledged");
        updates.put("seenByUser", false);

        donationsRef.child(donation.getId())
                .updateChildren(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(this,
                        "Donation acknowledged ✅", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Failed to acknowledge donation: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }
}
