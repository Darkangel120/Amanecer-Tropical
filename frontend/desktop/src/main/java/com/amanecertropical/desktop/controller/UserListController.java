package com.amanecertropical.desktop.controller;

import com.amanecertropical.desktop.api.ApiResponse;
import com.amanecertropical.desktop.model.User;
import com.amanecertropical.desktop.util.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class UserListController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(UserListController.class);

    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilter;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Long> idColumn;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> cedulaColumn;
    @FXML private TableColumn<User, String> telefonoColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> statusColumn;
    @FXML private TableColumn<User, Void> actionsColumn;
    @FXML private Button addButton;
    @FXML private Button refreshButton;
    @FXML private Label statusLabel;

    private ObservableList<User> users = FXCollections.observableArrayList();
    private FilteredList<User> filteredUsers;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupFilters();
        loadUsers();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
        cedulaColumn.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        telefonoColumn.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("rol"));

        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty("Activo"));

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Editar");
            private final Button deleteButton = new Button("Eliminar");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                editButton.getStyleClass().addAll("form-button", "edit");
                editButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleEditUser(user);
                });

                deleteButton.getStyleClass().addAll("form-button", "delete");
                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleDeleteUser(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
    }

    private void setupFilters() {
        roleFilter.setItems(FXCollections.observableArrayList("Todos", "USUARIO", "ADMIN", "EMPLEADO"));
        roleFilter.setValue("Todos");

        filteredUsers = new FilteredList<>(users);
        userTable.setItems(filteredUsers);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        roleFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String selectedRole = roleFilter.getValue();

        Predicate<User> searchPredicate = user -> {
            if (searchText.isEmpty()) return true;
            return user.getNombre().toLowerCase().contains(searchText) ||
                   user.getCorreoElectronico().toLowerCase().contains(searchText) ||
                   user.getCedula().toLowerCase().contains(searchText);
        };

        Predicate<User> rolePredicate = user -> {
            if ("Todos".equals(selectedRole)) return true;
            return selectedRole.equals(user.getRol());
        };

        filteredUsers.setPredicate(searchPredicate.and(rolePredicate));
    }

    @FXML
    private void handleAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserFormView.fxml"));
            DialogPane formView = loader.load();

            UserFormController controller = loader.getController();
            controller.setUser(null); 
            controller.setOnSaveCallback(() -> {
                loadUsers(); 
            });

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Nuevo Usuario");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(addButton.getScene().getWindow());
            dialogStage.setScene(new Scene(formView));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            logger.info("Add user dialog opened");
        } catch (IOException e) {
            logger.error("Error opening add user dialog", e);
            showAlert("Error", "Error al abrir el formulario de usuario: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditUser(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserFormView.fxml"));
            DialogPane formView = loader.load();

            UserFormController controller = loader.getController();
            controller.setUser(user); 
            controller.setOnSaveCallback(() -> {
                loadUsers(); 
            });

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Usuario");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(userTable.getScene().getWindow());
            dialogStage.setScene(new Scene(formView));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            logger.info("Edit user dialog opened for user: {}", user.getId());
        } catch (IOException e) {
            logger.error("Error opening edit user dialog", e);
            showAlert("Error", "Error al abrir el formulario de usuario: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de que desea eliminar este usuario?");
        alert.setContentText("Usuario: " + user.getNombre() + " (" + user.getCorreoElectronico() + ")");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteUser(user);
            }
        });
    }

    @FXML
    private void handleRefresh() {
        loadUsers();
    }

    @FXML
    private void handleSearch() {
        applyFilters();
    }

    @FXML
    private void handleRoleFilter() {
        applyFilters();
    }

    private void loadUsers() {
        statusLabel.setText("Cargando usuarios...");
        statusLabel.getStyleClass().removeAll("error-label", "success-label");

        new Thread(() -> {
            try {
                var apiClient = SessionManager.getInstance().getApiClient();
                var request = new okhttp3.Request.Builder()
                    .url(apiClient.getBaseUrl() + "/users")
                    .addHeader("Authorization", "Bearer " + SessionManager.getInstance().getAuthToken())
                    .build();

                try (var response = apiClient.getClient().newCall(request).execute()) {
                    final String responseBody;
                    if (response.body() != null) {
                        responseBody = response.body().string();
                    } else {
                        responseBody = null;
                    }

                    Platform.runLater(() -> {
                        if (response.isSuccessful() && responseBody != null) {
                            try {
                                User[] usersArray = apiClient.getGson().fromJson(responseBody, User[].class);
                                users.clear();
                                users.addAll(java.util.Arrays.asList(usersArray));
                                statusLabel.setText("Usuarios cargados exitosamente (" + users.size() + ")");
                                statusLabel.getStyleClass().add("success-label");
                                applyFilters();
                            } catch (Exception e) {
                                statusLabel.setText("Error al procesar respuesta: " + e.getMessage());
                                statusLabel.getStyleClass().add("error-label");
                                logger.error("Failed to parse users response", e);
                            }
                        } else {
                            String errorMsg = "Error al cargar usuarios";
                            if (responseBody != null) {
                                errorMsg += ": " + responseBody;
                            } else {
                                errorMsg += " (código: " + response.code() + ")";
                            }
                            statusLabel.setText(errorMsg);
                            statusLabel.getStyleClass().add("error-label");
                            logger.error("Failed to load users: {}", errorMsg);
                        }
                    });
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Error de conexión: " + e.getMessage());
                    statusLabel.getStyleClass().add("error-label");
                    logger.error("Connection error while loading users", e);
                });
            }
        }).start();
    }

    private void deleteUser(User user) {
        statusLabel.setText("Eliminando usuario...");
        statusLabel.getStyleClass().removeAll("error-label", "success-label");

        new Thread(() -> {
            try {
                ApiResponse<Void> response = SessionManager.getInstance()
                    .getApiClient().delete("/users/" + user.getId());

                Platform.runLater(() -> {
                    if (response.isSuccess()) {
                        users.remove(user);
                        statusLabel.setText("Usuario eliminado exitosamente");
                        statusLabel.getStyleClass().add("success-label");
                        applyFilters();
                    } else {
                        statusLabel.setText("Error al eliminar usuario: " + response.getErrorMessage());
                        statusLabel.getStyleClass().add("error-label");
                        logger.error("Failed to delete user: {}", response.getErrorMessage());
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Error de conexión: " + e.getMessage());
                    statusLabel.getStyleClass().add("error-label");
                    logger.error("Connection error while deleting user", e);
                });
            }
        }).start();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
