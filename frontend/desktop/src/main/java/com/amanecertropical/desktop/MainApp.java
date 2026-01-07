package com.amanecertropical.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLoginScreen();
        primaryStage.setTitle("Amanecer Tropical - Gestión de Agencia");
        primaryStage.show();
    }

    public static void showLoginScreen() throws Exception {
        Parent root = FXMLLoader.load(MainApp.class.getResource("/fxml/LoginView.fxml"));
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
    }

    public static void showDashboard() throws Exception {
        Parent root = FXMLLoader.load(MainApp.class.getResource("/fxml/DashboardView.fxml"));
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
