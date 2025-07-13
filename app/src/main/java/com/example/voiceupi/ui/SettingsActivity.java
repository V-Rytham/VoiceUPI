package com.example.voiceupi.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.voiceupi.R;

public class SettingsActivity extends AppCompatActivity {

    private EditText etAge, etContact, etThreshold;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        etAge = findViewById(R.id.et_age);
        etContact = findViewById(R.id.et_contact);
        etThreshold = findViewById(R.id.et_threshold);
        Button btnSave = findViewById(R.id.btn_save);

        sharedPreferences = getSharedPreferences("SOS_PREFS", MODE_PRIVATE);

        // Load existing settings
        loadSettings();

        btnSave.setOnClickListener(v -> saveSettings());
    }

    private void loadSettings() {
        etAge.setText(String.valueOf(sharedPreferences.getInt("age", 0)));
        etContact.setText(sharedPreferences.getString("contact", ""));
        etThreshold.setText(String.valueOf(sharedPreferences.getFloat("threshold", 1000.0f)));
    }

    private void saveSettings() {
        int age = Integer.parseInt(etAge.getText().toString());
        String contact = etContact.getText().toString();
        float threshold = Float.parseFloat(etThreshold.getText().toString());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("age", age);
        editor.putString("contact", contact);
        editor.putFloat("threshold", threshold);
        editor.apply();

        Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}