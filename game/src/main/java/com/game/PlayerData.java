package com.game;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerData {
    private static final Map<String, Integer> playerWins = new ConcurrentHashMap<>();
    private static final Path SAVE_PATH = Paths.get(System.getProperty("user.home"), "GameData", "players.json");
    
    static {
        try {
            Files.createDirectories(SAVE_PATH.getParent());
        } catch (IOException e) {
            System.err.println("Could not create save directory: " + e.getMessage());
        }
    }

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

    public static void saveData() {
        try (BufferedWriter writer = Files.newBufferedWriter(SAVE_PATH)) {
            new Gson().toJson(playerWins, writer);
        } catch (IOException e) {
            System.err.println("Error saving player data: " + e.getMessage());
        }
    }

    public static void addPlayer(String playerName) {
        if (playerName != null && !playerName.trim().isEmpty()) {
            playerWins.putIfAbsent(playerName, 0);
            saveData();
        }
    }

    public static void addWin(String playerName) {
        if (playerName != null && !playerName.trim().isEmpty()) {
            playerWins.put(playerName, playerWins.getOrDefault(playerName, 0) + 1);
            saveData();
        }
    }

    public static int getWins(String playerName) {
        return playerWins.getOrDefault(playerName, 0);
    }

    public static java.util.List<Map.Entry<String, Integer>> getTopPlayers(int count) {
        return playerWins.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(count)
            .toList();
    }

    public static Set<String> getAllPlayerNames() {
        return new TreeSet<>(playerWins.keySet());
    }
}