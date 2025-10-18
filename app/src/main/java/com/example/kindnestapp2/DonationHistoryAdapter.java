package com.example.kindnestapp2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DonationHistoryAdapter extends RecyclerView.Adapter<DonationHistoryAdapter.HistoryViewHolder> {

    private final List<Donation> donationList;

    public DonationHistoryAdapter(List<Donation> donationList) {
        this.donationList = donationList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_donation_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Donation donation = donationList.get(position);
        holder.bind(donation);
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView ngoName, itemDonated, amount, date, status;
        Button viewDetailsButton;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ngoName = itemView.findViewById(R.id.history_ngo_name);
            itemDonated = itemView.findViewById(R.id.history_item_donated);
            amount = itemView.findViewById(R.id.history_amount);
            date = itemView.findViewById(R.id.history_date);
            status = itemView.findViewById(R.id.history_status);
            viewDetailsButton = itemView.findViewById(R.id.view_details_button);
        }

        public void bind(Donation donation) {
            ngoName.setText(donation.getNgoName());
            itemDonated.setText("Donated for: " + donation.getItem());
            amount.setText("Amount: BDT " + String.format(Locale.US, "%.2f", donation.getAmount()));

            // Date formatting
            if (donation.getTimestamp() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                date.setText("Date: " + sdf.format(new Date(donation.getTimestamp())));
            } else {
                date.setText("Date not available");
            }

            // ✅ Show actual status dynamically
            String statusText = donation.getStatus() != null ? donation.getStatus() : "Pending";
            status.setText("Status: " + capitalize(statusText));

            // Set color for better UI
            if ("acknowledged".equalsIgnoreCase(statusText)) {
                status.setTextColor(0xFF4CAF50); // Green
            } else {
                status.setTextColor(0xFFFF9800); // Orange for pending
            }

            // Details dialog
            viewDetailsButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                builder.setTitle("Donation Details");
                builder.setMessage(
                        "NGO: " + donation.getNgoName() + "\n" +
                                "Item: " + donation.getItem() + "\n" +
                                "Amount: BDT " + String.format(Locale.US, "%.2f", donation.getAmount()) + "\n" +
                                "Date: " + (donation.getTimestamp() > 0
                                ? new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date(donation.getTimestamp()))
                                : "N/A") + "\n" +
                                "Transaction ID: " + donation.getTransactionId() + "\n" +
                                "Status: " + capitalize(statusText)
                );
                builder.setPositiveButton("OK", null);
                builder.show();
            });
        }

        private String capitalize(String s) {
            if (s == null || s.isEmpty()) return s;
            return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        }
    }
}
