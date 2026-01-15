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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.image.Image;

import com.google.gson.reflect.TypeToken;

public class DashboardController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @FXML private Label welcomeLabel;
    @FXML private HBox menuBar;
    @FXML private Button dashboardBtn;
    @FXML private Button reservationsBtn;
    @FXML private Button paymentsBtn;
    @FXML private Button servicesBtn;
    @FXML private Button usersBtn;
    @FXML private ImageView profileImage;
    private ContextMenu profileMenu;
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
        String token = SessionManager.getInstance().getAuthToken();
        if (token != null) {
            apiClient.setAuthToken(token);
        }

        String userName = SessionManager.getInstance().getCurrentUser().getNombre();
        welcomeLabel.setText("Bienvenido a Amanecer Tropical, " + userName);

        String role = SessionManager.getInstance().getCurrentUser().getRol();

        if ("ADMIN".equalsIgnoreCase(role)) {
            usersBtn.setVisible(true);
        }

        xAxis.setLabel("Categorías");
        yAxis.setLabel("Cantidad");
        statsChart.setTitle("Estadísticas del Sistema");

        xAxis.getCategories().addAll("Reservaciones", "Pagos", "Usuarios");

        profileMenu = new ContextMenu();
        profileMenu.getStyleClass().add("profile-menu");

        MenuItem profileItem = new MenuItem("👤 Perfil");
        profileItem.setOnAction(e -> showProfile());

        MenuItem settingsItem = new MenuItem("⚙️ Configuración");
        settingsItem.setOnAction(e -> showSettings());

        MenuItem logoutItem = new MenuItem("⬅️ Cerrar Sesión");
        logoutItem.setOnAction(e -> handleLogout());

        profileMenu.getItems().addAll(profileItem, settingsItem, new SeparatorMenuItem(), logoutItem);

        loadProfileImage();
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
        updateActiveButton(dashboardBtn);
        logger.info("Dashboard view loaded");
    }

    @FXML
    private void showReservations() {
        try {
            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReservationListView.fxml"));
            VBox reservationsView = loader.load();
            contentArea.getChildren().add(reservationsView);
            updateActiveButton(reservationsBtn);
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
            updateActiveButton(paymentsBtn);
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
            updateActiveButton(usersBtn);
            logger.info("Users view loaded");
        } catch (IOException e) {
            logger.error("Error loading users view", e);
        }
    }

    @FXML
    private void showServices() {
        // TODO: Implementar vista de servicios
        logger.info("Vista de servicios solicitada");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Servicios");
        alert.setHeaderText("Vista de Servicios");
        alert.setContentText("La funcionalidad de servicios estará disponible próximamente.");
        alert.showAndWait();
        updateActiveButton(servicesBtn);
    }

    @SuppressWarnings("unchecked")
    private void loadDashboardStats() {
        new Thread(() -> {
            try {
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

            statsChart.layout();
            statsChart.requestLayout();

            logger.info("Chart updated successfully");
        });
    }

    private void loadProfileImage() {
        new Thread(() -> {
            try {
                String fotoPerfil = SessionManager.getInstance().getCurrentUser().getFotoPerfil();
                Image image = null;

                if (fotoPerfil != null && !fotoPerfil.trim().isEmpty()) {
                    try {
                        String imageUrl = "http://localhost:8080" + fotoPerfil;
                        image = new Image(imageUrl, true);
                        logger.info("Loading user profile image from: {}", imageUrl);
                    } catch (Exception e) {
                        logger.warn("Failed to load user profile image, falling back to default: {}", e.getMessage());
                        image = loadDefaultProfileImage();
                    }
                } else {
                    image = loadDefaultProfileImage();
                }

                Image finalImage = image;
                Platform.runLater(() -> {
                    if (finalImage != null) {
                        profileImage.setImage(finalImage);
                        logger.info("Profile image loaded successfully");
                    } else {
                        logger.warn("Failed to load any profile image");
                    }
                });

            } catch (Exception e) {
                logger.error("Error loading profile image", e);
                Platform.runLater(() -> {
                    Image defaultImage = loadDefaultProfileImage();
                    if (defaultImage != null) {
                        profileImage.setImage(defaultImage);
                    }
                });
            }
        }).start();
    }

    private Image loadDefaultProfileImage() {
        try {
            return new Image(getClass().getResourceAsStream("../images/default-profile.png"));
        } catch (Exception e) {
            logger.error("Failed to load default profile image", e);
            return null;
        }
    }

    private void showProfile() {
        // TODO: Implementar vista de perfil
        logger.info("Vista de perfil solicitada");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Perfil");
        alert.setHeaderText("Vista de Perfil");
        alert.setContentText("La funcionalidad de perfil estará disponible próximamente.");
        alert.showAndWait();
    }

    private void showSettings() {
        // TODO: Implementar vista de configuración
        logger.info("Vista de configuración solicitada");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Configuración");
        alert.setHeaderText("Vista de Configuración");
        alert.setContentText("La funcionalidad de configuración estará disponible próximamente.");
        alert.showAndWait();
    }

    @FXML
    private void showProfileMenu() {
        if (profileMenu != null) {
            profileMenu.show(profileImage, javafx.geometry.Side.BOTTOM, 0, 0);
        }
    }

    private void updateActiveButton(Button activeBtn) {
        dashboardBtn.getStyleClass().remove("active");
        reservationsBtn.getStyleClass().remove("active");
        paymentsBtn.getStyleClass().remove("active");
        servicesBtn.getStyleClass().remove("active");
        usersBtn.getStyleClass().remove("active");

        activeBtn.getStyleClass().add("active");
    }
}
