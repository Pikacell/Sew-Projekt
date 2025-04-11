package org.example.classes;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class AbstractFighter {
    protected int x, y;
    protected BufferedImage sprite;

    public AbstractFighter(int x, int y, String spritePath) {
        this.x = x;
        this.y = y;
        try {
            sprite = ImageIO.read(getClass().getResource(spritePath));
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Fehler beim Laden: " + spritePath);
        }
    }

    public void update() {
        // Animationen, Physik etc.
    }

    public void draw(Graphics g) {
        if (sprite != null) {
            g.drawImage(sprite, x, y, null);
        }
    }
}