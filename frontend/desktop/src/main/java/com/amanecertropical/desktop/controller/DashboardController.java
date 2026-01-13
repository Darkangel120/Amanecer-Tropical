package com.amanecertropical.desktop.controller;

import com.amanecertropical.desktop.MainApp;
import com.amanecertropical.desktop.api.ApiClient;
import com.amanecertropical.desktop.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.google.gson.reflect.TypeToken;

public class DashboardController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    @FXML private HBox menuBar;
    @FXML private Button dashboardBtn;
    @FXML private Button reservationsBtn;
    @FXML private Button paymentsBtn;
    @FXML private Button usersBtn;
    @FXML private Label userRoleLabel;
    @FXML private VBox contentArea;
    @FXML private VBox dashboardContent;
    @FXML private VBox statsBox;
    @FXML private Label totalReservationsLabel;
    @FXML private Label totalPaymentsLabel;
    @FXML private Label totalUsersLabel;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private BarChart<String, Number> statsChart;

    private ApiClient apiClient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        apiClient = new ApiClient();
        // Set auth token from session
        String token = SessionManager.getInstance().getAuthToken();
        if (token != null) {
            apiClient.setAuthToken(token);
        }

        String userName = SessionManager.getInstance().getCurrentUser().getNombre();
        welcomeLabel.setText("Bienvenido, " + userName);

        String role = SessionManager.getInstance().getCurrentUser().getRol();
        userRoleLabel.setText("Rol: " + role);

        if ("ADMIN".equalsIgnoreCase(role)) {
            usersBtn.setVisible(true);
        }

        // Initialize chart
        xAxis.setLabel("Categorías");
        yAxis.setLabel("Cantidad");
        statsChart.setTitle("Estadísticas del Sistema");

        // Set categories for the chart
        xAxis.getCategories().addAll("Reservaciones", "Pagos", "Usuarios");

        loadDashboardStats();
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        try {
            MainApp.showLoginView();
        } catch (IOException e) {
            logger.error("Error showing login view", e);
        }
    }

    @FXML
    private void showDashboard() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(dashboardContent);
        dashboardContent.setVisible(true);
        logger.info("Dashboard view loaded");
    }

    @FXML
    private void showReservations() {
        try {
            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReservationListView.fxml"));
            VBox reservationsView = loader.load();
            contentArea.getChildren().add(reservationsView);
            logger.info("Reservations view loaded");
        } catch (IOException e) {
            logger.error("Error loading reservations view", e);
        }
    }

    @FXML
    private void showPayments() {
        try {
            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PaymentListView.fxml"));
            VBox paymentsView = loader.load();
            contentArea.getChildren().add(paymentsView);
            logger.info("Payments view loaded");
        } catch (IOException e) {
            logger.error("Error loading payments view", e);
        }
    }

    @FXML
    private void showUsers() {
        try {
            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserListView.fxml"));
            VBox usersView = loader.load();
            contentArea.getChildren().add(usersView);
            logger.info("Users view loaded");
        } catch (IOException e) {
            logger.error("Error loading users view", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadDashboardStats() {
        new Thread(() -> {
            try {
                // Get reservations count
                int reservationsCount = 0;
                try {
                    Type listType = new TypeToken<List<Object>>(){}.getType();
                    var reservationsResponse = apiClient.get("/reservations", listType);
                    if (reservationsResponse.isSuccess()) {
                        List<Object> reservations = (List<Object>) reservationsResponse.getData();
                        reservationsCount = reservations != null ? reservations.size() : 0;
                    }
                } catch (Exception e) {
                    logger.warn("Error fetching reservations count", e);
                }

                // Get payments count
                int paymentsCount = 0;
                try {
                    Type listType = new TypeToken<List<Object>>(){}.getType();
                    var paymentsResponse = apiClient.get("/payments", listType);
                    if (paymentsResponse.isSuccess()) {
                        List<Object> payments = (List<Object>) paymentsResponse.getData();
                        paymentsCount = payments != null ? payments.size() : 0;
                    }
                } catch (Exception e) {
                    logger.warn("Error fetching payments count", e);
                }

                // Get users count
                int usersCount = 0;
                try {
                    Type listType = new TypeToken<List<Object>>(){}.getType();
                    var usersResponse = apiClient.get("/users", listType);
                    if (usersResponse.isSuccess()) {
                        List<Object> users = (List<Object>) usersResponse.getData();
                        usersCount = users != null ? users.size() : 0;
                    }
                } catch (Exception e) {
                    logger.warn("Error fetching users count", e);
                }

                int finalReservationsCount = reservationsCount;
                int finalPaymentsCount = paymentsCount;
                int finalUsersCount = usersCount;

                Platform.runLater(() -> {
                    totalReservationsLabel.setText(String.valueOf(finalReservationsCount));
                    totalPaymentsLabel.setText(String.valueOf(finalPaymentsCount));
                    totalUsersLabel.setText(String.valueOf(finalUsersCount));

                    // Update chart
                    updateChart(finalReservationsCount, finalPaymentsCount, finalUsersCount);
                });
            } catch (Exception e) {
                logger.error("Error loading dashboard stats", e);
                Platform.runLater(() -> {
                    totalReservationsLabel.setText("Error");
                    totalPaymentsLabel.setText("Error");
                    totalUsersLabel.setText("Error");
                });
            }
        }).start();
    }

    private void updateChart(int reservations, int payments, int users) {
        Platform.runLater(() -> {
            logger.info("Updating chart with data - Reservations: {}, Payments: {}, Users: {}", reservations, payments, users);

            statsChart.getData().clear();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Estadísticas");

            series.getData().add(new XYChart.Data<>("Reservaciones", reservations));
            series.getData().add(new XYChart.Data<>("Pagos", payments));
            series.getData().add(new XYChart.Data<>("Usuarios", users));

            statsChart.getData().add(series);

            // Force chart refresh
            statsChart.layout();
            statsChart.requestLayout();

            logger.info("Chart updated successfully");
        });
    }
}
