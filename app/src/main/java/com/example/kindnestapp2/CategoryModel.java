package com.example.kindnestapp2;

public class CategoryModel {
    private String categoryName;
    private int iconRes;

    public CategoryModel(String categoryName, int iconRes) {
        this.categoryName = categoryName;
        this.iconRes = iconRes;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getIconRes() {
        return iconRes;
    }
}
