package com.game;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.HashSet;
import java.util.Set;
import javafx.scene.input.KeyCode;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Die GameArena ist das Herzstück des Spiels.
 * Hier findet der eigentliche Kampf statt, mit Bewegung, Angriffen und Kollisionen.
 */
public class GameArena {
    /** Container für alle visuellen Spielelemente */
    private Pane gamePane;
    
    /** Die beiden Kämpfer */
    private Character player1;
    private Character player2;
    
    /** UI-Elemente zur Anzeige von Gesundheit und Stats */
    private Label healthLabel1;
    private Label healthLabel2;
    private Rectangle healthBar1; 
    private Rectangle healthBar2;
    private Label player1StatsLabel;
    private Label player2StatsLabel;
    
    /** Speichert aktuell gedrückte Tasten und Spielstatus */
    private Set<KeyCode> activeKeys;
    private boolean gameOver = false;
    private GameManager gameManager;
    
    /** Timing-Einstellungen für flüssige 60 FPS */
    private static final long FRAME_TIME = 16_666_667;
    private long lastUpdate = 0;

    /**
     * Erstellt eine neue Arena mit zwei Kämpfern.
     * Richtet die komplette Spielfläche ein - von Boden bis UI.
     */
    public GameArena(String p1Type, String p2Type, GameManager gameManager) {
        this.gameManager = gameManager;
        gamePane = new Pane();
        gamePane.setPrefSize(800, 600);
        activeKeys = new HashSet<>();
        
        // Create floor
        Rectangle floor = new Rectangle(0, 500, 800, 100);
        floor.setFill(Color.GRAY);
        
        // Create players at center positions
        player1 = new Character(p1Type, 200, 300);  // X position is now center point
        player2 = new Character(p2Type, 600, 300);  // X position is now center point
        
        setupHealthBars();
        setupControls();
        startGameLoop();
        
        gamePane.getChildren().addAll(floor, player1, player2, healthBar1, healthBar2, 
                                    healthLabel1, healthLabel2);
        
        // Add player stats labels
        player1StatsLabel = new Label(String.format("%s - Wins: %d", 
            gameManager.getPlayer1Name(), 
            PlayerData.getWins(gameManager.getPlayer1Name())));
        player2StatsLabel = new Label(String.format("%s - Wins: %d", 
            gameManager.getPlayer2Name(), 
            PlayerData.getWins(gameManager.getPlayer2Name())));
            
        player1StatsLabel.setLayoutX(50);
        player1StatsLabel.setLayoutY(60);
        player2StatsLabel.setLayoutX(550);
        player2StatsLabel.setLayoutY(60);
        
        gamePane.getChildren().addAll(player1StatsLabel, player2StatsLabel);
    }

    /**
     * Erstellt die Gesundheitsanzeigen für beide Spieler.
     * Grüne Balken und Zahlen zeigen die verbleibenden Lebenspunkte.
     */
    private void setupHealthBars() {
        healthBar1 = new Rectangle(50, 20, 200, 20);
        healthBar2 = new Rectangle(550, 20, 200, 20);
        healthBar1.setFill(Color.GREEN);
        healthBar2.setFill(Color.GREEN);
        
        healthLabel1 = new Label("100/100");
        healthLabel2 = new Label("100/100");
        healthLabel1.setLayoutX(50);
        healthLabel1.setLayoutY(40);
        healthLabel2.setLayoutX(550);
        healthLabel2.setLayoutY(40);
    }

    /**
     * Konfiguriert die Tastatureingaben.
     * WASD + QE für Spieler 1, Pfeiltasten + KL für Spieler 2.
     */
    private void setupControls() {
        gamePane.setFocusTraversable(true);
        gamePane.requestFocus();
        
        gamePane.setOnKeyPressed(e -> {
            activeKeys.add(e.getCode());
            System.out.println("Key pressed: " + e.getCode()); // Debug-Ausgabe
            e.consume();
        });
        
        gamePane.setOnKeyReleased(e -> {
            activeKeys.remove(e.getCode());
            e.consume();
        });
    }

