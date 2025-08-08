package com.example.kindnestapp2;

public class Donation {
    private String ngoName;
    private String item;
    private double amount;
    private long timestamp;
    private String userId;
    private String transactionId; // To store the ID from the payment gateway

    // Required empty constructor for Firebase
    public Donation() {}

    public Donation(String ngoName, String item, double amount, long timestamp, String userId, String transactionId) {
        this.ngoName = ngoName;
        this.item = item;
        this.amount = amount;
        this.timestamp = timestamp;
        this.userId = userId;
        this.transactionId = transactionId;
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
}