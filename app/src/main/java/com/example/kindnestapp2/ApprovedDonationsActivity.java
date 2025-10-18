package com.example.kindnestapp2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

public class ApprovedDonationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DonationAdapter adapter;
    private List<Donation> donationList;
    private DatabaseReference donationsRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approved_donations);

        // Initialize Firebase Database
        donationsRef = FirebaseDatabase.getInstance().getReference("donations");

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.approvedDonationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        donationList = new ArrayList<>();
        adapter = new DonationAdapter(donationList);
        recyclerView.setAdapter(adapter);

        // Load approved donations from Firebase
        loadApprovedDonations();
    }

    private void loadApprovedDonations() {
        donationsRef.orderByChild("status").equalTo("approved").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                donationList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Donation donation = snapshot.getValue(Donation.class);
                    if (donation != null) {
                        donation.setId(snapshot.getKey());
                        donationList.add(donation);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    // Donation Adapter for RecyclerView
    private class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.DonationViewHolder> {

        private List<Donation> donations;

        DonationAdapter(List<Donation> donations) {
            this.donations = donations;
        }

        @NonNull
        @Override
        public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation_history, parent, false);
            return new DonationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DonationViewHolder holder, int position) {
            Donation donation = donations.get(position);
            holder.ngoNameTextView.setText(donation.getNgoName());
            holder.itemDonatedTextView.setText("Donated for: " + donation.getItem());
            holder.amountTextView.setText("Amount: $" + donation.getAmount());
            holder.dateTextView.setText("Date: " + donation.getDate());

            // No approve button needed for approved donations
        }

        @Override
        public int getItemCount() {
            return donations.size();
        }

        class DonationViewHolder extends RecyclerView.ViewHolder {
            TextView ngoNameTextView, itemDonatedTextView, amountTextView, dateTextView;

            DonationViewHolder(@NonNull View itemView) {
                super(itemView);
                ngoNameTextView = itemView.findViewById(R.id.history_ngo_name);
                itemDonatedTextView = itemView.findViewById(R.id.history_item_donated);
                amountTextView = itemView.findViewById(R.id.history_amount);
                dateTextView = itemView.findViewById(R.id.history_date);
            }
        }
    }
}