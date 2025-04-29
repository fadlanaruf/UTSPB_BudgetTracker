package com.example.budgettracker.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;
import com.example.budgettracker.data.adapters.TransactionsAdapter;
import com.example.budgettracker.data.firebase.FirebaseHelper;
import com.example.budgettracker.data.models.Transactions;
import com.example.budgettracker.ui.transactions.AddTransactionsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView tvWelcome, tvTotalPemasukan, tvTotalPengeluaran, tvSaldoTersedia;
    private RecyclerView rvTransactions;
    private FloatingActionButton fabAddTransaction;
    private TransactionsAdapter adapter;
    private FirebaseHelper firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvWelcome = view.findViewById(R.id.tv_welcome);
        rvTransactions = view.findViewById(R.id.rv_transactions);
        fabAddTransaction = view.findViewById(R.id.fab_add_transaction);
        tvTotalPemasukan = view.findViewById(R.id.tv_total_pemasukan);
        tvTotalPengeluaran = view.findViewById(R.id.tv_total_pengeluaran);
        tvSaldoTersedia = view.findViewById(R.id.tv_saldo_tersedia);

        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionsAdapter(new ArrayList<>());
        rvTransactions.setAdapter(adapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String username = currentUser.getDisplayName();
            if (username != null && !username.isEmpty()) {
                tvWelcome.setText("Selamat datang, " + username);
            } else {
                String email = currentUser.getEmail();
                String name = email != null ? email.split("@")[0] : "User";
                tvWelcome.setText("Selamat datang, " + name);
            }
            loadTransactionsData();
            calculateSummary();
        }

        fabAddTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddTransactionsActivity.class);
            startActivity(intent);
        });

    }

    private void loadTransactionsData() {
        firestore = new FirebaseHelper();
        firestore.getRecentTransaksi(3)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Transactions> transactionList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            Transactions transaction = document.toObject(Transactions.class);
                            if (transaction != null) {
                                transaction.setId(document.getId());
                                transactionList.add(transaction);
                            }
                        }

                        if (isAdded()) {
                            adapter.updateData(transactionList);
                        }
                    }
                });
    }

    private void calculateSummary() {
        firestore = new FirebaseHelper();
        firestore.getPemasukan()
                .addOnSuccessListener(queryDocuments -> {
                    if (!isAdded()) return;

                    double totalPemasukan = 0;
                    for (DocumentSnapshot document : queryDocuments) {
                        Transactions transaction = document.toObject(Transactions.class);
                        if (transaction != null) {
                            totalPemasukan += transaction.getJumlah();
                        }
                    }
                    tvTotalPemasukan.setText(formatCurrency(totalPemasukan));
                    calculatePengeluaran(totalPemasukan);
                });
    }

    private void calculatePengeluaran(double totalPemasukan) {
        firestore = new FirebaseHelper();
        firestore.getPengeluaran()
                .addOnSuccessListener(queryDocuments -> {
                    if (!isAdded()) return;
                    double totalPengeluaran = 0;
                    for (DocumentSnapshot document : queryDocuments) {
                        Transactions transaction = document.toObject(Transactions.class);
                        if (transaction != null) {
                            totalPengeluaran += transaction.getJumlah();
                        }
                    }
                    tvTotalPengeluaran.setText(formatCurrency(totalPengeluaran));

                    double saldo = totalPemasukan - totalPengeluaran;
                    tvSaldoTersedia.setText(formatCurrency(saldo));
                });
    }

    private String formatCurrency(double amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return currencyFormat.format(amount);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTransactionsData();
        calculateSummary();
    }
}