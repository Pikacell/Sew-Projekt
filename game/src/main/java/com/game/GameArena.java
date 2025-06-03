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

public class GameArena {
    private Pane gamePane;
    private Character player1;
    private Character player2;
    private Label healthLabel1;
    private Label healthLabel2;
    private Set<KeyCode> activeKeys;
    private Rectangle healthBar1;
    private Rectangle healthBar2;
    private boolean gameOver = false;
    private GameManager gameManager;
    // Add new field for projectiles
    private final List<Projectile> projectiles = new ArrayList<>();
    private Label player1StatsLabel;
    private Label player2StatsLabel;

    // Add frame limiting
    private static final long FRAME_TIME = 16_666_667; // ~60 FPS in nanoseconds
    private long lastUpdate = 0;

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

    private void update() {
        // Entferne Debug-Ausgaben für bessere Performance
        // System.out.println("Player 1 position: " + player1.getX());
        // System.out.println("Player 2 position: " + player2.getX());
        
        // Boundary checks
        if (player1.getX() < 0) player1.setX(0);
        if (player1.getX() > 750) player1.setX(750);
        if (player2.getX() < 0) player2.setX(0);
        if (player2.getX() > 750) player2.setX(750);

        // Add jumping controls
        if (activeKeys.contains(KeyCode.W)) {
            player1.jump();
        }
        if (activeKeys.contains(KeyCode.UP)) {
            player2.jump();
        }

        // Player 1 controls - WASD + QE
        if (activeKeys.contains(KeyCode.A)) {
            player1.moveLeft();
            player1.setFacingRight(false);
        }
        if (activeKeys.contains(KeyCode.D)) {
            player1.moveRight();
            player1.setFacingRight(true);
        }
        
        // Player 1 melee attack handling
        if (activeKeys.contains(KeyCode.Q) || activeKeys.contains(KeyCode.E)) {
            if (player1.getType().equals("Archer") || player1.getType().equals("Mage")) {
                fireProjectile(player1, player1.isFacingRight());
            } else {
                boolean isStrongAttack = activeKeys.contains(KeyCode.E);
                if (isStrongAttack && !player1.canUseStrongAttack()) {
                    return;
                }
                if (player1.canAttack()) {
                    player1.attack(player2, isStrongAttack);
                    if (isStrongAttack) {
                        player1.setStrongAttackCooldown();
                    }
                }
            }
        }

        // Player 2 controls
        if (activeKeys.contains(KeyCode.LEFT)) {
            player2.moveLeft();
            player2.setFacingRight(false);
        }
        if (activeKeys.contains(KeyCode.RIGHT)) {
            player2.moveRight();
            player2.setFacingRight(true);
        }
        
        // Player 2 melee attack handling
        if (activeKeys.contains(KeyCode.K) || activeKeys.contains(KeyCode.L)) {
            if (player2.getType().equals("Archer") || player2.getType().equals("Mage")) {
                fireProjectile(player2, player2.isFacingRight());
            } else {
                boolean isStrongAttack = activeKeys.contains(KeyCode.L);
                if (isStrongAttack && !player2.canUseStrongAttack()) {
                    return;
                }
                if (player2.canAttack()) {
                    player2.attack(player1, isStrongAttack);
                    if (isStrongAttack) {
                        player2.setStrongAttackCooldown();
                    }
                }
            }
        }

        // Update physics and projectiles
        player1.update();
        player2.update();
        updateProjectiles();
        updateHealthBars();
        checkGameOver();
    }

    private void updateProjectiles() {
        // Use iterator pattern for better performance
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile proj = iterator.next();
            proj.update();
            
            if (isProjectileOutOfBounds(proj) || handleProjectileCollision(proj)) {
                gamePane.getChildren().remove(proj);
                iterator.remove();
            }
        }
    }

    private boolean isProjectileOutOfBounds(Projectile proj) {
        double x = proj.getTranslateX();
        return x > 800 || x < 0;
    }

    private boolean handleProjectileCollision(Projectile proj) {
        Character target = proj.getData().equals("player1") ? player2 : player1;
        if (proj.getBoundsInParent().intersects(target.getBoundsInParent())) {
            target.takeDamage(proj.getDamage());
            return true;
        }
        return false;
    }

    private void fireProjectile(Character shooter, boolean facingRight) {
        if (!shooter.canAttack()) return;
        
        // Check if trying to use strong attack
        boolean isStrongAttack = (shooter == player1 && activeKeys.contains(KeyCode.E)) || 
                                (shooter == player2 && activeKeys.contains(KeyCode.L));
        
        // If strong attack but on cooldown, return
        if (isStrongAttack && !shooter.canUseStrongAttack()) {
            return;
        }
        
        double speed;
        double size;
        
        // Set base speed and size
        if (shooter.getType().equals("Mage")) {
            speed = 10;
            size = 8;
        } else if (shooter.getType().equals("Archer")) {
            speed = 20;
            size = 4;
        } else {
            speed = 15;
            size = 5;
        }
        
        // Set direction based on facing direction
        if (!shooter.isFacingRight()) {
            speed = -speed;
        }
        
        // Create projectile with damage multiplier for strong attack
        Projectile proj = new Projectile(
            shooter.getX() + (shooter.isFacingRight() ? shooter.getFitWidth() : 0),
            shooter.getY() + shooter.getFitHeight()/2,
            speed,
            shooter.getDamage() * (isStrongAttack ? 2 : 1),
            shooter.getOriginalColor(),
            size
        );
        
        proj.setData(shooter == player1 ? "player1" : "player2");
        projectiles.add(proj);
        gamePane.getChildren().add(proj);
        
        // Set cooldowns
        shooter.setAttackCooldown();
        if (isStrongAttack) {
            shooter.setStrongAttackCooldown();
        }
    }

    private void updateHealthBars() {
        healthBar1.setWidth(Math.max(0, player1.getHealth() * 2));
        healthBar2.setWidth(Math.max(0, player2.getHealth() * 2));
        healthLabel1.setText(player1.getHealth() + "/100");
        healthLabel2.setText(player2.getHealth() + "/100");
    }

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

    public Pane getGamePane() {
        return gamePane;
    }

    public void reset(String p1Character, String p2Character) {
        // Clear existing projectiles
        projectiles.forEach(proj -> gamePane.getChildren().remove(proj));
        projectiles.clear();
        
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