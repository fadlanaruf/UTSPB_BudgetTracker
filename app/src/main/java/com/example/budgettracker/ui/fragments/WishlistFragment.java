package com.example.budgettracker.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.data.adapters.WishlistAdapter;
import com.example.budgettracker.data.models.WishlistItem;
import com.example.budgettracker.databinding.FragmentWishlistBinding;

import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment {

    private FragmentWishlistBinding binding;
    private WishlistAdapter adapter;
    private final List<WishlistItem> wishlist = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWishlistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupRecyclerView();
        updateItemCount();
        setupAddButton();
        setupSwipeToDelete();
        updateEmptyStateVisibility();
    }

    private void setupRecyclerView() {
        adapter = new WishlistAdapter(wishlist, item -> {
            int position = wishlist.indexOf(item);
            if (position != -1) {
                item.setBought(!item.isBought());
                adapter.notifyItemChanged(position);
            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupAddButton() {
        binding.btnAdd.setOnClickListener(v -> {
            String text = binding.etItem.getText().toString().trim();
            if (!text.isEmpty()) {
                wishlist.add(0, new WishlistItem(text, false));
                adapter.notifyItemInserted(0);
                binding.recyclerView.scrollToPosition(0);
                binding.etItem.setText("");

                updateItemCount();
                updateEmptyStateVisibility();
            } else {
                Toast.makeText(getContext(), "Masukkan item terlebih dahulu",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv,
                                  @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder tgt) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    wishlist.remove(pos);
                    adapter.notifyItemRemoved(pos);
                    Toast.makeText(getContext(), "Item dihapus", Toast.LENGTH_SHORT).show();

                    updateItemCount();
                    updateEmptyStateVisibility();
                }
            }
        }).attachToRecyclerView(binding.recyclerView);
    }

    private void updateItemCount() {
        binding.tvItemCount.setText(wishlist.size() + " item");
    }

    private void updateEmptyStateVisibility() {
        if (wishlist.isEmpty()) {
            binding.emptyStateView.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        } else {
            binding.emptyStateView.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}