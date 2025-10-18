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
import java.util.List;

public class AcknowledgedDonationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AcknowledgedDonationAdapter adapter;
    private List<Donation> donationList;
    private DatabaseReference donationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acknowledged_donations);

        recyclerView = findViewById(R.id.recyclerViewAcknowledged);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        donationList = new ArrayList<>();
        adapter = new AcknowledgedDonationAdapter(donationList);
        recyclerView.setAdapter(adapter);

        donationsRef = FirebaseDatabase.getInstance().getReference("donations");
        loadAcknowledgedDonations();
    }

    private void loadAcknowledgedDonations() {
        donationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                donationList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Donation donation = ds.getValue(Donation.class);
                    if (donation != null && "acknowledged".equalsIgnoreCase(donation.getStatus())) {
                        donation.setId(ds.getKey());
                        donationList.add(donation);
                    }
                }

                adapter.notifyDataSetChanged();

                if (donationList.isEmpty()) {
                    Toast.makeText(AcknowledgedDonationsActivity.this,
                            "No acknowledged donations found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AcknowledgedDonationsActivity.this,
                        "Error loading donations: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
