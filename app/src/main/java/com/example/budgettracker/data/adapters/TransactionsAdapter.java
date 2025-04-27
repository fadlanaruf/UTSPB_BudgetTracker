package com.example.budgettracker.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;
import com.example.budgettracker.data.models.Transactions;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionsViewHolder> {

    private List<Transactions> transactionList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Transactions transaction, int position);
    }

    public TransactionsAdapter(List<Transactions> transactionList) {
        this.transactionList = transactionList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Transactions> newData) {
        this.transactionList = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transactions, parent, false);
        return new TransactionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsViewHolder holder, int position) {
        Transactions transaction = transactionList.get(position);
        holder.bind(transaction);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    class TransactionsViewHolder extends RecyclerView.ViewHolder {
        TextView tvDeskripsi, tvJumlah, tvKategori, tvTanggal;

        public TransactionsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeskripsi = itemView.findViewById(R.id.tv_deskripsi);
            tvJumlah = itemView.findViewById(R.id.tv_jumlah);
            tvKategori = itemView.findViewById(R.id.tv_kategori);
            tvTanggal = itemView.findViewById(R.id.tv_tanggal);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(transactionList.get(position), position);
                }
            });
        }

        public void bind(Transactions transaction) {
            tvDeskripsi.setText(transaction.getDeskripsi());

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            String formattedAmount = currencyFormat.format(transaction.getJumlah());

            if ("pemasukan".equals(transaction.getTipe())) {
                tvJumlah.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
                tvJumlah.setText("+ " + formattedAmount);
            } else {
                tvJumlah.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.purple_500));
                tvJumlah.setText("- " + formattedAmount);
            }

            tvKategori.setText(transaction.getKategori());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
            String formattedDate = dateFormat.format(transaction.getTanggal());
            tvTanggal.setText(formattedDate);
        }
    }
}
