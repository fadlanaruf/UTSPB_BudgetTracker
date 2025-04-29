package com.example.budgettracker.data.models;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class Transactions {
    private String id;
    private String userId;
    private double jumlah;
    private String deskripsi;
    private String kategori;
    private String tipe;
    private Date tanggal;

    public Transactions() {
    }

    public Transactions(String userId, double jumlah, String deskripsi, String kategori, String tipe, Date tanggal) {
        this.userId = userId;
        this.jumlah = jumlah;
        this.deskripsi = deskripsi;
        this.kategori = kategori;
        this.tipe = tipe;
        this.tanggal = tanggal;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getJumlah() {
        return jumlah;
    }

    public void setJumlah(double jumlah) {
        this.jumlah = jumlah;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }
}