    /**
     * Startet den Haupt-Gameloop.
     * Sorgt für konstante 60 FPS und regelmäßige Updates.
     */
    private void startGameLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameOver && (now - lastUpdate) >= FRAME_TIME) {
                    update();
                    lastUpdate = now;
                }
            }
        }.start();
    }

    /**
     * Zentrale Methode für alle Angriffe.
     * Verarbeitet normale und starke Angriffe, prüft Reichweiten und Cooldowns.
     */
    private void handleAttack(Character attacker, Character target, boolean isStrongAttack) {
        // Prüfe ob Angriff möglich
        if (isStrongAttack && !attacker.canUseStrongAttack()) {
            return;
        }
        if (!attacker.canAttack()) {
            return;
        }

        // Unterscheide zwischen Nah- und Fernkampf
        boolean isRanged = attacker.getType().equals("Magician") || 
                          attacker.getType().equals("Bishop") || 
                          attacker.getType().equals("Priestess") || 
                          attacker.getType().equals("Wizard");

        if (isRanged) {
            double heightDiff = Math.abs(attacker.getY() - target.getY());
            double distance = Math.abs(attacker.getX() - target.getX());
            
            if (heightDiff < 50 && distance <= attacker.getAttackRange()) {
                int damage = attacker.getDamage() * (isStrongAttack ? 2 : 1);
                target.takeDamage(damage);
                attacker.setAttackCooldown();
                if (isStrongAttack) {
                    attacker.setStrongAttackCooldown();
                }
            }
        } else {
            if (attacker.attack(target, isStrongAttack) && isStrongAttack) {
                attacker.setStrongAttackCooldown();
            }
        }
    }

    /**
     * Hauptupdate-Methode, wird jeden Frame aufgerufen.
     * Koordiniert Bewegung, Angriffe und Spielphysik.
     */
    private void update() {
        handleMovement();
        handleAttacks();
        updatePhysics();
    }

    /**
     * Verarbeitet die Bewegungseingaben beider Spieler.
     * Prüft auf aktive Tasten und bewegt die Charaktere entsprechend.
     */
    private void handleMovement() {
        // Player 1
        if (activeKeys.contains(KeyCode.A)) {
            player1.moveLeft();
        }
        if (activeKeys.contains(KeyCode.D)) {
            player1.moveRight();
        }
        if (activeKeys.contains(KeyCode.W)) {
            player1.jump();
        }

        // Player 2 
        if (activeKeys.contains(KeyCode.LEFT)) {
            player2.moveLeft();
        }
        if (activeKeys.contains(KeyCode.RIGHT)) {
            player2.moveRight();
        }
        if (activeKeys.contains(KeyCode.UP)) {
            player2.jump();
        }
    }

    /**
     * Kümmert sich um die Angriffsaktionen beider Spieler.
     * Prüft Tasteneingaben und löst entsprechende Angriffe aus.
     */
    private void handleAttacks() {
        // Player 1 attacks
        if (activeKeys.contains(KeyCode.Q) || activeKeys.contains(KeyCode.E)) {
            handleAttack(player1, player2, activeKeys.contains(KeyCode.E));
        }

        // Player 2 attacks
        if (activeKeys.contains(KeyCode.K) || activeKeys.contains(KeyCode.L)) {
            handleAttack(player2, player1, activeKeys.contains(KeyCode.L));
        }
    }

    /**
     * Aktualisiert die Spielphysik.
     * Berechnet Sprünge, Schwerkraft und Kollisionen.
     */
    private void updatePhysics() {
        player1.update();
        player2.update();
        updateHealthBars();
        checkGameOver();
    }

    /**
     * Aktualisiert die HP-Anzeigen.
     * Passt Balken und Zahlenwerte an den aktuellen Gesundheitszustand an.
     */
    private void updateHealthBars() {
        healthBar1.setWidth(Math.max(0, player1.getHealth() * 2));
        healthBar2.setWidth(Math.max(0, player2.getHealth() * 2));
        healthLabel1.setText(player1.getHealth() + "/100");
        healthLabel2.setText(player2.getHealth() + "/100");
    }

    /**
     * Prüft ob ein Spieler gewonnen hat.
     * Zeigt Game Over Screen und aktualisiert die Siegesstatistiken.
     */
    private void checkGameOver() {
        if (!gameOver && (player1.getHealth() <= 0 || player2.getHealth() <= 0)) {
            gameOver = true;
            boolean isPlayer1Winner = player2.getHealth() <= 0;
            String winnerName = isPlayer1Winner ? 
                gameManager.getPlayer1Name() : 
                gameManager.getPlayer2Name();
            
            // Update wins in PlayerData
            PlayerData.addWin(winnerName);
            
            String winner = player1.getHealth() <= 0 ? "Player 2" : "Player 1";
            Label gameOverLabel = new Label(winner + " wins!");
            gameOverLabel.setStyle("-fx-font-size: 24;");
            gameOverLabel.setLayoutX(350);
            gameOverLabel.setLayoutY(250);
            
            HBox buttons = new HBox(20);
            buttons.setLayoutX(300);
            buttons.setLayoutY(300);
            
            Button restartButton = new Button("Play Again");
            restartButton.setOnAction(e -> restartGame());
            
            Button characterSelectButton = new Button("Character Select");
            characterSelectButton.setOnAction(e -> gameManager.showCharacterSelect());  // Changed from switchToCharacterSelect to showCharacterSelect
            
            buttons.getChildren().addAll(restartButton, characterSelectButton);
            
            gamePane.getChildren().addAll(gameOverLabel, buttons);
        }
    }

    /**
     * Setzt die Arena für eine neue Runde zurück.
     * Heilt die Spieler und entfernt Game Over Anzeigen.
     */
    private void restartGame() {
        player1.reset();
        player2.reset();
        gameOver = false;
        
        // Remove all game over elements
        gamePane.getChildren().removeIf(node -> 
            (node instanceof Label && ((Label) node).getText().contains("wins")) ||
            (node instanceof HBox)  // This will remove the HBox containing both buttons
        );
        
        // Update player stats labels with new win counts
        player1StatsLabel.setText(String.format("%s - Wins: %d", 
            gameManager.getPlayer1Name(), 
            PlayerData.getWins(gameManager.getPlayer1Name())));
        player2StatsLabel.setText(String.format("%s - Wins: %d", 
            gameManager.getPlayer2Name(), 
            PlayerData.getWins(gameManager.getPlayer2Name())));
        
        updateHealthBars();
        gamePane.requestFocus();
    }

    /**
     * Gibt den Haupt-Spielcontainer zurück.
     * Wird vom GameManager verwendet um die Szene zu wechseln.
     */
    public Pane getGamePane() {
        return gamePane;
    }

    /**
     * Setzt die Arena mit neuen Charakteren zurück.
     * Wird nach der Charakterauswahl aufgerufen.
     */
    public void reset(String p1Character, String p2Character) {
        // Remove old players
        gamePane.getChildren().removeAll(player1, player2);
        
        // Create new players
        player1 = new Character(p1Character, 100, 450);
        player2 = new Character(p2Character, 600, 450);
        
        // Add new players
        gamePane.getChildren().addAll(player1, player2);
        
        gameOver = false;
        updateHealthBars();
        gamePane.requestFocus();
    }
}