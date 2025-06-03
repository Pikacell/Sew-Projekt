package com.game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Projectile extends ImageView {  // Change from Circle to ImageView
    private double speedX;
    private int damage;
    private String shooter;
    private static final Image PROJECTILE_IMAGE = new Image("/images/projectile.png");

    public Projectile(double x, double y, double speedX, int damage, Color color, double size) {
        super(PROJECTILE_IMAGE);
        setX(x);
        setY(y);
        this.speedX = speedX;
        this.damage = damage;
        setFitWidth(size);
        setFitHeight(size);
        setPreserveRatio(true);
        
        // Rotate image based on direction
        setRotate(speedX > 0 ? 0 : 180);
    }

    public void update() {
        setTranslateX(getTranslateX() + speedX);
    }

    public int getDamage() {
        return damage;
    }

    public void setData(String shooter) {
        this.shooter = shooter;
    }

    public String getData() {
        return shooter;
    }

    public double getSpeedX() {
        return speedX;
    }
}