package com.amanecertropical.desktop.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.gson.reflect.TypeToken;

public class ApiClient {

    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);

    private static final String BASE_URL = "http://localhost:8080/api";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final Gson gson;
    private String authToken;

    public ApiClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        this.gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public void clearAuthToken() {
        this.authToken = null;
    }

    private Request.Builder createRequest(String url) {
        Request.Builder builder = new Request.Builder().url(BASE_URL + url);
        if (authToken != null) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }
        return builder;
    }

    public <T> ApiResponse<T> get(String endpoint, Class<T> responseClass) throws IOException {
        Request request = createRequest(endpoint).build();

        try (Response response = client.newCall(request).execute()) {
            return handleResponse(response, responseClass);
        }
    }

    public <T> ApiResponse<T> get(String endpoint, Type responseType) throws IOException {
        Request request = createRequest(endpoint).build();

        try (Response response = client.newCall(request).execute()) {
            return handleResponse(response, responseType);
        }
    }

    public <T> ApiResponse<T> post(String endpoint, Object body, Class<T> responseClass) throws IOException {
        String jsonBody = gson.toJson(body);
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        Request request = createRequest(endpoint)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return handleResponse(response, responseClass);
        }
    }

    public <T> ApiResponse<T> post(String endpoint, Object body, Type responseType) throws IOException {
        String jsonBody = gson.toJson(body);
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        Request request = createRequest(endpoint)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return handleResponse(response, responseType);
        }
    }

    public <T> ApiResponse<T> put(String endpoint, Object body, Class<T> responseClass) throws IOException {
        String jsonBody = gson.toJson(body);
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        Request request = createRequest(endpoint)
                .put(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return handleResponse(response, responseClass);
        }
    }

    public ApiResponse<Void> delete(String endpoint) throws IOException {
        Request request = createRequest(endpoint)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            return handleResponse(response, Void.class);
        }
    }

    private <T> ApiResponse<T> handleResponse(Response response, Class<T> responseClass) throws IOException {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(response.code());

        String responseBody = response.body() != null ? response.body().string() : "";

        if (response.isSuccessful()) {
            if (responseClass != Void.class && !responseBody.isEmpty()) {
                T data = gson.fromJson(responseBody, responseClass);
                apiResponse.setData(data);
            }
            apiResponse.setSuccess(true);
        } else {
            apiResponse.setSuccess(false);
            apiResponse.setErrorMessage(responseBody);
            logger.error("API request failed: {} - {}", response.code(), responseBody);
        }

        return apiResponse;
    }

    private <T> ApiResponse<T> handleResponse(Response response, Type responseType) throws IOException {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(response.code());

        String responseBody = response.body() != null ? response.body().string() : "";

        if (response.isSuccessful()) {
            if (!responseBody.isEmpty()) {
                T data = gson.fromJson(responseBody, responseType);
                apiResponse.setData(data);
            }
            apiResponse.setSuccess(true);
        } else {
            apiResponse.setSuccess(false);
            apiResponse.setErrorMessage(responseBody);
            logger.error("API request failed: {} - {}", response.code(), responseBody);
        }

        return apiResponse;
    }

    public String getBaseUrl() {
        return BASE_URL;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public Gson getGson() {
        return gson;
    }

    @SuppressWarnings("unchecked")
    public ApiResponse<Map<String, Object>> login(String email, String password) throws IOException {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", email);
        loginRequest.put("password", password);

        Type responseType = new TypeToken<Map<String, Object>>(){}.getType();
        return (ApiResponse<Map<String, Object>>) (ApiResponse<?>) post("/auth/login", loginRequest, responseType);
    }
}
