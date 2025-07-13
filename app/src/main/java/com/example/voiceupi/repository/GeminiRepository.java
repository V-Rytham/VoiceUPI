package com.example.voiceupi.repository;

import com.example.voiceupi.network.GeminiRequest;
import com.example.voiceupi.network.GeminiResponse;
import com.example.voiceupi.network.GeminiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class GeminiRepository {
    private final GeminiService service;

    public GeminiRepository(GeminiService service) {
        this.service = service;
    }

    public Response<GeminiResponse> parseCommand(String apiKey, String command) throws IOException {
        String prompt = "Extract payment intent from this voice command: \"" + command + "\"\n\n" +
                "Respond ONLY with JSON in this format:\n" +
                "{\n" +
                "  \"intent\": \"make_payment\" or \"check_balance\",\n" +
                "  \"amount\": number or 0,\n" +
                "  \"recipient\": \"name\" or \"\"\n" +
                "}";

        GeminiRequest request = createRequest(prompt);

        // ✅ This matches your current GeminiService method signature
        return service.generateContent(apiKey, request).execute();
    }


    private GeminiRequest createRequest(String prompt) {
        GeminiRequest.Part part = new GeminiRequest.Part(prompt);
        List<GeminiRequest.Part> parts = new ArrayList<>();
        parts.add(part);

        GeminiRequest.Content content = new GeminiRequest.Content(parts);
        List<GeminiRequest.Content> contents = new ArrayList<>();
        contents.add(content);

        return new GeminiRequest(contents);
    }
}
