package com.example.kindnestapp2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class DonationHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DonationHistoryAdapter donationAdapter;
    private List<Donation> donationList;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        donationList = new ArrayList<>();

        donationAdapter = new DonationHistoryAdapter(donationList);

        recyclerView.setAdapter(donationAdapter);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "You need to be logged in to see history.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("donations");

        loadUserDonations();
    }

    private void loadUserDonations() {
        String userId = auth.getCurrentUser().getUid();
        databaseReference.orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        donationList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Donation donation = dataSnapshot.getValue(Donation.class);
                            if (donation != null) {
                                donationList.add(donation);
                            }
                        }
                        donationAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DonationHistoryActivity.this, "Failed to load donations", Toast.LENGTH_SHORT).show();
                        Log.e("DonationHistory", error.getMessage());
                    }
                });
    }
}