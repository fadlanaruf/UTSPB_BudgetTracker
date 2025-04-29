package com.example.budgettracker.data.models;

public class WishlistItem {
    private String name;
    private boolean bought;

    public WishlistItem(String name, boolean bought) {
        this.name = name;
        this.bought = bought;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }
}