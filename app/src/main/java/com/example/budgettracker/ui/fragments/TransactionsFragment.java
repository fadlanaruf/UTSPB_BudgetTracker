package com.example.budgettracker.ui.fragments;

import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;
import com.example.budgettracker.data.adapters.TransactionsAdapter;
import com.example.budgettracker.data.firebase.FirebaseHelper;
import com.example.budgettracker.data.models.Transactions;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TransactionsAdapter adapter;
    private List<Transactions> transactionList;
    private ListenerRegistration listenerRegistration;
    private EditText searchInput;
    private AutoCompleteTextView categorySpinner;
    private LinearLayout emptyState;
    private Chip chipResultCount;
    private TextView emptyStateText;
    private FirebaseHelper firestore;

    private List<Transactions> fullTransactionList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRecyclerView();

        showEmptyState();

        setupSearchAndFilter();
        setupSwipeToDelete();
        loadTransactions();
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerTransactions);
        searchInput = view.findViewById(R.id.editSearch);
        categorySpinner = view.findViewById(R.id.spinnerCategory);
        emptyState = view.findViewById(R.id.emptyState);
        chipResultCount = view.findViewById(R.id.chipResultCount);
        emptyStateText = view.findViewById(R.id.emptyStateText);

        emptyStateText.setText(R.string.no_transactions_found);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionList = new ArrayList<>();
        adapter = new TransactionsAdapter(transactionList);
        recyclerView.setAdapter(adapter);
    }

    private void loadTransactions() {
        firestore = new FirebaseHelper();

        showLoadingState();

        firestore.getAllTransaksi()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        fullTransactionList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Transactions transaction = doc.toObject(Transactions.class);
                            transaction.setId(doc.getId());
                            fullTransactionList.add(transaction);
                        }
                        applyFilters();
                    } else {
                        Log.e("Firestore", "Error loading transactions: ", task.getException());
                        showSnackbar("Gagal memuat transaksi");
                        showEmptyState();
                    }
                });
    }

    private void showLoadingState() {
        emptyState.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyStateText.setText(R.string.loading_transactions);
    }

    private void setupSearchAndFilter() {
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.transaction_categories, R.layout.dropdown_menu_item);
        categoryAdapter.setDropDownViewResource(R.layout.dropdown_menu_item);
        categorySpinner.setAdapter(categoryAdapter);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        categorySpinner.setOnItemClickListener((parent, view, position, id) -> applyFilters());
    }

    private void applyFilters() {
        String keyword = searchInput.getText().toString().toLowerCase(Locale.ROOT);
        String selectedCategory = categorySpinner.getText().toString();

        List<Transactions> filtered = new ArrayList<>();
        for (Transactions transaction : fullTransactionList) {
            boolean matchesKeyword = transaction.getDeskripsi().toLowerCase().contains(keyword);
            boolean matchesCategory = selectedCategory.equals("Semua") ||
                    transaction.getKategori().equalsIgnoreCase(selectedCategory);

            if (matchesKeyword && matchesCategory) {
                filtered.add(transaction);
            }
        }

        updateTransactionList(filtered);
    }

    private void updateTransactionList(List<Transactions> filteredList) {
        transactionList.clear();
        transactionList.addAll(filteredList);
        adapter.notifyDataSetChanged();

        chipResultCount.setText(getResources().getQuantityString(
                R.plurals.search_results_count,
                filteredList.size(),
                filteredList.size()));

        if (transactionList.isEmpty()) {
            showEmptyState();
        } else {
            hideEmptyState();
        }
    }

    private void showEmptyState() {
        emptyState.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        if (!fullTransactionList.isEmpty()) {
            emptyStateText.setText(R.string.no_matching_transactions);
        } else {
            emptyStateText.setText(R.string.no_transactions_found);
        }
    }

    private void hideEmptyState() {
        emptyState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Transactions deleted = transactionList.get(position);
                deleteTransaction(deleted, position);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        new ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(recyclerView);
    }

    private void deleteTransaction(Transactions transaction, int position) {
        String docId = transaction.getId();

        transactionList.remove(position);
        adapter.notifyItemRemoved(position);
        showSnackbar("Transaksi dihapus", "BATAL", v -> undoDelete(transaction, position));
    }

    private void undoDelete(Transactions transaction, int position) {
        transactionList.add(position, transaction);
        adapter.notifyItemInserted(position);
        applyFilters();
    }

    private void showSnackbar(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void showSnackbar(String message, String actionText, View.OnClickListener action) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG)
                    .setAction(actionText, action)
                    .show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}