package com.example.budgettracker.data.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;
import com.example.budgettracker.data.models.WishlistItem;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private final List<WishlistItem> items;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(WishlistItem item);
    }

    public WishlistAdapter(List<WishlistItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wishlist, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        WishlistItem item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class WishlistViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvItemName;
        private final CheckBox checkBox;

        WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

        void bind(final WishlistItem item, final OnItemClickListener listener) {
            tvItemName.setText(item.getName());

            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(item.isBought());

            itemView.setOnClickListener(v -> listener.onItemClick(item));

            checkBox.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}