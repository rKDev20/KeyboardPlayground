package com.rk.keyboardplayground.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rk.keyboardplayground.R;
import com.rk.keyboardplayground.customViews.KeyboardLayout;
import com.rk.keyboardplayground.util.SPManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.enable).setOnClickListener(v -> enableMyKeyboard());
        setupSize();
    }

    private void setupSize() {
        RadioGroup group = findViewById(R.id.size);
        switch (SPManager.getSize(this)) {
            case MEDIUM:
                group.check(R.id.medium);
                break;
            case SMALL:
                group.check(R.id.small);
                break;
            case LARGE:
                group.check(R.id.large);
        }
        group.setOnCheckedChangeListener((group1, checkedId) -> {
            switch (checkedId) {
                case R.id.small:
                    SPManager.setSize(this, KeyboardLayout.Size.SMALL);
                    break;
                case R.id.large:
                    SPManager.setSize(this, KeyboardLayout.Size.LARGE);
                    break;
                case R.id.medium:
                    SPManager.setSize(this, KeyboardLayout.Size.MEDIUM);
            }
        });
    }

    void enableMyKeyboard() {
        InputMethodManager imeManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        boolean enabled = false;
        List<InputMethodInfo> listOfKeyboards = imeManager.getEnabledInputMethodList();
        for (InputMethodInfo i : listOfKeyboards) {
            if (i.getPackageName().compareTo(getPackageName()) == 0) {
                if (i.getId().equals(Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD))) {
                    Toast.makeText(MainActivity.this, "Keyboard is enabled", Toast.LENGTH_SHORT).show();
                    return;
                }
                enabled = true;
                break;
            }
        }
        if (!enabled) {
            Intent enableIntent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
            enableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableIntent);
        } else
            imeManager.showInputMethodPicker();
    }
}