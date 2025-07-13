package com.example.voiceupi.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.ScanContract;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.voiceupi.R;
import com.example.voiceupi.ai.VoiceCommandParser;
import com.example.voiceupi.models.PaymentIntent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;


public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 300;
    private static final int SMS_PERMISSION_CODE = 101;
    private static final int REQUEST_PERMISSION = 200;
    private static final int REQUEST_SPEECH = 100;

    private PaymentIntent pendingSosIntent;
    private FloatingActionButton fabMic;
    private android.app.ProgressDialog progressDialog;
    private List<Transaction> transactions = new ArrayList<>();

    private static final String SOS_MESSAGE_TEMPLATE =
            "SOS ALERT: %1$s is attempting to send ₹%2$.2f to %3$s. " +
                    "Reply 'ALLOW' to approve or 'DENY' to reject.";

    // ActivityResultLauncher for QR scanner - only declared once
    private final ActivityResultLauncher<com.journeyapps.barcodescanner.ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    handleQrResult(result.getContents());
                } else {
                    Toast.makeText(MainActivity.this, "Scan cancelled", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fabQr = findViewById(R.id.fab_center_circle);
        fabQr.setOnClickListener(v -> {
            if (hasCameraPermission()) {
                startQrScanner();
            } else {
                requestCameraPermission();
            }
        });

        progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Processing command...");
        progressDialog.setCancelable(false);

        ImageView btnHistory = findViewById(R.id.btn_history);
        btnHistory.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HistoryActivity.class)));

        ImageView btnSettings = findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_PERMISSION
            );
        }

        fabMic = findViewById(R.id.fab_mic);
        fabMic.setOnClickListener(view -> startVoiceInput());
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    private void startQrScanner() {
        com.journeyapps.barcodescanner.ScanOptions options = new com.journeyapps.barcodescanner.ScanOptions();
        options.setPrompt("Scan UPI QR Code");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setBarcodeImageEnabled(true);
        barcodeLauncher.launch(options);
    }

    private void handleQrResult(String scannedData) {
        if (scannedData.startsWith("upi://pay")) {
            Uri uri = Uri.parse(scannedData);
            String payeeName = uri.getQueryParameter("pn");
            String payeeUpi = uri.getQueryParameter("pa");
            String amountStr = uri.getQueryParameter("am");
            float amount = 0f;
            try {
                if (amountStr != null) {
                    amount = Float.parseFloat(amountStr);
                }
            } catch (NumberFormatException ignored) {
            }

            PaymentIntent intent = new PaymentIntent();
            intent.setIntent("make_payment");
            intent.setRecipient(payeeName != null ? payeeName : payeeUpi);
            intent.setAmount(amount);

            runOnUiThread(() -> showConfirmationDialog(intent));
        } else {
            Toast.makeText(this, "Invalid QR code scanned", Toast.LENGTH_SHORT).show();
        }
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say payment command...");
        startActivityForResult(intent, REQUEST_SPEECH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SPEECH && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);

                progressDialog.show();

                VoiceCommandParser parser = new VoiceCommandParser(this);
                parser.parseCommand(spokenText, new VoiceCommandParser.ParseCallback() {
                    @Override
                    public void onSuccess(PaymentIntent paymentIntent) {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            handleIntent(paymentIntent);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "AI error: " + error, Toast.LENGTH_LONG).show();
                        });
                    }
                });
            }
        }
    }

    private void handleIntent(PaymentIntent intent) {
        if ("make_payment".equals(intent.getIntent())) {
            handlePaymentIntent(intent);
        } else if ("check_balance".equals(intent.getIntent())) {
            handleBalanceIntent();
        } else {
            Toast.makeText(this, "Unknown command", Toast.LENGTH_SHORT).show();
        }
    }

    private void handlePaymentIntent(PaymentIntent intent) {
        SharedPreferences prefs = getSharedPreferences("SOS_PREFS", MODE_PRIVATE);
        int age = prefs.getInt("age", 0);
        float threshold = prefs.getFloat("threshold", 1000.0f);
        String contact = prefs.getString("contact", null);

        pendingSosIntent = intent;

        if (age > 60 && intent.getAmount() > threshold && contact != null) {
            showSOSAlert(intent, contact);
        } else {
            showConfirmationDialog(intent);
        }
    }

    private void showConfirmationDialog(PaymentIntent intent) {
        if (intent.getAmount() <= 0 || intent.getRecipient() == null) {
            Toast.makeText(this, "Missing payment details", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm Payment")
                .setMessage("Send ₹" + intent.getAmount() + " to " + intent.getRecipient() + "?")
                .setPositiveButton("Pay", (dialog, which) -> openUPI(intent))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void showSOSAlert(PaymentIntent intent, String contact) {
        sendSosAlert(intent, contact);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("SOS Alert Sent!")
                .setMessage("Waiting for guardian approval...")
                .setCancelable(false)
                .show();

        BroadcastReceiver responseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String response = intent.getStringExtra("response");
                alertDialog.dismiss();

                if ("ALLOW".equalsIgnoreCase(response)) {
                    openUPI(pendingSosIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Payment denied by guardian", Toast.LENGTH_SHORT).show();
                }
                pendingSosIntent = null;
                unregisterReceiver(this);
            }
        };

        IntentFilter filter = new IntentFilter("SOS_RESPONSE_ACTION");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(responseReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(responseReceiver, filter);
        }

        // 5 minute timeout
        new Handler().postDelayed(() -> {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
                Toast.makeText(MainActivity.this,
                        "No response from guardian. Payment cancelled.",
                        Toast.LENGTH_SHORT).show();
                unregisterReceiver(responseReceiver);
                pendingSosIntent = null;
            }
        }, 5 * 60 * 1000);
    }

    private void sendSosAlert(PaymentIntent intent, String phoneNumber) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_CODE);
            Toast.makeText(this, "SMS permission not granted yet", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String message = String.format(
                    SOS_MESSAGE_TEMPLATE,
                    "User",
                    intent.getAmount(),
                    intent.getRecipient()
            );

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);

            SharedPreferences prefs = getSharedPreferences("SOS_PREFS", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("last_sos_time", System.currentTimeMillis());
            editor.apply();

            pendingSosIntent = null;

            Log.d("SOS", "Contact: " + phoneNumber);
            Log.d("SOS", "Message: " + message);
            Toast.makeText(this, "Sending SMS to: " + phoneNumber + "\nMsg: " + message, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "SMS failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveTransaction(Transaction transaction) {
        transactions.add(0, transaction);
        // Implement DB saving later
    }

    private void openUPI(PaymentIntent intent) {
        try {
            String amount = (intent.getAmount() == (int) intent.getAmount()) ?
                    String.valueOf((int) intent.getAmount()) :
                    String.valueOf(intent.getAmount());

            String upiId = getUpiIdForRecipient(intent.getRecipient());

            String upiDeepLink = "intent://pay?" +
                    "pa=" + URLEncoder.encode(upiId, "UTF-8") +
                    "&pn=" + URLEncoder.encode(intent.getRecipient(), "UTF-8") +
                    "&am=" + URLEncoder.encode(amount, "UTF-8") +
                    "&cu=INR&tn=VoiceCommandPayment#Intent;" +
                    "scheme=upi;package=com.google.android.apps.nbu.paisa.user;end";

            Intent paymentIntent = Intent.parseUri(upiDeepLink, Intent.URI_INTENT_SCHEME);
            startActivity(paymentIntent);
            saveTransaction(new Transaction("payment", intent.getAmount(), intent.getRecipient(), "pending"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "No UPI app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleBalanceIntent() {
        saveTransaction(new Transaction("balance_check", 0, "", "success"));

        new AlertDialog.Builder(this)
                .setTitle("Account Balance")
                .setMessage("Your current balance is ₹8,450")
                .setPositiveButton("OK", null)
                .show();
    }

    private String getUpiIdForRecipient(String recipientName) {
        String lower = recipientName.toLowerCase();

        switch (lower) {
            case "mummy":
            case "mom":
                return "yourmom@okaxis";
            case "ramesh":
                return "ramesh@oksbi";
            case "suresh":
                return "suresh@okhdfcbank";
            case "rahul":
                return "rahul123@okicici";
            default:
                return "test@upi";
        }
    }

    // Remember to implement Transaction class or import it

    // Also ensure you add necessary imports for any missing classes.
}
