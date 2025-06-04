package com.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Hauptklasse des Spiels.
 * Startet die JavaFX Anwendung und übergibt an den GameManager.
 */
public class Main extends Application {
    /** Der GameManager verwaltet dann alles weitere */
    private GameManager gameManager;

    /**
     * Wird von JavaFX aufgerufen um die Anwendung zu starten.
     * Erstellt das Hauptfenster und den GameManager.
     */
    @Override
    public void start(Stage primaryStage) {
        gameManager = new GameManager(primaryStage);
        primaryStage.setTitle("2D Fighting Game");
        primaryStage.setScene(new Scene(gameManager.getMainMenu(), 800, 600));
        primaryStage.show();
    }

    /**
     * Programmstart - übergibt direkt an JavaFX.
     */
    public static void main(String[] args) {
        launch(args);
    }
}