package com.example.budgettracker.data.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.budgettracker.data.models.Transactions;

public class FirebaseHelper {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public FirebaseHelper() {
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public Task<DocumentReference> addTransaksi(Transactions transaksi) {
        transaksi.setUserId(getCurrentUserId());
        return db.collection("transaksi").add(transaksi);
    }

    public Task<Void> deleteTransaksi(String transaksiId) {
        return db.collection("transaksi")
                .document(transaksiId).delete();
    }

    public Task<QuerySnapshot> getAllTransaksi() {
        return db.collection("transaksi")
                .whereEqualTo("userId", getCurrentUserId())
                .orderBy("tanggal", Query.Direction.DESCENDING)
                .get();
    }

    public Task<QuerySnapshot> getRecentTransaksi(int limit) {
        return db.collection("transaksi")
                .whereEqualTo("userId", getCurrentUserId())
                .orderBy("tanggal", Query.Direction.DESCENDING)
                .limit(limit)
                .get();
    }

    public Task<QuerySnapshot> getPemasukan() {
        return db.collection("transaksi")
                .whereEqualTo("userId", getCurrentUserId())
                .whereEqualTo("tipe", "pemasukan")
                .get();
    }

    public Task<QuerySnapshot> getPengeluaran() {
        return db.collection("transaksi")
                .whereEqualTo("userId", getCurrentUserId())
                .whereEqualTo("tipe", "pengeluaran")
                .get();
    }

    public void signOut() {
        mAuth.signOut();
    }
}