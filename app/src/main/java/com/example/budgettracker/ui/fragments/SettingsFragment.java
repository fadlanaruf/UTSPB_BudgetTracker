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
import com.example.budgettracker.ui.activities.AccountSettingsActivity;
import com.example.budgettracker.ui.activities.InfoActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    private Switch themeSwitch;
    private SharedPreferences preferences;
    private static final String PREF_NAME = "settings_pref";
    private static final String KEY_THEME = "dark_mode";
    private boolean isThemeChanging = false;

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

        int currentNightMode = AppCompatDelegate.getDefaultNightMode();
        boolean isDark = currentNightMode == AppCompatDelegate.MODE_NIGHT_YES;

        if (preferences.getBoolean(KEY_THEME, false) != isDark) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_THEME, isDark);
            editor.apply();
        }

        themeSwitch.setChecked(isDark);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) { // Only respond to user input, not programmatic changes
                isThemeChanging = true;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(KEY_THEME, isChecked);
                editor.apply();

                SharedPreferences navPref = requireActivity().getSharedPreferences("nav_pref", Context.MODE_PRIVATE);
                navPref.edit().putString("last_fragment", "settings").apply();

                AppCompatDelegate.setDefaultNightMode(
                        isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
                );
            }
        });

        logoutBtn.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logout berhasil", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
            requireActivity().finish();
        });

        view.findViewById(R.id.account_settings).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AccountSettingsActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.help_section).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), InfoActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isThemeChanging) {
            isThemeChanging = false;
            int currentNightMode = AppCompatDelegate.getDefaultNightMode();
            boolean isDark = currentNightMode == AppCompatDelegate.MODE_NIGHT_YES;
            themeSwitch.setChecked(isDark);
        }
    }
}