package org.example;

import org.example.classes.*;
import org.example.input.InputHandler;
import org.example.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements Runnable {
    private Thread gameThread;
    private final int FPS = 60;
    private List<AbstractFighter> fighters = new ArrayList<>();

    public GamePanel() {
        setPreferredSize(new Dimension(Constants.GAME_WIDTH, Constants.GAME_HEIGHT));
        setFocusable(true);
        requestFocus();
        addKeyListener(new InputHandler());

        fighters.add(new Knight(100, 400));
        fighters.add(new Ninja(600, 400));

        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        long interval = 1000000000 / FPS;
        while (true) {
            update();
            repaint();
            try {
                Thread.sleep(interval / 1000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        for (AbstractFighter f : fighters) {
            f.update();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (AbstractFighter f : fighters) {
            f.draw(g);
        }
    }
}