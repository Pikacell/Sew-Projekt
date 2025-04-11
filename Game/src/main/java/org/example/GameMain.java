package org.example;

import javax.swing.*;

public class GameMain {
    public static void main(String[] args) {
        JFrame frame = new JFrame("2D Fighter Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new GamePanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
