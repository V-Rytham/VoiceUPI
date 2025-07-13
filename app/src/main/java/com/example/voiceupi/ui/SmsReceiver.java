package com.example.voiceupi.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
    private static final String ALLOW_KEYWORD = "ALLOW";
    private static final String DENY_KEYWORD = "DENY";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                    String sender = sms.getDisplayOriginatingAddress();
                    String messageBody = sms.getMessageBody().toUpperCase();

                    // Check if this is a response to our SOS alert
                    if (messageBody.contains(ALLOW_KEYWORD) || messageBody.contains(DENY_KEYWORD)) {
                        handleSosResponse(context, sender, messageBody);
                    }
                }
            }
        }
    }

    private void handleSosResponse(Context context, String sender, String message) {
        // Get the saved SOS contact from preferences
        SharedPreferences prefs = context.getSharedPreferences("SOS_PREFS", Context.MODE_PRIVATE);
        String sosContact = prefs.getString("contact", "");

        // Verify the sender is the SOS contact
        if (sosContact.contains(sender)) {
            Intent responseIntent = new Intent("SOS_RESPONSE_ACTION");

            if (message.contains(ALLOW_KEYWORD)) {
                responseIntent.putExtra("response", "ALLOW");
                Toast.makeText(context, "Payment Approved by Guardian", Toast.LENGTH_SHORT).show();
            } else if (message.contains(DENY_KEYWORD)) {
                responseIntent.putExtra("response", "DENY");
                Toast.makeText(context, "Payment Denied by Guardian", Toast.LENGTH_SHORT).show();
            }

            context.sendBroadcast(responseIntent);
        }
    }
}