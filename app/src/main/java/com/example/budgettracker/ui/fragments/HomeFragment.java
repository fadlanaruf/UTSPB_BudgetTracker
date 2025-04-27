package com.example.budgettracker.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;
import com.example.budgettracker.ui.transactions.TransactionsAdapter;
import com.example.budgettracker.data.firebase.FirebaseHelper;
import com.example.budgettracker.data.models.Transactions;
import com.example.budgettracker.ui.transactions.AddTransactionsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView tvWelcome, tvTotalPemasukan, tvTotalPengeluaran, tvSaldoTersedia;
    private RecyclerView rvBudgets;
    private FloatingActionButton fabAddBudget;
    private TransactionsAdapter adapter;
    private FirebaseHelper firebaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseHelper = new FirebaseHelper();

        tvWelcome = view.findViewById(R.id.tv_welcome);
        rvBudgets = view.findViewById(R.id.rv_budgets);
        fabAddBudget = view.findViewById(R.id.fab_add_budget);
        tvTotalPemasukan = view.findViewById(R.id.tv_total_pemasukan);
        tvTotalPengeluaran = view.findViewById(R.id.tv_total_pengeluaran);
        tvSaldoTersedia = view.findViewById(R.id.tv_saldo_tersedia);

        rvBudgets.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionsAdapter(new ArrayList<>());
        rvBudgets.setAdapter(adapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            tvWelcome.setText("Selamat datang, " + currentUser.getEmail());
            loadTransactionsData();
            calculateSummary();
        }

        fabAddBudget.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddTransactionsActivity.class);
            startActivity(intent);
        });

    }

    private void loadTransactionsData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) return;

        db.collection("transaksi")
                .whereEqualTo("userId", user.getUid())
                .orderBy("tanggal", Query.Direction.DESCENDING)
                .limit(5)
                .get()
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
                        adapter.updateData(transactionList);
                    }
                });
    }

    private void calculateSummary() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) return;

        db.collection("transaksi")
                .whereEqualTo("userId", user.getUid())
                .whereEqualTo("tipe", "pemasukan")
                .get()
                .addOnSuccessListener(queryDocuments -> {
                    double totalPemasukan = 0;
                    for (DocumentSnapshot document : queryDocuments) {
                        Transactions transaction = document.toObject(Transactions.class);
                        if (transaction != null) {
                            totalPemasukan += transaction.getJumlah();
                        }
                    }
                    displayAmount(tvTotalPemasukan, totalPemasukan, true);
                    calculatePengeluaran(totalPemasukan);
                });
    }

    private void calculatePengeluaran(double totalPemasukan) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) return;

        db.collection("transaksi")
                .whereEqualTo("userId", user.getUid())
                .whereEqualTo("tipe", "pengeluaran")
                .get()
                .addOnSuccessListener(queryDocuments -> {
                    double totalPengeluaran = 0;
                    for (DocumentSnapshot document : queryDocuments) {
                        Transactions transaction = document.toObject(Transactions.class);
                        if (transaction != null) {
                            totalPengeluaran += transaction.getJumlah();
                        }
                    }
                    displayAmount(tvTotalPengeluaran, totalPengeluaran, false);

                    double saldoTersedia = totalPemasukan - totalPengeluaran;
                    displayAmount(tvSaldoTersedia, saldoTersedia, saldoTersedia >= 0);
                });
    }

    private void displayAmount(TextView textView, double amount, boolean isPositive) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String formattedAmount = currencyFormat.format(amount);

        int color = ContextCompat.getColor(requireContext(), isPositive ? R.color.green : R.color.red);
        textView.setTextColor(color);
        textView.setText(formattedAmount);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTransactionsData();
        calculateSummary();
    }
}
