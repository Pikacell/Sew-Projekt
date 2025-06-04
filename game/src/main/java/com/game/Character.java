package com.game;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import java.util.HashMap;
import java.util.Map;

/**
 * Das Herzstück des Spiels - unsere Charaktere!
 * Hier steckt die komplette Kampflogik und Charaktersteuerung drin.
 */
public class Character extends ImageView {
    
    /** Bildercache - niemand will die dauernd neu laden */
    private static final Map<String, Image> characterImages = new HashMap<>();
    
    /** Pfad zu den Charakterbildern */
    private static final String IMAGE_PATH = "/images/%s.png";

    /** 
     * Die Charakterwerte - endlich ausbalanciert!
     * Hat ewig gedauert die vernünftig einzustellen.
     */
    public static final Map<String, CharacterStats> CHARACTER_STATS = Map.of(
        "Bishop", new CharacterStats(15, 2, 180, 12, 6, "Healer with moderate ranged attacks"),
        "Holyknight", new CharacterStats(22, 3, 45, 18, 4, "Holy warrior with high defense"),
        "Knight", new CharacterStats(20, 2, 45, 16, 5, "Well-armored fighter with balanced stats"),
        "Magician", new CharacterStats(25, 2, 200, 5, 4, "Powerful spellcaster with high damage"),
        "Ninja", new CharacterStats(14, 5, 35, 6, 9, "Fastest character with rapid attacks"),
        "Priestess", new CharacterStats(18, 2, 160, 8, 7, "Support caster with healing abilities"),
        "Rogue", new CharacterStats(16, 4, 35, 6, 8, "Quick melee fighter with high attack speed"),
        "Swordsman", new CharacterStats(19, 3, 40, 12, 6, "Balanced fighter with good reach"),
        "Warrior", new CharacterStats(21, 3, 40, 15, 5, "Strong melee fighter with good defense"),
        "Wizard", new CharacterStats(28, 2, 220, 4, 3, "Master of destructive magic")
    );

    /** 
     * Lädt die Bilder beim Start
     * Wichtig: Alle Bilder müssen im richtigen Format vorliegen
     */
    static {
        for (String type : CHARACTER_STATS.keySet()) {
            try {
                String imagePath = String.format(IMAGE_PATH, type.toLowerCase());
                Image img = new Image(
                    Character.class.getResource(imagePath).toString(),
                    450,  
                    450,  
                    true, 
                    true
                );
                characterImages.put(type, img);
            } catch (Exception e) {
                System.err.println("Error loading image for " + type + ": " + e.getMessage());
            }
        }
    }

    /** Basis-Spielwerte für jeden Charakter */
    private String type;
    private int health = 100;
    private int maxHealth = 100;
    private int damage;
    private double speed;
    private boolean canAttack = true;
    private long lastAttackTime;
    private static final int ATTACK_COOLDOWN = 500;  // Halbe Sekunde Cooldown
    private double velocityY = 0;
    private boolean isJumping = false;
    private double attackRange;
    private int defense;
    private int attackSpeed;
    private String description;
    private boolean facingRight = true;
    private boolean canUseStrongAttack = true;
    private long lastStrongAttackTime = 0;
    private static final long STRONG_ATTACK_COOLDOWN = 3000;  // 3 Sekunden für Spezialangriff
    private static final double GRAVITY = 0.5;
    private static final double JUMP_FORCE = -12;
    private static final double GROUND_Y = 450;

    /**
     * Erstellt einen neuen Charakter mit den gewählten Eigenschaften.
     * Lädt das passende Bild und initialisiert alle Werte.
     */
    public Character(String type, double x, double y) {
        super(characterImages.getOrDefault(type, characterImages.get("Warrior")));
        CharacterStats stats = CHARACTER_STATS.getOrDefault(type, CHARACTER_STATS.get("Warrior"));

        initializeCharacter(type, x, y, stats);
    }

    /**
     * Richtet den Charakter komplett ein.
     * Skaliert das Bild, setzt Position und lädt die Stats.
     */
    private void initializeCharacter(String type, double x, double y, CharacterStats stats) {
        // Get the actual image
        Image characterImage = getImage();
        
        // Calculate scale to maintain reasonable game size
        double baseHeight = 300;  // Increased from 100 to 300
        double scale = baseHeight / characterImage.getHeight();
        
        // Set image properties with calculated dimensions
        setFitHeight(characterImage.getHeight() * scale);
        setFitWidth(characterImage.getWidth() * scale);
        setPreserveRatio(true);
        setSmooth(true);
        setCache(true);
        
        // Add visible hitbox outline
        DropShadow borderEffect = new DropShadow();
        borderEffect.setColor(Color.LIME);
        borderEffect.setOffsetX(0);
        borderEffect.setOffsetY(0);
        borderEffect.setRadius(10);  // Increased outline width
        borderEffect.setSpread(0.8); // More visible spread
        setEffect(borderEffect);
        
        // Set position
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
        
        // Initialize combat state
        this.maxHealth = 100;
        this.health = maxHealth;
        this.canAttack = true;
    }

