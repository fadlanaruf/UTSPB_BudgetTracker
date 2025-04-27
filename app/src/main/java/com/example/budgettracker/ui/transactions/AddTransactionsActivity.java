package com.example.budgettracker.ui.transactions;

import android.app.DatePickerDialog;
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

        firebaseHelper = new FirebaseHelper();

        etJumlah = findViewById(R.id.et_jumlah);
        etDeskripsi = findViewById(R.id.et_deskripsi);
        etTanggal = findViewById(R.id.et_tanggal);
        rgTipe = findViewById(R.id.rg_tipe);
        rbPemasukan = findViewById(R.id.rb_pemasukan);
        rbPengeluaran = findViewById(R.id.rb_pengeluaran);
        spinnerKategori = findViewById(R.id.spinner_kategori);
        btnSimpan = findViewById(R.id.btn_simpan);
        btnBatal = findViewById(R.id.btn_batal);

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

        rgTipe.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_pemasukan) {
                setupKategoriSpinner(kategoriPemasukan);
            } else {
                setupKategoriSpinner(kategoriPengeluaran);
            }
        });

        setupKategoriSpinner(kategoriPemasukan);

        spinnerKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedKategori = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedKategori = parent.getItemAtPosition(0).toString();
            }
        });

        btnSimpan.setOnClickListener(v -> saveTransactions() );
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
    }

    private void updateDateLabel() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("id", "ID"));
        etTanggal.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void saveTransactions() {
        // Validate input
        if (etJumlah.getText().toString().trim().isEmpty()) {
            etJumlah.setError("Jumlah harus diisi");
            return;
        }

        if (etDeskripsi.getText().toString().trim().isEmpty()) {
            etDeskripsi.setError("Deskripsi harus diisi");
            return;
        }

        double jumlah = Double.parseDouble(etJumlah.getText().toString().trim());
        String deskripsi = etDeskripsi.getText().toString().trim();
        Date tanggal = selectedDate.getTime();
        String tipe = rbPemasukan.isChecked() ? "pemasukan" : "pengeluaran";

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
                    Toast.makeText(AddTransactionsActivity.this, "Transaksi berhasil disimpan", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddTransactionsActivity.this, "Gagal menyimpan transaksi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}