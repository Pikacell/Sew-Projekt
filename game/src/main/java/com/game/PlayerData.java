package com.game;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Verwaltet die Spielerdatenbank.
 * Speichert Siege und lädt sie beim Spielstart.
 */
public final class PlayerData {
    /** Thread-sichere Map für die Spielersiege */
    private static final Map<String, Integer> playerWins = new ConcurrentHashMap<>();
    
    /** Pfad zur JSON Speicherdatei im Nutzerverzeichnis */
    private static final Path SAVE_PATH = Paths.get(System.getProperty("user.home"), "GameData", "players.json");

    /** Erstellt den Speicherordner falls nötig */
    static {
        try {
            Files.createDirectories(SAVE_PATH.getParent());
        } catch (IOException e) {
            System.err.println("Fehler beim Erstellen des Speicherordners: " + e.getMessage());
        }
    }

    /**
     * Lädt die Spielerdaten aus der JSON-Datei.
     * Wird beim Start und nach der Charakterauswahl aufgerufen.
     */
    public static void loadData() {
        if (Files.exists(SAVE_PATH)) {
            try (BufferedReader reader = Files.newBufferedReader(SAVE_PATH)) {
                Map<String, Integer> loaded = new Gson().fromJson(reader, 
                    new TypeToken<Map<String, Integer>>(){}.getType());
                if (loaded != null) {
                    playerWins.putAll(loaded);
                }
            } catch (IOException e) {
                System.err.println("Error loading player data: " + e.getMessage());
            }
        }
    }

    /**
     * Speichert den aktuellen Spielstand.
     * Wird nach jedem Sieg und beim Beenden aufgerufen.
     */
    public static void saveData() {
        try (BufferedWriter writer = Files.newBufferedWriter(SAVE_PATH)) {
            new Gson().toJson(playerWins, writer);
        } catch (IOException e) {
            System.err.println("Error saving player data: " + e.getMessage());
        }
    }

    /**
     * Fügt einen neuen Spieler hinzu oder ignoriert existierende.
     * @param playerName Name des neuen Spielers
     */
    public static void addPlayer(String playerName) {
        if (playerName != null && !playerName.trim().isEmpty()) {
            playerWins.putIfAbsent(playerName, 0);
            saveData();
        }
    }

    /**
     * Erhöht den Siegeszähler eines Spielers.
     * @param playerName Name des Siegers
     */
    public static void addWin(String playerName) {
        if (playerName != null && !playerName.trim().isEmpty()) {
            playerWins.put(playerName, playerWins.getOrDefault(playerName, 0) + 1);
            saveData();
        }
    }

    /**
     * Gibt die Anzahl der Siege eines Spielers zurück.
     * @param playerName Name des Spielers
     * @return Anzahl der Siege, 0 falls Spieler unbekannt
     */
    public static int getWins(String playerName) {
        return playerWins.getOrDefault(playerName, 0);
    }

    /**
     * Erstellt eine sortierte Liste der besten Spieler.
     * @param count Anzahl der gewünschten Top-Spieler
     * @return Liste der Spieler, sortiert nach Siegen
     */
    public static java.util.List<Map.Entry<String, Integer>> getTopPlayers(int count) {
        return playerWins.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(count)
            .toList();
    }

    /**
     * Gibt eine sortierte Liste aller Spielernamen zurück.
     * @return Set mit allen registrierten Spielern
     */
    public static Set<String> getAllPlayerNames() {
        return new TreeSet<>(playerWins.keySet());
    }
}