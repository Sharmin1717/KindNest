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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView profileBtn = findViewById(R.id.profile_button);
        progressBar = findViewById(R.id.progress_bar);
        ngoRecyclerView = findViewById(R.id.ngo_recycler_view);
        ngoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ngoList = new ArrayList<>();
        ngoAdapter = new NGOAdapter(this, ngoList, this);
        ngoRecyclerView.setAdapter(ngoAdapter);

        // Firebase reference to "ngos"
        ngoRef = FirebaseDatabase.getInstance().getReference("ngos");

        profileBtn.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        loadNGOsFromFirebase();
    }

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
                Toast.makeText(MainActivity.this, "Failed to load NGOs: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNGOClick(NGO ngo) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_ngo_details, null);
        bottomSheetDialog.setContentView(dialogView);

        ImageView logo = dialogView.findViewById(R.id.dialog_ngo_logo);
        TextView name = dialogView.findViewById(R.id.dialog_ngo_name);
        TextView focus = dialogView.findViewById(R.id.dialog_ngo_focus);
        TextView categories = dialogView.findViewById(R.id.dialog_ngo_categories);

        // NEW FIELDS
        TextView phone = dialogView.findViewById(R.id.dialog_ngo_phone);
        TextView email = dialogView.findViewById(R.id.dialog_ngo_email);
        TextView website = dialogView.findViewById(R.id.dialog_ngo_website);
        TextView address = dialogView.findViewById(R.id.dialog_ngo_address);

        MaterialButton donateButton = dialogView.findViewById(R.id.button_donate);
        MaterialButton closeButton = dialogView.findViewById(R.id.button_close);

        // Set details
        name.setText(ngo.getName());
        focus.setText(ngo.getFocusArea());
        Glide.with(this).load(ngo.getLogoUrl()).into(logo);

        // EXTRA INFO
        phone.setText("Phone: " + (ngo.getPhone() != null ? ngo.getPhone() : "N/A"));
        email.setText("Email: " + (ngo.getEmail() != null ? ngo.getEmail() : "N/A"));
        website.setText("Website: " + (ngo.getWebsite() != null ? ngo.getWebsite() : "N/A"));
        address.setText("Address: " + (ngo.getAddress() != null ? ngo.getAddress() : "N/A"));

        // Categories
        StringBuilder categoriesText = new StringBuilder();
        if (ngo.getCategories() != null && !ngo.getCategories().isEmpty()) {
            for (String category : ngo.getCategories().keySet()) {
                categoriesText.append("• ").append(category).append("\n");
            }
        } else {
            categoriesText.append("No specific donation categories listed.");
        }
        categories.setText(categoriesText.toString().trim());

        // Donate button
        donateButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DonationActivity.class);
            intent.putExtra("NGO_OBJECT", ngo);
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });

        // Clickable website link
        website.setOnClickListener(v -> {
            if (ngo.getWebsite() != null && !ngo.getWebsite().isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ngo.getWebsite()));
                startActivity(browserIntent);
            }
        });

        closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }
}
