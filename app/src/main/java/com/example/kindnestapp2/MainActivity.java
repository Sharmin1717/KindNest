package com.example.kindnestapp2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NGOAdapter.OnNGOClickListener {

    private RecyclerView ngoRecyclerView;
    private NGOAdapter ngoAdapter;
    private List<NGO> ngoList;
    private DatabaseReference ngoRef;

    private ProgressBar progressBar;


    private ImageView notificationBtn;
    private DatabaseReference donationsRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView profileBtn = findViewById(R.id.profile_button);
        notificationBtn = findViewById(R.id.notification_button);
        progressBar = findViewById(R.id.progress_bar);
        ngoRecyclerView = findViewById(R.id.ngo_recycler_view);
        ngoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ngoList = new ArrayList<>();
        ngoAdapter = new NGOAdapter(this, ngoList, this);
        ngoRecyclerView.setAdapter(ngoAdapter);

        ngoRef = FirebaseDatabase.getInstance().getReference("ngos");


        auth = FirebaseAuth.getInstance();
        donationsRef = FirebaseDatabase.getInstance().getReference("donations");

        profileBtn.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));


        notificationBtn.setOnClickListener(v -> markAcknowledgedAsSeenAndOpen());

        listenForAcknowledgedDonations();

        loadNGOsFromFirebase();
    }


    private void listenForAcknowledgedDonations() {
        if (auth.getCurrentUser() == null) return;
        String userId = auth.getCurrentUser().getUid();

        donationsRef.orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean hasNewAcknowledged = false;

                        for (DataSnapshot donationSnap : snapshot.getChildren()) {
                            Donation donation = donationSnap.getValue(Donation.class);
                            if (donation != null
                                    && "acknowledged".equalsIgnoreCase(donation.getStatus())
                                    && !donation.isSeenByUser()) {
                                hasNewAcknowledged = true;
                                break;
                            }
                        }


                        notificationBtn.setImageResource(
                                hasNewAcknowledged ? R.drawable.ic_notifications_new : R.drawable.ic_notifications
                        );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }


    private void markAcknowledgedAsSeenAndOpen() {
        if (auth.getCurrentUser() == null) return;
        String userId = auth.getCurrentUser().getUid();

        donationsRef.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot donationSnap : snapshot.getChildren()) {
                            Donation donation = donationSnap.getValue(Donation.class);
                            if (donation != null
                                    && "acknowledged".equalsIgnoreCase(donation.getStatus())
                                    && !donation.isSeenByUser()) {
                                donationSnap.getRef().child("seenByUser").setValue(true);
                            }
                        }


                        startActivity(new Intent(MainActivity.this, AcknowledgedDonationsActivity.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    //Load
    private void loadNGOsFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        ngoRecyclerView.setVisibility(View.GONE);

        ngoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ngoList.clear();
                for (DataSnapshot ngoSnapshot : snapshot.getChildren()) {
                    NGO ngo = ngoSnapshot.getValue(NGO.class);
                    if (ngo != null) {
                        ngoList.add(ngo);
                    }
                }
                ngoAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                ngoRecyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this,
                        "Failed to load NGOs: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

  //ngo details
    @Override
    public void onNGOClick(NGO ngo) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_ngo_details, null);
        bottomSheetDialog.setContentView(dialogView);

        ImageView logo = dialogView.findViewById(R.id.dialog_ngo_logo);
        TextView name = dialogView.findViewById(R.id.dialog_ngo_name);
        TextView focus = dialogView.findViewById(R.id.dialog_ngo_focus);
        TextView categories = dialogView.findViewById(R.id.dialog_ngo_categories);

        TextView phone = dialogView.findViewById(R.id.dialog_ngo_phone);
        TextView email = dialogView.findViewById(R.id.dialog_ngo_email);
        TextView website = dialogView.findViewById(R.id.dialog_ngo_website);
        TextView address = dialogView.findViewById(R.id.dialog_ngo_address);

        MaterialButton donateButton = dialogView.findViewById(R.id.button_donate);
        MaterialButton closeButton = dialogView.findViewById(R.id.button_close);

        name.setText(ngo.getName());
        focus.setText(ngo.getFocusArea());
        Glide.with(this).load(ngo.getLogoUrl()).into(logo);

        phone.setText("Phone: " + (ngo.getPhone() != null ? ngo.getPhone() : "N/A"));
        email.setText("Email: " + (ngo.getEmail() != null ? ngo.getEmail() : "N/A"));
        website.setText("Website: " + (ngo.getWebsite() != null ? ngo.getWebsite() : "N/A"));
        address.setText("Address: " + (ngo.getAddress() != null ? ngo.getAddress() : "N/A"));

        StringBuilder categoriesText = new StringBuilder();
        if (ngo.getCategories() != null && !ngo.getCategories().isEmpty()) {
            for (String category : ngo.getCategories().keySet()) {
                categoriesText.append("• ").append(category).append("\n");
            }
        } else {
            categoriesText.append("No specific donation categories listed.");
        }
        categories.setText(categoriesText.toString().trim());

        donateButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DonationActivity.class);
            intent.putExtra("NGO_OBJECT", ngo);
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });

        website.setOnClickListener(v -> {
            if (ngo.getWebsite() != null && !ngo.getWebsite().isEmpty()) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ngo.getWebsite())));
            }
        });

        closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }
}
