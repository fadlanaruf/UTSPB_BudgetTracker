    package com.example.budgettracker.ui.activities;

    import android.os.Bundle;
    import android.view.View;
    import android.widget.ImageView;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;
    import com.example.budgettracker.data.adapters.InfoAdapter;
    import com.example.budgettracker.R;
    import com.example.budgettracker.data.models.InfoItem;

    import java.util.ArrayList;
    import java.util.List;

    public class InfoActivity extends AppCompatActivity {

        private RecyclerView recyclerView;
        private List<InfoItem> infoItems;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_info);

            recyclerView = findViewById(R.id.rv_info_items);
            ImageView backButton = findViewById(R.id.iv_back);

            backButton.setOnClickListener(v -> finish());

            setupInfoItems();
            setupRecyclerView();

            View rootView = findViewById(R.id.info_container);
            rootView.setAlpha(0f);
            rootView.animate().alpha(1f).setDuration(300).start();
        }

        private void setupInfoItems() {
            infoItems = new ArrayList<>();

            infoItems.add(new InfoItem(
                    "Mengelola Keuangan",
                    "Aplikasi Budget Tracker membantu Anda melacak pemasukan dan pengeluaran dengan mudah. Lihat saldo, analisis pengeluaran, dan kelola keuangan Anda secara efisien.",
                    R.drawable.ic_money
            ));

            infoItems.add(new InfoItem(
                    "Mencatat Transaksi",
                    "Tambahkan transaksi dengan mengklik tombol '+' di Dashboard. Pilih kategori, masukkan jumlah, dan tambahkan deskripsi untuk melacak setiap transaksi Anda.",
                    R.drawable.ic_transactions
            ));

            infoItems.add(new InfoItem(
                    "Laporan Keuangan",
                    "Lihat laporan keuangan harian, mingguan, dan bulanan. Analisis pengeluaran berdasarkan kategori untuk memahami kebiasaan belanja Anda.",
                    R.drawable.ic_report
            ));

            infoItems.add(new InfoItem(
                    "Pengaturan Anggaran",
                    "Tetapkan batas anggaran untuk setiap kategori dan dapatkan notifikasi ketika pengeluaran mendekati atau melebihi batas yang ditentukan.",
                    R.drawable.budget_icon
            ));

            infoItems.add(new InfoItem(
                    "Tema Aplikasi",
                    "Sesuaikan tampilan aplikasi dengan mengaktifkan mode gelap di pengaturan untuk kenyamanan penggunaan di malam hari.",
                    R.drawable.ic_moon
            ));
        }

        private void setupRecyclerView() {
            InfoAdapter adapter = new InfoAdapter(infoItems);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
    }