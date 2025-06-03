package com.game;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class Character extends ImageView {
    // Cache für Bilder
    private static final Map<String, Image> characterImages = new HashMap<>();
    private static final String IMAGE_PATH = "/images/%s.png";

    // Konstanten für Charakter-Stats
    public static final Map<String, CharacterStats> CHARACTER_STATS = Map.of(
        "Bishop", new CharacterStats(15, 2, 180, 12, 6, "Healer with moderate ranged attacks", Color.WHITE),
        "Holyknight", new CharacterStats(22, 3, 45, 18, 4, "Holy warrior with high defense", Color.GOLD),
        "Knight", new CharacterStats(20, 2, 45, 16, 5, "Well-armored fighter with balanced stats", Color.SILVER),
        "Magician", new CharacterStats(25, 2, 200, 5, 4, "Powerful spellcaster with high damage", Color.PURPLE),
        "Ninja", new CharacterStats(14, 5, 35, 6, 9, "Fastest character with rapid attacks", Color.BLACK),
        "Priestess", new CharacterStats(18, 2, 160, 8, 7, "Support caster with healing abilities", Color.PINK),
        "Rogue", new CharacterStats(16, 4, 35, 6, 8, "Quick melee fighter with high attack speed", Color.DARKGREEN),
        "Swordsman", new CharacterStats(19, 3, 40, 12, 6, "Balanced fighter with good reach", Color.BLUE),
        "Warrior", new CharacterStats(21, 3, 40, 15, 5, "Strong melee fighter with good defense", Color.RED),
        "Wizard", new CharacterStats(28, 2, 220, 4, 3, "Master of destructive magic", Color.ORANGE)
    );

    // Lade Bilder einmalig beim Klassenstart
    static {
        for (String type : CHARACTER_STATS.keySet()) {
            try {
                String imagePath = String.format(IMAGE_PATH, type.toLowerCase());
                // Create Image from resource path string, not from InputStream
                Image img = new Image(
                    Character.class.getResource(imagePath).toString(),
                    150, // Fixed width
                    150, // Fixed height
                    true, // Preserve ratio
                    true  // Smooth
                );
                characterImages.put(type, img);
            } catch (Exception e) {
                System.err.println("Error loading image for " + type + ": " + e.getMessage());
            }
        }
    }

    // Fields
    private String type;
    private int health = 100;
    private int maxHealth = 100;
    private int damage;
    private double speed;
    private boolean canAttack = true;
    private long lastAttackTime;
    private static final int ATTACK_COOLDOWN = 500;
    private Color originalColor;
    private double velocityY = 0;
    private boolean isJumping = false;
    private double attackRange;
    private int defense;
    private int attackSpeed;
    private String description;
    private boolean facingRight = true;
    private boolean canUseStrongAttack = true;
    private long lastStrongAttackTime = 0;
    private static final long STRONG_ATTACK_COOLDOWN = 3000; // 3 seconds in milliseconds
    private static final double GRAVITY = 0.5;
    private static final double JUMP_FORCE = -12;
    private static final double GROUND_Y = 450; // Angepasst für kleinere Charaktere

    public Character(String type, double x, double y) {
        super(characterImages.getOrDefault(type, characterImages.get("Warrior")));
        CharacterStats stats = CHARACTER_STATS.getOrDefault(type, CHARACTER_STATS.get("Warrior"));

        initializeCharacter(type, x, y, stats);
    }

    private void initializeCharacter(String type, double x, double y, CharacterStats stats) {
        // Set image properties
        setFitWidth(200);  
        setFitHeight(200);
        setPreserveRatio(true);
        setSmooth(true);
        setCache(true);
        
        // Set position directly
        setX(x);
        setY(GROUND_Y - getFitHeight());
        
        // Initialize stats
        this.type = type;
        this.damage = stats.damage;
        this.speed = stats.speed;
        this.attackRange = stats.range;
        this.defense = stats.defense;
        this.attackSpeed = stats.attackSpeed;
        this.description = stats.description;
        this.originalColor = stats.color;
        
        // Initialize combat state
        this.maxHealth = 100;
        this.health = maxHealth;
        this.canAttack = true;
    }

    public void jump() {
        if (!isJumping) {
            velocityY = JUMP_FORCE;
            isJumping = true;
        }
    }

    public void moveLeft() {
        double newX = getX() - speed;
        if (newX >= 0) {
            setX(newX);
        }
        if (facingRight) {
            setScaleX(-1);
            facingRight = false;
        }
    }

    public void moveRight() {
        double newX = getX() + speed;
        if (newX <= 800 - getFitWidth()) {
            setX(newX);
        }
        if (!facingRight) {
            setScaleX(1);
            facingRight = true;
        }
    }

    public void update() {
        // Gravity and jumping
        if (isJumping) {
            setY(getY() + velocityY);
            velocityY += GRAVITY;

            // Check for ground collision
            if (getY() >= GROUND_Y - getFitHeight()) {
                setY(GROUND_Y - getFitHeight());  // Place on floor
                velocityY = 0;
                isJumping = false;
            }
        }

        // Attack cooldown
        if (!canAttack && System.currentTimeMillis() - lastAttackTime > ATTACK_COOLDOWN) {
            canAttack = true;
            setEffect(null); // Reset visual effect
        }
    }

    public boolean attack(Character target, boolean isSpecialAttack) {
        if (!canAttack) return false;

        double distance = Math.abs(this.getX() - target.getX());
        if (distance <= attackRange) {
            int attackDamage = isSpecialAttack ? damage * 2 : damage;
            target.takeDamage(attackDamage);
            canAttack = false;
            lastAttackTime = System.currentTimeMillis();
            setEffect(new ColorAdjust(0, 1, 0, 0)); // Yellow tint
            return true;
        }
        return false;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
        
        // Use opacity property binding instead of Thread
        setOpacity(0.5);
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
            javafx.util.Duration.millis(100)
        );
        pause.setOnFinished(e -> {
            if (canAttack) setOpacity(1.0);
        });
        pause.play();
    }

    public void reset() {
        health = maxHealth;
        canAttack = true;
        canUseStrongAttack = true;
        setEffect(null);
        setOpacity(1.0);
    }

    public int getHealth() {
        return health;
    }

    // New getters
    public int getDefense() { return defense; }
    public int getAttackSpeed() { return attackSpeed; }
    public String getDescription() { return description; }
    public int getDamage() { return damage; }
    public double getSpeed() { return speed; }
    public double getAttackRange() { return attackRange; }

    public String getType() {
        return type;
    }

    public Color getOriginalColor() {
        return originalColor;
    }

    public boolean canAttack() {
        return canAttack;
    }

    public void setAttackCooldown() {
        canAttack = false;
        lastAttackTime = System.currentTimeMillis();
    }

    public void setFacingRight(boolean facing) {
        if (facingRight != facing) {
            facingRight = facing;
            setScaleX(facing ? 1 : -1);
            
            // Update position to maintain visual position when flipping
            if (!facing) {
                setTranslateX(getFitWidth());
            } else {
                setTranslateX(0);
            }
        }
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public boolean canUseStrongAttack() {
        if (!canUseStrongAttack) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastStrongAttackTime >= STRONG_ATTACK_COOLDOWN) {
                canUseStrongAttack = true;
            }
        }
        return canUseStrongAttack;
    }

    public void setStrongAttackCooldown() {
        canUseStrongAttack = false;
        lastStrongAttackTime = System.currentTimeMillis();
    }

    // Add these methods to get dimensions
    public double getWidth() {
        return getFitWidth();
    }

    public double getHeight() {
        return getFitHeight();
    }

    // Return centered position
    public double getCenterX() {
        return getLayoutX() + getFitWidth()/2;
    }

    public double getCenterY() {
        return getLayoutY() + getFitHeight()/2;
    }

    // Neue Hilfsklasse für Charakter-Stats
    record CharacterStats(
        int damage,
        double speed,
        double range,
        int defense,
        int attackSpeed,
        String description,
        Color color
    ) {}
}