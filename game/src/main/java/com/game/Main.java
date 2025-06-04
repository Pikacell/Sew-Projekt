package com.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Hauptklasse der Anwendung
 */
public class Main extends Application {
    private GameManager gameManager;

    /**
     * Startet die JavaFX Anwendung
     */
    @Override
    public void start(Stage primaryStage) {
        gameManager = new GameManager(primaryStage);
        primaryStage.setTitle("2D Fighting Game");
        primaryStage.setScene(new Scene(gameManager.getMainMenu(), 800, 600));
        primaryStage.show();
    }

    /**
     * Haupteinstiegspunkt
     */
    public static void main(String[] args) {
        launch(args);
    }
}