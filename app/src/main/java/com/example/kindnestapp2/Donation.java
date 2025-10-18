package com.example.kindnestapp2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Donation {
    private String ngoName;
    private String item;
    private double amount;
    private long timestamp;
    private String userId;
    private String transactionId;
    private String status; // "pending" or "acknowledged"
    private String category;
    private String id;
    private boolean seenByUser;

    // Required empty constructor for Firebase
    public Donation() {
        this.status = "pending";
        this.seenByUser = false;
    }

    // When creating a Donation programmatically, default it to pending (user made donation)
    public Donation(String ngoName, String item, double amount, long timestamp, String userId, String transactionId, String category) {
        this.ngoName = ngoName;
        this.item = item;
        this.amount = amount;
        this.timestamp = timestamp;
        this.userId = userId;
        this.transactionId = transactionId;
        this.category = category;
        this.status = "pending";     // IMPORTANT: start as pending
        this.seenByUser = false;     // user hasn't seen acknowledgment yet
    }

    // --- Getters and Setters ---
    public String getNgoName() { return ngoName; }
    public void setNgoName(String ngoName) { this.ngoName = ngoName; }

    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public boolean isSeenByUser() { return seenByUser; }
    public void setSeenByUser(boolean seenByUser) { this.seenByUser = seenByUser; }

    // Helper method to get formatted date
    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
