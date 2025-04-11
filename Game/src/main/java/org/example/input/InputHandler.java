package org.example.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputHandler extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("Taste gedrückt: " + e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        System.out.println("Taste losgelassen: " + e.getKeyCode());
    }
}