    /**
     * Der Sprung! 
     * Nichts spektakuläres, aber hey - es funktioniert!
     */
    public void jump() {
        if (!isJumping) {
            velocityY = JUMP_FORCE;
            isJumping = true;
        }
    }

    /**
     * Bewegung nach links
     * Checkt auch gleich ob wir nicht aus der Arena fallen
     */
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

    /**
     * Bewegung nach rechts
     * Checkt auch gleich ob wir nicht aus der Arena fallen
     */
    public void moveRight() {
        double newX = getX() + speed;
        // Use actual width for boundary check
        if (newX <= 800 - getFitWidth()) {
            setX(newX);
        }
        if (!facingRight) {
            setScaleX(1);
            facingRight = true;
        }
    }

    /**
     * Aktualisiert die Physik jeden Frame.
     * Kümmert sich um Schwerkraft und Sprunghöhe.
     */
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

    /**
     * Führt einen Angriff aus.
     * Prüft Reichweite und berechnet den Schaden.
     */
    public boolean attack(Character target, boolean isSpecialAttack) {
        if (!canAttack) return false;

        // Use actual bounds for distance calculation
        javafx.geometry.Bounds myBounds = getCollisionBounds();
        javafx.geometry.Bounds targetBounds = target.getCollisionBounds();
        
        double distance = Math.abs(myBounds.getCenterX() - targetBounds.getCenterX());
        
        if (distance <= attackRange) {
            int attackDamage = isSpecialAttack ? damage * 2 : damage;
            target.takeDamage(attackDamage);
            canAttack = false;
            lastAttackTime = System.currentTimeMillis();
            setEffect(new ColorAdjust(0, 1, 0, 0));
            return true;
        }
        return false;
    }

    /**
     * Verarbeitet eingehenden Schaden.
     * Zeigt visuelles Feedback durch kurze Transparenz.
     */
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

    /**
     * Setzt den Charakter zurück.
     * Wird nach Spielende oder für neue Runde aufgerufen.
     */
    public void reset() {
        health = maxHealth;
        canAttack = true;
        canUseStrongAttack = true;
        setEffect(null);
        setOpacity(1.0);
    }

    /**
     * Gibt die Kollisionsbox des Charakters zurück.
     */
    public javafx.geometry.Bounds getCollisionBounds() {
        return getBoundsInParent();
    }

    /**
     * Prüft ob dieser Charakter mit einem anderen kollidiert
     */
    public boolean collidesWith(Character other) {
        return getCollisionBounds().intersects(other.getCollisionBounds());
    }

    /** 
     * Charakterwerte-Record
     * Speichert alle wichtigen Stats eines Charaktertyps
     */
    record CharacterStats(
        int damage,
        double speed,
        double range,
        int defense,
        int attackSpeed,
        String description
    ) {}

    // Various getter methods for character properties
    public int getHealth() {
        return health;
    }

    public int getDefense() { return defense; }
    public int getAttackSpeed() { return attackSpeed; }
    public String getDescription() { return description; }
    public int getDamage() { return damage; }
    public double getSpeed() { return speed; }
    public double getAttackRange() { return attackRange; }

    public String getType() {
        return type;
    }

    public boolean canAttack() {
        return canAttack;
    }

    /**
     * Startet die Abklingzeit für normale Angriffe
     */
    public void setAttackCooldown() {
        canAttack = false;
        lastAttackTime = System.currentTimeMillis();
    }

    /**
     * Aktualisiert die Blickrichtung und passt die Position an
     */
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

    /**
     * Gibt die aktuelle Blickrichtung zurück
     */
    public boolean isFacingRight() {
        return facingRight;
    }

    /**
     * Prüft ob ein Spezialangriff verfügbar ist
     */
    public boolean canUseStrongAttack() {
        if (!canUseStrongAttack) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastStrongAttackTime >= STRONG_ATTACK_COOLDOWN) {
                canUseStrongAttack = true;
            }
        }
        return canUseStrongAttack;
    }

    /**
     * Startet die Abklingzeit für Spezialangriffe
     */
    public void setStrongAttackCooldown() {
        canUseStrongAttack = false;
        lastStrongAttackTime = System.currentTimeMillis();
    }

    // Hilfsmethoden für Dimensionen und Position
    public double getWidth() {
        return getFitWidth();
    }

    public double getHeight() {
        return getFitHeight();
    }

    public double getCenterX() {
        return getLayoutX() + getFitWidth()/2;
    }

    public double getCenterY() {
        return getLayoutY() + getFitHeight()/2;
    }
}