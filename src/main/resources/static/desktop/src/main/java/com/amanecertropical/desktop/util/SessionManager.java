package com.amanecertropical.desktop.util;

import com.amanecertropical.desktop.api.ApiClient;
import com.amanecertropical.desktop.model.User;


public class SessionManager {

    private static SessionManager instance;
    private String authToken;
    private User currentUser;
    private final ApiClient apiClient;

    private SessionManager() {
        this.apiClient = new ApiClient();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
        this.apiClient.setAuthToken(token);
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public boolean isLoggedIn() {
        return authToken != null && currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    public boolean isEmployee() {
        return currentUser != null && ("ADMIN".equals(currentUser.getRole()) || "EMPLEADO".equals(currentUser.getRole()));
    }

    public void logout() {
        this.authToken = null;
        this.currentUser = null;
        this.apiClient.clearAuthToken();
    }
}
