package com.example.voiceupi.ai;

import android.content.Context;

import com.example.voiceupi.BuildConfig;
import com.example.voiceupi.models.PaymentIntent;
import com.example.voiceupi.network.ApiClient;
import com.example.voiceupi.network.GeminiResponse;
import com.example.voiceupi.repository.GeminiRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class VoiceCommandParser {
    private final Context context;

    public interface ParseCallback {
        void onSuccess(PaymentIntent intent);
        void onError(String error);
    }

    public VoiceCommandParser(Context context) {
        this.context = context;
    }

    public void parseCommand(String command, ParseCallback callback) {
        new Thread(() -> {
            String apiKey = BuildConfig.GEMINI_API_KEY;
            GeminiRepository repository = new GeminiRepository(ApiClient.getGeminiService());

            try {
                retrofit2.Response<GeminiResponse> response = repository.parseCommand(apiKey, command);

                if (response.isSuccessful()) {
                    GeminiResponse body = response.body();
                    if (body != null && body.getCandidates() != null && !body.getCandidates().isEmpty()) {
                        String jsonText = body.getCandidates().get(0).getContent().getParts().get(0).getText();
                        jsonText = jsonText.replace("```json", "").replace("```", "").trim();
                        parseJsonResponse(jsonText, command, callback);
                    } else {
                        callback.onError("Empty response from Gemini");
                    }
                } else {
                    callback.onError("API error: " + response.code());
                }
            } catch (IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }
        }).start();
    }

    private void parseJsonResponse(String jsonText, String command, ParseCallback callback) {
        try {
            JSONObject json = new JSONObject(jsonText);
            PaymentIntent intent = new PaymentIntent();
            intent.setIntent(json.getString("intent"));
            intent.setAmount(json.optDouble("amount", 0.0));
            intent.setRecipient(json.optString("recipient", ""));
            intent.setRawCommand(command);
            callback.onSuccess(intent);
        } catch (JSONException e) {
            callback.onError("JSON parsing error: " + e.getMessage());
        }
    }

}