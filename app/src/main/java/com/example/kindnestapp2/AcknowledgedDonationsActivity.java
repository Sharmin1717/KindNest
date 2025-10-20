package com.example.kindnestapp2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class AcknowledgedDonationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AcknowledgedDonationAdapter adapter;
    private List<Donation> donationList;
    private DatabaseReference donationsRef;
    private ProgressBar progressBar;
    private TextView emptyMessage;

    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acknowledged_donations);

        recyclerView = findViewById(R.id.recyclerViewAcknowledged);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.progressBarAcknowledged);
        emptyMessage = findViewById(R.id.textEmptyAcknowledged);

        donationList = new ArrayList<>();
        adapter = new AcknowledgedDonationAdapter(donationList);
        recyclerView.setAdapter(adapter);

        donationsRef = FirebaseDatabase.getInstance().getReference("donations");

        //get role from intent
        if (getIntent() != null && getIntent().hasExtra("IS_ADMIN")) {
            isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
        }

        loadAcknowledgedDonations();
    }

    private void loadAcknowledgedDonations() {
        progressBar.setVisibility(View.VISIBLE);
        emptyMessage.setVisibility(View.GONE);

        donationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                donationList.clear();

                String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                        ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                        : "";

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Donation donation = ds.getValue(Donation.class);
                    if (donation != null) {
                        donation.setId(ds.getKey());
                        String status = donation.getStatus() != null ? donation.getStatus().trim() : "";

                        Log.d("ACK_DEBUG", "Donation ID: " + donation.getId() + ", status: '" + status + "'");

                        //Add donation if acknowledged
                        if ("acknowledged".equalsIgnoreCase(status)) {
                            if (isAdmin || currentUserId.equals(donation.getUserId())) {
                                donationList.add(donation);
                            }
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (donationList.isEmpty()) {
                    emptyMessage.setVisibility(View.VISIBLE);
                    emptyMessage.setText(isAdmin
                            ? "No acknowledged donations found."
                            : "You have no acknowledged donations yet.");
                } else {
                    emptyMessage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AcknowledgedDonationsActivity.this,
                        "Error loading donations: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
