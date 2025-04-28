package com.example.budgettracker.data.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;
import com.example.budgettracker.data.models.InfoItem;

import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoViewHolder> {

    private List<InfoItem> infoItems;

    public InfoAdapter(List<InfoItem> infoItems) {
        this.infoItems = infoItems;
    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_info, parent, false);
        return new InfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoViewHolder holder, int position) {
        InfoItem item = infoItems.get(position);
        holder.titleText.setText(item.getTitle());
        holder.descriptionText.setText(item.getDescription());
        holder.iconImage.setImageResource(item.getIconResId());

        holder.itemView.setTranslationX(300);
        holder.itemView.setAlpha(0f);
        holder.itemView.animate()
                .translationX(0)
                .alpha(1f)
                .setDuration(300)
                .setStartDelay(position * 100)
                .start();
    }

    @Override
    public int getItemCount() {
        return infoItems.size();
    }

    static class InfoViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView descriptionText;
        ImageView iconImage;

        public InfoViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.tv_info_title);
            descriptionText = itemView.findViewById(R.id.tv_info_description);
            iconImage = itemView.findViewById(R.id.iv_info_icon);
        }
    }
}