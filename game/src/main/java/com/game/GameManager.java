package com.game;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.scene.control.Alert;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Steuert den kompletten Spielablauf.
 * Von Menüs bis zur Charakterauswahl - hier läuft alles zusammen.
 */
public class GameManager {
    /** Fenster und UI Management */
    private Stage primaryStage;
    private Scene mainScene;
    private Scene characterSelectScene;
    private GameArena gameArena;

    /** Spieler und Charakterwahl */
    private String selectedP1Character = null;
    private String selectedP2Character = null;
    private String player1Name;
    private String player2Name;

    /**
     * Erstellt einen neuen GameManager.
     * @param stage Das Hauptfenster der Anwendung
     */
    public GameManager(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Erstellt das Hauptmenü mit allen Buttons.
     * Hat jetzt auch einen Exit-Button - war echt nötig.
     */
    public VBox getMainMenu() {
        VBox mainMenu = new VBox(20);
        mainMenu.setAlignment(Pos.CENTER);

        Button storyMode = new Button("Story Mode");
        Button pvpMode = new Button("1v1 Mode");
        Button closeButton = new Button("Exit Game");  // Add close button

        storyMode.setOnAction(e -> showStoryMode());
        pvpMode.setOnAction(e -> showCharacterSelect());
        closeButton.setOnAction(e -> {
            // Save any pending data before closing
            PlayerData.saveData();
            // Close the application
            primaryStage.close();
        });

        // Style the buttons consistently
        storyMode.setPrefWidth(150);
        pvpMode.setPrefWidth(150);
        closeButton.setPrefWidth(150);
        
        mainMenu.getChildren().addAll(storyMode, pvpMode, closeButton);
        return mainMenu;
    }

    /**
     * Zeigt Story-Modus (noch nicht implementiert)
     */
    private void showStoryMode() {
        VBox storyBox = new VBox(20);
        storyBox.setAlignment(Pos.CENTER);
        Label comingSoon = new Label("Coming Soon!");
        Button back = new Button("Back to Main Menu");
        back.setOnAction(e -> primaryStage.getScene().setRoot(getMainMenu()));
        storyBox.getChildren().addAll(comingSoon, back);
        primaryStage.getScene().setRoot(storyBox);
    }

    /**
     * Zeigt die Charakterauswahl.
     * Mit Top 3 Spielern und allen Stats - schön übersichtlich.
     */
    public void showCharacterSelect() {
        // Load player data first
        PlayerData.loadData();

        VBox characterSelect = new VBox(20);
        characterSelect.setAlignment(Pos.CENTER);
        Label title = new Label("Select Characters");

        // Add Top 3 Players display
        VBox topPlayersBox = new VBox(5);
        topPlayersBox.setAlignment(Pos.CENTER);
        Label topPlayersTitle = new Label("Top 3 Players");
        topPlayersBox.getChildren().add(topPlayersTitle);

        List<Map.Entry<String, Integer>> topPlayers = PlayerData.getTopPlayers(3);
        for (int i = 0; i < topPlayers.size(); i++) {
            Map.Entry<String, Integer> player = topPlayers.get(i);
            Label playerLabel = new Label(String.format("%d. %s - %d wins", 
                i + 1, player.getKey(), player.getValue()));
            topPlayersBox.getChildren().add(playerLabel);
        }

        // Create main HBox to hold both player sections side by side
        HBox mainContainer = new HBox(50);
        mainContainer.setAlignment(Pos.CENTER);

        // Player 1 (Left) section
        VBox p1Section = new VBox(10);
        p1Section.setAlignment(Pos.CENTER);
        Label p1Label = new Label("Player 1: Not Selected");
        VBox p1ButtonBox = new VBox(5);  // Changed from HBox to VBox for vertical buttons
        p1ButtonBox.setAlignment(Pos.CENTER);
        Label statsLabelP1 = new Label("Player 1: No character selected");
        statsLabelP1.setStyle("-fx-font-size: 14; -fx-font-family: monospace;");

        // Player 2 (Right) section
        VBox p2Section = new VBox(10);
        p2Section.setAlignment(Pos.CENTER);
        Label p2Label = new Label("Player 2: Not Selected");
        VBox p2ButtonBox = new VBox(5);  // Changed from HBox to VBox for vertical buttons
        p2ButtonBox.setAlignment(Pos.CENTER);
        Label statsLabelP2 = new Label("Player 2: No character selected");
        statsLabelP2.setStyle("-fx-font-size: 14; -fx-font-family: monospace;");

        String[] characters = {
            "Bishop", 
            "Holyknight", 
            "Knight", 
            "Magician", 
            "Ninja", 
            "Priestess", 
            "Rogue", 
            "Swordsman", 
            "Warrior", 
            "Wizard"
        };  // Nur die Charaktere mit vorhandenen Bildern

        for (String character : characters) {
            Button p1Button = new Button(character);
            Button p2Button = new Button(character);
            
            p1Button.setPrefWidth(100);  // Set fixed width for buttons
            p2Button.setPrefWidth(100);

            p1Button.setOnAction(e -> {
                selectedP1Character = character;
                p1Label.setText("Player 1: " + character);
                Character.CharacterStats stats = Character.CHARACTER_STATS.get(character);
                String statsText = String.format("""
                    Player 1: %s
                    Damage: %d
                    Defense: %d
                    Speed: %.1f
                    Attack Speed: %d
                    Attack Range: %.0f
                    
                    %s""", 
                    character,
                    stats.damage(),
                    stats.defense(),
                    stats.speed(),
                    stats.attackSpeed(), // Changed order
                    stats.range(),      // Changed from %d to %.0f
                    stats.description()
                );
                statsLabelP1.setText(statsText);
            });
            
            p2Button.setOnAction(e -> {
                selectedP2Character = character;
                p2Label.setText("Player 2: " + character);
                Character.CharacterStats stats = Character.CHARACTER_STATS.get(character);
                String statsText = String.format("""
                    Player 2: %s
                    Damage: %d
                    Defense: %d
                    Speed: %.1f
                    Attack Speed: %d
                    Attack Range: %.0f
                    
                    %s""", 
                    character,
                    stats.damage(),
                    stats.defense(),
                    stats.speed(),
                    stats.attackSpeed(), // Changed order
                    stats.range(),      // Changed from %d to %.0f
                    stats.description()
                );
                statsLabelP2.setText(statsText);
            });

            p1ButtonBox.getChildren().add(p1Button);
            p2ButtonBox.getChildren().add(p2Button);
        }

        // Add components to player sections
        p1Section.getChildren().addAll(p1Label, p1ButtonBox, statsLabelP1);
        p2Section.getChildren().addAll(p2Label, p2ButtonBox, statsLabelP2);

        // Add player sections to main container
        mainContainer.getChildren().addAll(p1Section, p2Section);

        // Bottom buttons
        Button startButton = new Button("Start Game");
        startButton.setDisable(true);
        startButton.setOnAction(e -> {
            if (selectedP1Character != null && selectedP2Character != null) {
                getPlayerNames().ifPresent(names -> {
                    player1Name = names[0];
                    player2Name = names[1];
                    startGame(selectedP1Character, selectedP2Character);
                });
            }
        });

        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(e -> {
            selectedP1Character = null;
            selectedP2Character = null;
            primaryStage.getScene().setRoot(getMainMenu());
        });

        // Update start button state
        p1Label.textProperty().addListener((obs, old, newVal) -> 
            startButton.setDisable(selectedP1Character == null || selectedP2Character == null));
        p2Label.textProperty().addListener((obs, old, newVal) -> 
            startButton.setDisable(selectedP1Character == null || selectedP2Character == null));

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(backButton, startButton);

        // Add all components to main VBox
        characterSelect.getChildren().addAll(topPlayersBox, title, mainContainer, buttons);
        primaryStage.getScene().setRoot(characterSelect);
    }

    /**
     * Dialog für die Spielerauswahl/Erstellung.
     * Speichert auch direkt neue Spieler in der Datenbank.
     */
    private Optional<String[]> getPlayerNames() {
        // Create player selection dialogs
        VBox dialog = new VBox(10);
        dialog.setAlignment(Pos.CENTER);
        dialog.setPadding(new javafx.geometry.Insets(20));
        
        // Player 1 selection
        VBox p1Box = new VBox(5);
        Label p1Label = new Label("Select Player 1:");
        ComboBox<String> p1Select = new ComboBox<>();
        p1Select.setEditable(true);
        p1Select.getItems().addAll(PlayerData.getAllPlayerNames());
        p1Select.setPrefWidth(200);
        p1Box.getChildren().addAll(p1Label, p1Select);
        
        // Player 2 selection
        VBox p2Box = new VBox(5);
        Label p2Label = new Label("Select Player 2:");
        ComboBox<String> p2Select = new ComboBox<>();
        p2Select.setEditable(true);
        p2Select.getItems().addAll(PlayerData.getAllPlayerNames());
        p2Select.setPrefWidth(200);
        p2Box.getChildren().addAll(p2Label, p2Select);
        
        // Buttons
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        Button confirmButton = new Button("Confirm");
        Button cancelButton = new Button("Cancel");
        buttons.getChildren().addAll(confirmButton, cancelButton);
        
        dialog.getChildren().addAll(p1Box, p2Box, buttons);
        
        // Create and show dialog
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Select Players");
        dialogStage.setScene(new Scene(dialog));
        
        // Result holder
        String[] result = new String[2];
        
        confirmButton.setOnAction(e -> {
            String p1Name = p1Select.getValue();
            String p2Name = p2Select.getValue();
            
            if (p1Name != null && !p1Name.trim().isEmpty() &&
                p2Name != null && !p2Name.trim().isEmpty()) {
                p1Name = p1Name.trim();
                p2Name = p2Name.trim();
                
                // Add both players to the database immediately
                PlayerData.addPlayer(p1Name);
                PlayerData.addPlayer(p2Name);
                
                result[0] = p1Name;
                result[1] = p2Name;
                dialogStage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Names");
                alert.setContentText("Please enter names for both players!");
                alert.showAndWait();
            }
        });
        
        cancelButton.setOnAction(e -> dialogStage.close());
        
        dialogStage.showAndWait();
        
        return (result[0] != null && result[1] != null) ? 
            Optional.of(result) : Optional.empty();
    }

    /**
     * Startet das eigentliche Spiel.
     * Erstellt die Arena und übergibt die gewählten Charaktere.
     */
    private void startGame(String p1Character, String p2Character) {
        gameArena = new GameArena(p1Character, p2Character, this);
        primaryStage.setScene(new Scene(gameArena.getGamePane(), 800, 600));
        gameArena.getGamePane().requestFocus();
    }

    /** Getter für die Spielernamen */
    public String getPlayer1Name() { return player1Name; }
    public String getPlayer2Name() { return player2Name; }
}