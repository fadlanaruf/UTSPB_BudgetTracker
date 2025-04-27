package com.example.budgettracker.ui.fragments;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.budgettracker.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountSettingsActivity extends AppCompatActivity {

    private EditText nameEdit, emailEdit, phoneEdit;
    private TextInputLayout nameLayout, emailLayout, phoneLayout;
    private Button saveButton, changePasswordButton;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        // Initialize Firebase Auth and Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Pengaturan Akun");
        }

        // Initialize views
        nameEdit = findViewById(R.id.et_name);
        emailEdit = findViewById(R.id.et_email);
        phoneEdit = findViewById(R.id.et_phone);
        nameLayout = findViewById(R.id.til_name);
        emailLayout = findViewById(R.id.til_email);
        phoneLayout = findViewById(R.id.til_phone);
        saveButton = findViewById(R.id.btn_save);
        changePasswordButton = findViewById(R.id.btn_change_password);
        progressBar = findViewById(R.id.progress_bar);

        // Load user data
        loadUserData();

        // Setup click listeners
        saveButton.setOnClickListener(v -> saveUserData());
        changePasswordButton.setOnClickListener(v -> sendPasswordResetEmail());

        // Profile picture click listener
        ImageView profilePic = findViewById(R.id.iv_profile);
        profilePic.setOnClickListener(v -> {
            Toast.makeText(this, "Fitur ubah foto profil akan segera hadir", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserData() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            // Set email (can't be changed in Firebase without re-authentication)
            emailEdit.setText(user.getEmail());
            emailEdit.setEnabled(false);

            // Set display name
            if (user.getDisplayName() != null) {
                nameEdit.setText(user.getDisplayName());
            }

            // Get additional user data from Firestore
            firestore.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        progressBar.setVisibility(View.GONE);
                        if (documentSnapshot.exists()) {
                            // Get phone number if exists
                            String phone = documentSnapshot.getString("phone");
                            if (phone != null) {
                                phoneEdit.setText(phone);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AccountSettingsActivity.this,
                                "Gagal memuat data: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        } else {
            progressBar.setVisibility(View.GONE);
            finish(); // If no user, close activity
        }
    }

    private void saveUserData() {
        String name = nameEdit.getText().toString().trim();
        String phone = phoneEdit.getText().toString().trim();

        // Validate inputs
        boolean isValid = true;

        if (name.isEmpty()) {
            nameLayout.setError("Nama tidak boleh kosong");
            isValid = false;
        } else {
            nameLayout.setError(null);
        }

        if (phone.isEmpty()) {
            phoneLayout.setError("Nomor telepon tidak boleh kosong");
            isValid = false;
        } else {
            phoneLayout.setError(null);
        }

        if (!isValid) return;

        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            // Update display name in Firebase Auth
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnSuccessListener(aVoid -> {
                        // Update additional info in Firestore
                        DocumentReference userRef = firestore.collection("users").document(user.getUid());

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("name", name);
                        updates.put("phone", phone);

                        userRef.update(updates)
                                .addOnSuccessListener(aVoid1 -> {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(AccountSettingsActivity.this,
                                            "Profil berhasil diperbarui",
                                            Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(AccountSettingsActivity.this,
                                            "Gagal memperbarui data: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AccountSettingsActivity.this,
                                "Gagal memperbarui profil: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void sendPasswordResetEmail() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.sendPasswordResetEmail(user.getEmail())
                    .addOnSuccessListener(aVoid -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AccountSettingsActivity.this,
                                "Email reset password telah dikirim ke " + user.getEmail(),
                                Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AccountSettingsActivity.this,
                                "Gagal mengirim email reset: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}