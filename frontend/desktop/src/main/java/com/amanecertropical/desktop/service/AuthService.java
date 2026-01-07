package com.amanecertropical.desktop.service;

import com.amanecertropical.desktop.models.User;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AuthService extends ApiService {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public static class LoginRequest {
        public String email;
        public String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public static class LoginResponse {
        public String token;
        public User user;
    }

    public String login(String email, String password) throws IOException {
        LoginRequest loginRequest = new LoginRequest(email, password);

        String json = getObjectMapper().writeValueAsString(loginRequest);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/auth/login")
                .post(body)
                .build();

        try (Response response = executeRequest(request)) {
            LoginResponse loginResponse = parseResponse(response, LoginResponse.class);
            return loginResponse.token;
        }
    }

    private com.fasterxml.jackson.databind.ObjectMapper getObjectMapper() {
        // This should be inherited from ApiService, but for simplicity
        return new com.fasterxml.jackson.databind.ObjectMapper();
    }
}
