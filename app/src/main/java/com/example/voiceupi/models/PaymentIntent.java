package com.example.voiceupi.models;
public class PaymentIntent {
    private String intent;
    private double amount;
    private String recipient;
    private String rawCommand;

    // Getters and setters
    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public String getRawCommand() { return rawCommand; }
    public void setRawCommand(String rawCommand) { this.rawCommand = rawCommand; }
}
