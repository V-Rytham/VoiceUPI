package com.example.voiceupi.ui;

import java.util.Date;

public class Transaction {
    private String id;
    private String type; // "payment" or "balance_check"
    private double amount;
    private String recipient;
    private Date timestamp;
    private String status; // "success", "pending", "denied"

    public Transaction(String type, double amount, String recipient, String status) {
        this.type = type;
        this.amount = amount;
        this.recipient = recipient;
        this.status = status;
        this.timestamp = new Date();
    }

    // Getters
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getRecipient() { return recipient; }
    public Date getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
}
