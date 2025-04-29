package com.example.budgettracker.ui.transactions;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.budgettracker.R;
import com.example.budgettracker.data.firebase.FirebaseHelper;
import com.example.budgettracker.data.models.Transactions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTransactionsActivity extends AppCompatActivity {

    private EditText etJumlah, etDeskripsi, etTanggal;
    private RadioGroup rgTipe;
    private RadioButton rbPemasukan, rbPengeluaran;
    private Spinner spinnerKategori;
    private Button btnSimpan, btnBatal;

    private FirebaseHelper firebaseHelper;
    private Calendar selectedDate = Calendar.getInstance();
    private List<String> kategoriPemasukan;
    private List<String> kategoriPengeluaran;
    private String selectedKategori;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transactions);

        // Initialize Firebase Helper
        firebaseHelper = new FirebaseHelper();

        // Initialize views
        etJumlah = findViewById(R.id.et_jumlah);
        etDeskripsi = findViewById(R.id.et_deskripsi);
        etTanggal = findViewById(R.id.et_tanggal);
        rgTipe = findViewById(R.id.rg_tipe);
        rbPemasukan = findViewById(R.id.rb_pemasukan);
        rbPengeluaran = findViewById(R.id.rb_pengeluaran);
        spinnerKategori = findViewById(R.id.spinner_kategori);
        btnSimpan = findViewById(R.id.btn_simpan);
        btnBatal = findViewById(R.id.btn_batal);

        etJumlah.requestFocus();

        setupKategoriLists();
        updateDateLabel();

        etTanggal.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddTransactionsActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateLabel();
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        setupKategoriSpinner(kategoriPemasukan);

        rgTipe.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_pemasukan) {
                setupKategoriSpinner(kategoriPemasukan);
            } else {
                setupKategoriSpinner(kategoriPengeluaran);
            }
        });

        spinnerKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedKategori = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSimpan.setOnClickListener(v -> saveTransactions());
        btnBatal.setOnClickListener(v -> finish());
    }

    private void setupKategoriLists() {
        kategoriPemasukan = new ArrayList<>();
        kategoriPemasukan.add("Gaji");
        kategoriPemasukan.add("Bonus");
        kategoriPemasukan.add("Hadiah");
        kategoriPemasukan.add("Investasi");
        kategoriPemasukan.add("Lainnya");

        kategoriPengeluaran = new ArrayList<>();
        kategoriPengeluaran.add("Makanan");
        kategoriPengeluaran.add("Transportasi");
        kategoriPengeluaran.add("Belanja");
        kategoriPengeluaran.add("Hiburan");
        kategoriPengeluaran.add("Tagihan");
        kategoriPengeluaran.add("Kesehatan");
        kategoriPengeluaran.add("Pendidikan");
        kategoriPengeluaran.add("Lainnya");
    }

    private void setupKategoriSpinner(List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKategori.setAdapter(adapter);
        selectedKategori = items.get(0);
    }

    private void updateDateLabel() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("id", "ID"));
        etTanggal.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void saveTransactions() {
        String jumlahText = etJumlah.getText().toString().trim();
        String deskripsi = etDeskripsi.getText().toString().trim();
        String tanggalText = etTanggal.getText().toString().trim();

        if (jumlahText.isEmpty()) {
            etJumlah.setError("Jumlah harus diisi");
            return;
        }

        double jumlah;
        try {
            jumlah = Double.parseDouble(jumlahText);
        } catch (NumberFormatException e) {
            etJumlah.setError("Jumlah tidak valid");
            return;
        }

        if (deskripsi.isEmpty()) {
            etDeskripsi.setError("Deskripsi harus diisi");
            return;
        }

        if (tanggalText.isEmpty()) {
            etTanggal.setError("Tanggal harus diisi");
            return;
        }

        String tipe = rbPemasukan.isChecked() ? "pemasukan" : "pengeluaran";
        Date tanggal = selectedDate.getTime();

        Transactions transactions = new Transactions(
                firebaseHelper.getCurrentUserId(),
                jumlah,
                deskripsi,
                selectedKategori,
                tipe,
                tanggal
        );

        firebaseHelper.addTransaksi(transactions)
                .addOnSuccessListener(documentReference -> {
                    showSuccessDialog();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal menyimpan transaksi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Berhasil!")
                .setMessage("Transaksi berhasil disimpan. Tambah lagi?")
                .setPositiveButton("Ya", (dialog, which) -> resetForm())
                .setNegativeButton("Tidak", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void resetForm() {
        etJumlah.setText("");
        etDeskripsi.setText("");
        selectedDate = Calendar.getInstance();
        updateDateLabel();
        spinnerKategori.setSelection(0);
        rgTipe.check(R.id.rb_pemasukan);
        etJumlah.requestFocus();
    }
}