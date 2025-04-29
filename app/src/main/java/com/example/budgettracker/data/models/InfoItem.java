package com.example.budgettracker.data.models;

public class InfoItem {
    private String title;
    private String description;
    private int iconResId;

    public InfoItem(String title, String description, int iconResId) {
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIconResId() {
        return iconResId;
    }
}