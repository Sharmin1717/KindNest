package com.example.kindnestapp2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class AdminDonationAdapter extends RecyclerView.Adapter<AdminDonationAdapter.DonationViewHolder> {

    private final List<Donation> donationList;
    private final OnDonationActionListener listener;

    public interface OnDonationActionListener {
        void onAcknowledgeClicked(Donation donation);
    }

    public AdminDonationAdapter(List<Donation> donationList, OnDonationActionListener listener) {
        this.donationList = donationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_donation_admin, parent, false);
        return new DonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonationViewHolder holder, int position) {
        Donation donation = donationList.get(position);

        holder.tvDonationId.setText("ID: " + safe(donation.getId()));
        holder.tvDonationAmount.setText(donation.getNgoName() + " - ৳" + String.format(Locale.US, "%.2f", donation.getAmount()));
        holder.tvDonationCategory.setText("Category: " + safe(donation.getCategory()));
        holder.tvDonationDate.setText("Date: " + donation.getDate());

        String status = safe(donation.getStatus());
        holder.tvDonationStatus.setText("Status: " + status);
        holder.tvDonationStatus.setTextColor(status.equalsIgnoreCase("acknowledged") ? 0xFF4CAF50 : 0xFFFFA000);

        String paymentStatus = donation.getTransactionId() != null && !donation.getTransactionId().isEmpty()
                ? "Completed" : "Pending";
        holder.tvPaymentStatus.setText("Payment: " + paymentStatus);
        holder.tvPaymentStatus.setTextColor(paymentStatus.equals("Completed") ? 0xFF4CAF50 : 0xFFFFA000);

        if ("acknowledged".equalsIgnoreCase(status)) {
            holder.btnAcknowledge.setVisibility(View.GONE);
        } else {
            holder.btnAcknowledge.setVisibility(View.VISIBLE);
            holder.btnAcknowledge.setOnClickListener(v -> {
                if (listener != null) listener.onAcknowledgeClicked(donation);
            });
        }
    }

    @Override
    public int getItemCount() {
        return donationList == null ? 0 : donationList.size();
    }

    private String safe(String text) {
        return (text == null || text.trim().isEmpty()) ? "-" : text;
    }

    public static class DonationViewHolder extends RecyclerView.ViewHolder {
        TextView tvDonationId, tvDonationAmount, tvDonationCategory, tvDonationDate, tvDonationStatus, tvPaymentStatus;
        Button btnAcknowledge;

        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDonationId = itemView.findViewById(R.id.tvDonationId);
            tvDonationAmount = itemView.findViewById(R.id.tvDonationAmount);
            tvDonationCategory = itemView.findViewById(R.id.tvDonationCategory);
            tvDonationDate = itemView.findViewById(R.id.tvDonationDate);
            tvDonationStatus = itemView.findViewById(R.id.tvDonationStatus);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
            btnAcknowledge = itemView.findViewById(R.id.btnAcknowledge);
        }
    }
}
