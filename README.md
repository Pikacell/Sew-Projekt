# 🥋 2D Kampfspiel

Das ist unser in JavaFX entwickeltes 2D-Kampfspiel, in dem du im lokalen Multiplayer gegen deine Freunde antreten kannst. Dieses Projekt hat viel Leid bereitet aber auch Freude (der Abgabe Termin) und jetzt steht dieses Teilweise Fragwürdiges Spiel als ein ganzes hier. Es gab auch mal das Projektile geschossen werden, aber beim wechsel von Boxen zu actual Characteren is das irgendwie kaputt gegangen und da das trotz vieler Rettungsversuche nd mehr geklappt hat muss der Fernkampf Character jetzt einfach auf der Selben höhe wie der Gegner sein.

---

## 🚀 Features

- 🎮 **2-Spieler Kampfsystem** im lokalen Multiplayer  
- 👥 **10 einzigartige Charaktere**:
  - **Bishop** – Heiler mit starken Fernangriffen  
  - **Holyknight** – Tank mit heiliger Stärke  
  - **Knight** – Ausbalancierter Kämpfer  
  - **Magician** – Mächtiger Fernkämpfer  
  - **Ninja** – Extrem schneller Nahkämpfer  
  - **Priestess** – Support-Charakter mit Heilkräften  
  - **Rogue** – Flinker Nahkämpfer  
  - **Swordsman** – Ausgewogener Schwertkämpfer  
  - **Warrior** – Kraftvoller Nahkämpfer  
  - **Wizard** – Magier mit hoher Reichweite  
- 💫 **Spezialangriffe** für jeden Charakter  
- 🏆 **Speicherung von Spielerstatistiken und Siegen**  
- 🎯 **Unterschiedliche Angriffsreichweiten und Schadenswerte**  
- 🎨 **Visuelle Effekte** bei Angriffen und erlittenem Schaden  
- 📊 **Bestenliste der Spieler**

---

## 🎮 Steuerung

**Spieler 1**  
- `A/D` – Links/Rechts bewegen  
- `W` – Springen  
- `Q` – Normaler Angriff  
- `E` – Spezialangriff  

**Spieler 2**  
- `←/→` – Bewegen  
- `↑` – Springen  
- `K` – Normaler Angriff  
- `L` – Spezialangriff  

---

## ⚙️ Technische Details

Entwickelt mit:

- Java 17  
- JavaFX  
- Maven  
- GSON (für Speicherdaten)

---

## 🛠️ Installation

1. Stelle sicher, dass **Java 17** und **Maven** installiert sind  
2. Repository klonen:  
   ```bash
   git clone <repository-url>
   cd <projektverzeichnis>

3. Projekt bauen:

   ```bash
   mvn clean install
   ```
4. Spiel starten:

   ```bash
   mvn javafx:run
   ```

---

## 💾 Speicherdaten

Das Spiel speichert Spielerstatistiken automatisch unter:
`%USER_HOME%/GameData/players.json`

---

## 🐞 Bekannte Bugs

- Game freezed aus unbekannten Gründen beim ersten mal Caracter auswählen für 2-3 Sekunden
- Carachtere teleporten sich ein bisschen nach links und Rechts beim drehen (keine Ahnung wie das zustande gekommen ist, aber bis zum Präsentationstermin is das hoffentlich gefixxed)

---

## 🔮 Zukunftspläne

* Weitere Charaktere
* Story-Modus
* Sound und Musik (wie das geht? Absolut keine Ahnung)
* Wieder Projektile einbauen

---

Meldungen von Bugs unerwünscht (werden sowieso nd gefixed).
**Viel Spaß beim Spielen!** 🎮


