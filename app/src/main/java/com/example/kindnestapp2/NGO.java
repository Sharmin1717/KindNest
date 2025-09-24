package com.example.kindnestapp2;

import java.io.Serializable;
import java.util.Map;

public class NGO implements Serializable {

    private String id;  // <-- Firebase key
    private String name;
    private String logoUrl;
    private String focusArea;
    private String phone;
    private String email;
    private String website;
    private String address;
    private Map<String, Map<String, Boolean>> categories;

    public NGO() {}

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getLogoUrl() { return logoUrl; }
    public String getFocusArea() { return focusArea; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getWebsite() { return website; }
    public String getAddress() { return address; }
    public Map<String, Map<String, Boolean>> getCategories() { return categories; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public void setFocusArea(String focusArea) { this.focusArea = focusArea; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setWebsite(String website) { this.website = website; }
    public void setAddress(String address) { this.address = address; }
    public void setCategories(Map<String, Map<String, Boolean>> categories) { this.categories = categories; }
}
