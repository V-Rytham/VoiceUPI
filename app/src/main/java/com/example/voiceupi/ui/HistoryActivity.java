package com.example.voiceupi.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.voiceupi.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private ListView listView;
    private List<Transaction> transactions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.listView);
        loadTransactions();
        setupAdapter();
    }

    private void loadTransactions() {
        // In real app, load from database
        transactions.add(new Transaction("payment", 500.0, "John", "success"));
        transactions.add(new Transaction("balance_check", 0, "", "success"));
        transactions.add(new Transaction("payment", 1200.0, "Amazon", "pending"));
    }

    private void setupAdapter() {
        ArrayAdapter<Transaction> adapter = new ArrayAdapter<Transaction>(
                this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                transactions
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);

                Transaction t = transactions.get(position);
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, hh:mm a", Locale.US);

                if ("payment".equals(t.getType())) {
                    text1.setText("Paid ₹" + t.getAmount() + " to " + t.getRecipient());
                } else {
                    text1.setText("Balance Check");
                }

                text2.setText(sdf.format(t.getTimestamp()) + " • " + t.getStatus());
                return view;
            }
        };

        listView.setAdapter(adapter);
    }
}