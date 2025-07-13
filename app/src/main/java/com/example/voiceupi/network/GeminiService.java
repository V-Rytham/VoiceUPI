package com.example.voiceupi.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeminiService {
//    @POST("v1beta/models/gemini-pro:generateContent")
//    Call<GeminiResponse> generateContent(
//            @Header("Content-Type") String contentType,
//            @Header("x-goog-api-key") String apiKey,
//            @Body GeminiRequest request
//    );
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    Call<GeminiResponse> generateContent(
            @Query("key") String apiKey,
            @Body GeminiRequest request
    );


}