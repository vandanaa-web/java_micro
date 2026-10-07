# HauntedMansionEscape

> A Java text-adventure puzzle game where players navigate a haunted mansion, collect items, unlock rooms, and escape before running out of moves.
> Features a classic command-line console interface, a Java Swing GUI, and a browser-based web frontend built using standard Java HTTP server utilities.

---

## 🚀 How to Compile and Run

### Requirements
- Java Development Kit (JDK 8 or higher) installed.
- No external libraries or build tools required.

### 1. Compilation
Open a terminal in the project directory and run:
```bash
javac *.java
```

### 2. Running the Swing GUI (Default Desktop App)
```bash
java Main
```

### 3. Running the Web Version (Browser Interface)
```bash
java WebMain
```
Then open **[http://localhost:8080](http://localhost:8080)** in your web browser.

### 4. Running the Console Version
```bash
java ConsoleMain
```

### 5. Running the Automated Test Suite
```bash
java TestEngine
```

---

## 🌐 Web Version API Endpoints

The browser frontend uses `com.sun.net.httpserver.HttpServer` on port `8080` with the following endpoints:

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/` | Serves `index.html`, `style.css`, and `script.js` static assets |
| `GET` | `/api/state` | Returns the current game state as JSON |
| `POST` | `/api/command` | Processes a command string sent in request body and returns JSON state |
| `POST` | `/api/restart` | Resets the game engine to a fresh game state and returns JSON |

---

## 📖 Story & Mansion Map

### Story Intro
You awaken inside an eerie, decaying mansion. The front door is locked shut, and ominous shadows linger in the corridors. You must search the rooms for clues, light sources, and keys to escape before the mansion's dark forces trap you forever!

### Mansion Layout (ASCII Map)
```text
           +-----------------+
           |     Kitchen     |
           | (Item: Candle)  |
           +--------+--------+
                    | (east)
                    v
  +-----------------+-----------------+
  |     Library     |      Hall       | (Start)
  | (Locked Door)   |<===============>| (Item: Rusty Key)
  | (Item: Old Book)| (west)   (east) |
  +-----------------+--------+--------+
                             | (south)
                             v
                   +-----------------+
                   |    Basement     |
                   | (Dark Room)     |
                   | (Item: Silver K)|
                   +--------+--------+
                            | (south)
                            v
                   +-----------------+
                   |   Front Door    | (Exit Room)
                   | (Locked Door)   |
                   +-----------------+
```

### 🔑 Puzzle Solution Walkthrough
1. **Hall**: Take the `Rusty Key` (`take rusty key`).
2. **Library**: Move east (`go east`). The `Rusty Key` unlocks the door. Take the `Old Book` (`take old book`) and read it (`read old book`) for a clue about the silver key.
3. **Kitchen**: Return west to Hall (`go west`), then go west (`go west`) to the Kitchen. Take the `Candle` (`take candle`).
4. **Basement**: Move east to Hall (`go east`), then south (`go south`) into the Basement. With the Candle in inventory, the pitch-black cellar is illuminated. Take the `Silver Key` (`take silver key`).
5. **Escape**: Move south (`go south`) to the Front Door. The `Silver Key` unlocks the exit, winning the game!

---

## 🎮 Command List

| Command | Shortcuts | Description |
| :--- | :--- | :--- |
| `go <direction>` | `n`, `s`, `e`, `w` | Move to an adjacent room (north, south, east, west) |
| `take <item>` | - | Pick up an item from the current room |
| `read <item>` | - | Read an item carried in your inventory |
| `inventory` | `i` | View all currently carried items |
| `look` | `l` | Re-examine the current room description, items, and exits |
| `hint` | - | Receive a contextual hint based on game progress |
| `help` | - | Display the list of available commands |
| `quit` | - | Exit the game |

---

## 🎓 Viva OOP Concepts Mapping

| OOP / Language Concept | File Name | Class / Method Name | Explanation & Implementation |
| :--- | :--- | :--- | :--- |
| **Classes & Objects** | [`Item.java`](file:///c:/Users/Desai%20Srikoundinya/OneDrive/Desktop/gf_java/Item.java) | `public class Item` | Instantiates item objects with name and description properties throughout the mansion. |
| **Encapsulation** | [`Room.java`](file:///c:/Users/Desai%20Srikoundinya/OneDrive/Desktop/gf_java/Room.java) | `private String name;`, `public String getName()` | All fields are strictly `private` and accessed via public getter and setter methods. |
| **Inheritance** | [`KeyItem.java`](file:///c:/Users/Desai%20Srikoundinya/OneDrive/Desktop/gf_java/KeyItem.java) | `public class KeyItem extends Item` | Extends base `Item` class, calling `super(name, description)` and adding the `unlocks` attribute. |
| **ArrayList** | [`Player.java`](file:///c:/Users/Desai%20Srikoundinya/OneDrive/Desktop/gf_java/Player.java) | `private ArrayList<Item> inventory;` | Dynamically manages items carried by player (`pickUp()`, `hasItem()`). |
| **HashMap** | [`Room.java`](file:///c:/Users/Desai%20Srikoundinya/OneDrive/Desktop/gf_java/Room.java) | `private HashMap<String, Room> exits;` | Maps direction strings (`"west"`) to target `Room` objects for $O(1)$ navigation lookup. |
| **While Loop** | [`Game.java`](file:///c:/Users/Desai%20Srikoundinya/OneDrive/Desktop/gf_java/Game.java) | `while (!engine.isGameOver())` | Runs the continuous console user input loop until a victory, loss, or quit condition occurs. |
| **If / Else Logic** | [`GameEngine.java`](file:///c:/Users/Desai%20Srikoundinya/OneDrive/Desktop/gf_java/GameEngine.java) | `handleGo()`, `isRoomTooDark()` | Evaluates locked doors, darkness restrictions, move limits, and command routing. |
| **HTTP Web Server** | [`WebServer.java`](file:///c:/Users/Desai%20Srikoundinya/OneDrive/Desktop/gf_java/WebServer.java) | `HttpServer.create(...)` | Uses Java standard library `com.sun.net.httpserver` to host REST API and static web assets. |

---

## 📐 Text-Based Class Diagram

```text
+-------------------------------------------------------+
|                         Item                          |
+-------------------------------------------------------+
| - name: String                                        |
| - description: String                                 |
+-------------------------------------------------------+
| + getName(): String                                   |
| + getDescription(): String                            |
| + toString(): String                                  |
+-------------------------------------------------------+
                           ^
                           | (extends)
+--------------------------+----------------------------+
|                       KeyItem                         |
+-------------------------------------------------------+
| - unlocks: String                                     |
+-------------------------------------------------------+
| + getUnlocks(): String                                |
+-------------------------------------------------------+

+-------------------------------------------------------+
|                         Room                          |
+-------------------------------------------------------+
| - name: String                                        |
| - description: String                                 |
| - items: ArrayList<Item>                              |
| - exits: HashMap<String, Room>                        |
| - locked: boolean                                     |
| - requiredItem: String                                |
| - dark: boolean                                       |
| - lightItem: String                                   |
| - exitRoom: boolean                                   |
+-------------------------------------------------------+
| + addItem(item: Item): void                           |
| + removeItem(item: Item): void                        |
| + findItem(name: String): Item                        |
| + setExit(direction: String, room: Room): void        |
| + getExit(direction: String): Room                    |
| + getExitsString(): String                            |
| + lock(requiredItem: String): void                    |
| + unlock(): void                                      |
| + isLocked(): boolean                                 |
| + makeDark(lightItem: String): void                   |
| + isDark(): boolean                                   |
| + isExitRoom(): boolean                               |
+-------------------------------------------------------+

+-------------------------------------------------------+
|                        Player                         |
+-------------------------------------------------------+
| - currentRoom: Room                                   |
| - inventory: ArrayList<Item>                          |
| - moves: int                                          |
+-------------------------------------------------------+
| + getCurrentRoom(): Room                              |
| + setCurrentRoom(room: Room): void                    |
| + pickUp(item: Item): void                            |
| + hasItem(name: String): boolean                      |
| + getItem(name: String): Item                         |
| + getInventoryString(): String                        |
| + getMoves(): int                                     |
| + incrementMoves(): void                              |
+-------------------------------------------------------+

+-------------------------------------------------------+
|                     GameEngine                        |
+-------------------------------------------------------+
| - MAX_MOVES: int = 30                                 |
| - player: Player                                      |
| - gameOver: boolean                                   |
| - won: boolean                                        |
+-------------------------------------------------------+
| + getWelcomeText(): String                            |
| + processCommand(input: String): String               |
| + isGameOver(): boolean                               |
| + hasWon(): boolean                                   |
| + getPlayer(): Player                                 |
| + getMovesLeft(): int                                 |
+-------------------------------------------------------+

          +------------------+------------------+------------------+
          |                  |                  |                  |
          v                  v                  v                  v
+-------------------+ +-------------------+ +-------------------+ +-------------------+
|      GameGUI      | |       Game        | |     WebServer     | |    TestEngine     |
| (Swing Frontend)  | | (Console Wrapper) | | (HTTP REST API)   | | (Automated Tests) |
+-------------------+ +-------------------+ +-------------------+ +-------------------+
          ^                  ^                  ^
          |                  |                  |
+-------------------+ +-------------------+ +-------------------+
|       Main        | |    ConsoleMain    | |      WebMain      |
+-------------------+ +-------------------+ +-------------------+
```
