package com.example.kindnestapp2;

public class User {
    // These variable names must exactly match the keys in your Firebase Realtime Database
    public String username;
    public String email;
    public String phone;
    public String role;
    public String profileImageUrl; // <-- THIS IS THE NEW FIELD

    // A public no-argument constructor is required for Firebase
    public User() {
    }

    public User(String username, String email, String phone, String role) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.profileImageUrl = ""; // Default to an empty string
    }

    // --- Getters and Setters ---
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // --- GETTER AND SETTER FOR THE NEW FIELD ---
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}