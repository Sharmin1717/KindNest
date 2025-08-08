package com.example.kindnestapp2;

import java.io.Serializable;
import java.util.Map;

// We implement Serializable so we can pass the entire NGO object via an Intent
public class NGO implements Serializable {

    private String name;
    private String logoUrl;
    private String focusArea;
    // THIS IS THE KEY CHANGE: It now matches your JSON structure perfectly.
    // Map<CategoryName, Map<SubCategoryName, Boolean>>
    private Map<String, Map<String, Boolean>> categories;

    // Required empty public constructor for Firebase
    public NGO() {}

    // --- Getters ---
    public String getName() { return name; }
    public String getLogoUrl() { return logoUrl; }
    public String getFocusArea() { return focusArea; }
    public Map<String, Map<String, Boolean>> getCategories() { return categories; }

    // --- Setters ---
    public void setName(String name) { this.name = name; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public void setFocusArea(String focusArea) { this.focusArea = focusArea; }
    public void setCategories(Map<String, Map<String, Boolean>> categories) { this.categories = categories; }
}