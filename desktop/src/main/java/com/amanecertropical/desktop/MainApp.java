package com.amanecertropical.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MainApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Amanecer Tropical - Sistema de Agencia de Viajes");
        primaryStage.getIcons().add(new Image("/images/favicon.png"));

        try {
            showLoginView();
        } catch (Exception e) {
            logger.error("Error starting application", e);
            System.exit(1);
        }
    }

    public static void showLoginView() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/LoginView.fxml"));
        VBox loginView = loader.load();

        Scene scene = new Scene(loginView, 400, 550);
        scene.getStylesheets().add(MainApp.class.getResource("/css/desktop-styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void showDashboard() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/DashboardView.fxml"));
        BorderPane dashboardView = loader.load();

        Scene scene = new Scene(dashboardView, 1200, 800);
        scene.getStylesheets().add(MainApp.class.getResource("/css/desktop-styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
