package com.example.kindnestapp2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class AcknowledgedDonationAdapter extends RecyclerView.Adapter<AcknowledgedDonationAdapter.DonationViewHolder> {

    private final List<Donation> donationList;

    public AcknowledgedDonationAdapter(List<Donation> donationList) {
        this.donationList = donationList;
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

        holder.tvDonationId.setText("Donation ID: " + safe(donation.getId()));
        holder.tvDonationAmount.setText(String.format(Locale.US, "Amount: ৳%.2f", donation.getAmount()));
        holder.tvDonationCategory.setText("Category: " + safe(donation.getCategory()));
        holder.tvDonationDate.setText("Date: " + safe(donation.getDate()));
        holder.tvDonationStatus.setText("Status: " + safe(donation.getStatus()));
        holder.tvDonationStatus.setTextColor(0xFF4CAF50);

        String paymentStatus = (donation.getTransactionId() != null && !donation.getTransactionId().isEmpty())
                ? "Completed" : "Pending";
        holder.tvPaymentStatus.setText("Payment: " + paymentStatus);
        holder.tvPaymentStatus.setTextColor(paymentStatus.equals("Completed") ? 0xFF4CAF50 : 0xFFFFA000);

        // Hide Acknowledge button (not needed here)
        holder.btnAcknowledge.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return donationList == null ? 0 : donationList.size();
    }

    private String safe(String text) {
        return (text == null || text.trim().isEmpty()) ? "-" : text;
    }

    static class DonationViewHolder extends RecyclerView.ViewHolder {
        TextView tvDonationId, tvDonationAmount, tvDonationCategory, tvDonationDate,
                tvDonationStatus, tvPaymentStatus;
        View btnAcknowledge;

        DonationViewHolder(@NonNull View itemView) {
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
