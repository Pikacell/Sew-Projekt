package com.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private GameManager gameManager;

    @Override
    public void start(Stage primaryStage) {
        gameManager = new GameManager(primaryStage);
        primaryStage.setTitle("2D Fighting Game");
        primaryStage.setScene(new Scene(gameManager.getMainMenu(), 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}