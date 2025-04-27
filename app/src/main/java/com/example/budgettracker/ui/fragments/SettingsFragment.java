package com.example.budgettracker.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.budgettracker.ui.auth.LoginActivity;
import com.example.budgettracker.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    private Switch themeSwitch;
    private SharedPreferences preferences;
    private static final String PREF_NAME = "settings_pref";
    private static final String KEY_THEME = "dark_mode";

    public SettingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        view.setAlpha(0f);
        view.animate().alpha(1f).setDuration(300).start();

        themeSwitch = view.findViewById(R.id.switch_theme);
        Button logoutBtn = view.findViewById(R.id.btn_logout);

        preferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        boolean isDark = preferences.getBoolean(KEY_THEME, false);
        themeSwitch.setChecked(isDark);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_THEME, isChecked);
            editor.apply();

            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        logoutBtn.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logout berhasil", Toast.LENGTH_SHORT).show();

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
            requireActivity().finish();
        });

        return view;
    }
}
