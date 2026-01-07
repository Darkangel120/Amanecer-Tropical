package com.amanecertropical.desktop.service;

import com.amanecertropical.desktop.models.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class ApiService {
    private static final String BASE_URL = "http://localhost:8080/api"; // Adjust as needed
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String authToken;

    protected String getBaseUrl() {
        return BASE_URL;
    }

    protected Response executeRequest(Request request) throws IOException {
        return client.newCall(request).execute();
    }

    protected <T> T parseResponse(Response response, Class<T> clazz) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException("Request failed: " + response.message());
        }
        return objectMapper.readValue(response.body().string(), clazz);
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    private Request.Builder getAuthenticatedRequestBuilder() {
        return new Request.Builder()
                .addHeader("Authorization", "Bearer " + authToken);
    }

    // Authentication
    public String login(String email, String password) throws IOException {
        String json = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(BASE_URL + "/auth/login")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Login failed: " + response.message());
            }
            String responseBody = response.body().string();
            // Assuming response has "token" field
            return objectMapper.readTree(responseBody).get("token").asText();
        }
    }

    // Users
    public List<User> getUsers() throws IOException {
        Request request = getAuthenticatedRequestBuilder()
                .url(BASE_URL + "/users")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get users: " + response.message());
            }
            return objectMapper.readValue(response.body().string(), new TypeReference<List<User>>() {});
        }
    }

    public User createUser(User user) throws IOException {
        String json = objectMapper.writeValueAsString(user);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = getAuthenticatedRequestBuilder()
                .url(BASE_URL + "/users")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to create user: " + response.message());
            }
            return objectMapper.readValue(response.body().string(), User.class);
        }
    }

    public User updateUser(Long id, User user) throws IOException {
        String json = objectMapper.writeValueAsString(user);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = getAuthenticatedRequestBuilder()
                .url(BASE_URL + "/users/" + id)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to update user: " + response.message());
            }
            return objectMapper.readValue(response.body().string(), User.class);
        }
    }

    public void deleteUser(Long id) throws IOException {
        Request request = getAuthenticatedRequestBuilder()
                .url(BASE_URL + "/users/" + id)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to delete user: " + response.message());
            }
        }
    }

    // Destinations
    public List<Destination> getDestinations() throws IOException {
        Request request = getAuthenticatedRequestBuilder()
                .url(BASE_URL + "/destinations")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get destinations: " + response.message());
            }
            return objectMapper.readValue(response.body().string(), new TypeReference<List<Destination>>() {});
        }
    }

    public Destination createDestination(Destination destination) throws IOException {
        String json = objectMapper.writeValueAsString(destination);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = getAuthenticatedRequestBuilder()
                .url(BASE_URL + "/destinations")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to create destination: " + response.message());
            }
            return objectMapper.readValue(response.body().string(), Destination.class);
        }
    }

    public Destination updateDestination(Long id, Destination destination) throws IOException {
        String json = objectMapper.writeValueAsString(destination);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = getAuthenticatedRequestBuilder()
                .url(BASE_URL + "/destinations/" + id)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to update destination: " + response.message());
            }
            return objectMapper.readValue(response.body().string(), Destination.class);
        }
    }

    public void deleteDestination(Long id) throws IOException {
        Request request = getAuthenticatedRequestBuilder()
                .url(BASE_URL + "/destinations/" + id)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to delete destination: " + response.message());
            }
        }
    }

    // Flights
    public List<Flight> getFlights() throws IOException {
        Request request = getAuthenticatedRequestBuilder()
                .url(BASE_URL + "/flights")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get flights: " + response.message());
            }
            return objectMapper.readValue(response.body().string(), new TypeReference<List<Flight>>() {});
        }
    }

    public Flight createFlight(Flight flight) throws IOException {
        String json = objectMapper.writeValueAsString(flight);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = getAuthenticatedRequestBuilder()
                .url(BASE_URL + "/flights")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to create flight: " + response.message());
            }
            return objectMapper.readValue(response.body().string(), Flight.class);
        }
    }

    public Flight updateFlight(Long id, Flight flight) throws IOException {
        String json = objectMapper.writeValueAsString(flight);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = getAuthenticatedRequestBuilder()
                .url(BASE_URL + "/flights/" + id)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to update flight: " + response.message());
            }
            return objectMapper.readValue(response.body().string(), Flight.class);
        }
    }

    public void deleteFlight(Long id) throws IOException {
        Request request = getAuthenticatedRequestBuilder()
                .url(BASE_URL + "/flights/" + id)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to delete flight: " + response.message());
            }
        }
    }

    // Hotels
    public List<Hotel> getHotels() throws IOException {
        Request request = getAuthenticatedRequestBuilder()
                .url(BASE_URL + "/hotels")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get hotels: " + response.message());
            }
            return objectMapper.readValue(response.body().string(), new TypeReference<List<Hotel>>() {});
        }
    }

    public Hotel createHotel(Hotel hotel) throws IOException {
        String json = objectMapper.writeValueAsString(hotel);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = getAuthenticatedRequestBuilder()
                .url(BASE_URL + "/hotels")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to create hotel: " + response.message());
            }
            return objectMapper.readValue(response.body().string(), Hotel.class);
        }
    }

    public Hotel updateHotel(Long id, Hotel hotel) throws IOException {
        String json = objectMapper.writeValueAsString(hotel);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = getAuthenticatedRequestBuilder()
                .url(BASE_URL + "/hotels/" + id)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to update hotel: " + response.message());
            }
            return objectMapper.readValue(response.body().string(), Hotel.class);
        }
    }

    public void deleteHotel(Long id) throws IOException {
        Request request = getAuthenticatedRequestBuilder()
                .url(BASE_URL + "/hotels/" + id)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to delete hotel: " + response.message());
            }
        }
    }

    // Reservations
    public List<Reservation> getReservations() throws IOException {
        Request request = getAuthenticatedRequestBuilder()
                .url(BASE_URL + "/reservations")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get reservations: " + response.message());
            }
            return objectMapper.readValue(response.body().string(), new TypeReference<List<Reservation>>() {});
        }
    }

    public Reservation createReservation(Reservation reservation) throws IOException {
        String json = objectMapper.writeValueAsString(reservation);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = getAuthenticatedRequestBuilder()
                .url(BASE_URL + "/reservations")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to create reservation: " + response.message());
            }
            return objectMapper.readValue(response.body().string(), Reservation.class);
        }
    }

    public Reservation updateReservation(Long id, Reservation reservation) throws IOException {
        String json = objectMapper.writeValueAsString(reservation);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = getAuthenticatedRequestBuilder()
                .url(BASE_URL + "/reservations/" + id)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to update reservation: " + response.message());
            }
            return objectMapper.readValue(response.body().string(), Reservation.class);
        }
    }

    public void deleteReservation(Long id) throws IOException {
        Request request = getAuthenticatedRequestBuilder()
                .url(BASE_URL + "/reservations/" + id)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to delete reservation: " + response.message());
            }
        }
    }
}
