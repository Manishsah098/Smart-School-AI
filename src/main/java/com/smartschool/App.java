package com.smartschool;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App extends Application {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    @Override
    public void start(Stage primaryStage) {
        logger.info("Initializing SmartSchool AI Application...");

        Label label = new Label("SmartSchool AI Platform - Phase 1 Ready");
        label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        StackPane root = new StackPane(label);
        root.setStyle("-fx-background-color: #f8fafc;");

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("SmartSchool AI - Agentic School Management & Student Success Platform");
        primaryStage.setScene(scene);
        primaryStage.show();

        logger.info("SmartSchool AI Application UI launched successfully.");
    }

    public static void main(String[] args) {
        logger.info("Starting SmartSchool AI Engine...");
        launch(args);
    }
}
