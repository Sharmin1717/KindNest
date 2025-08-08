package com.example.kindnestapp2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation_history, parent, false);
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
        TextView ngoName, itemDonated, amount, date;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ngoName = itemView.findViewById(R.id.history_ngo_name);
            itemDonated = itemView.findViewById(R.id.history_item_donated);
            amount = itemView.findViewById(R.id.history_amount);
            date = itemView.findViewById(R.id.history_date);
        }

        public void bind(Donation donation) {
            ngoName.setText(donation.getNgoName());
            itemDonated.setText("Donated for: " + donation.getItem());
            amount.setText("Amount: $" + String.format(Locale.US, "%.2f", donation.getAmount()));

            // Format the timestamp into a readable date
            if (donation.getTimestamp() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                date.setText("Date: " + sdf.format(new Date(donation.getTimestamp())));
            } else {
                date.setText("Date not available");
            }
        }
    }
}