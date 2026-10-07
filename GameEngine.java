/**
 * GameEngine encapsulates all core game rules, map state, and logic processing
 * for HauntedMansionEscape without relying on Scanner or System.out console printing.
 * 
 * Provides clean API methods for both Console and GUI frontends.
 */
public class GameEngine {
    // Maximum moves constant
    private static final int MAX_MOVES = 30;

    // Private encapsulated fields
    private Player player;
    private boolean gameOver;
    private boolean won;

    /**
     * Constructor for GameEngine. Builds mansion map, initializes player in starting room.
     */
    public GameEngine() {
        Room startingRoom = MansionBuilder.buildMansion();
        this.player = new Player(startingRoom);
        this.gameOver = false;
        this.won = false;
    }

    /**
     * Gets the initial welcome banner and starting room description text.
     *
     * @return Formatted welcome string.
     */
    public String getWelcomeText() {
        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("         WELCOME TO HAUNTED MANSION ESCAPE        \n");
        sb.append("==================================================\n");
        sb.append("You awaken inside an eerie, dark mansion.\n");
        sb.append("Your goal is to find a way out to safety.\n");
        sb.append("Type 'help' at any time to see available commands.\n");
        sb.append("--------------------------------------------------\n\n");
        sb.append(displayRoomText());
        return sb.toString();
    }

    /**
     * Processes a single raw command string from the user, updates game state,
     * and returns the output text to be displayed by the frontend.
     *
     * @param input Raw command string.
     * @return Output response string.
     */
    public String processCommand(String input) {
        if (gameOver) {
            return "The game is already over.";
        }

        String trimmed = input.trim().toLowerCase();
        if (trimmed.isEmpty()) {
            return "";
        }

        // Split into command word and argument
        String[] parts = trimmed.split("\\s+", 2);
        String command = parts[0];
        String argument = parts.length > 1 ? parts[1].trim() : "";

        // Handle direction shortcuts (n, s, e, w)
        if (command.equals("n")) {
            command = "go";
            argument = "north";
        } else if (command.equals("s")) {
            command = "go";
            argument = "south";
        } else if (command.equals("e")) {
            command = "go";
            argument = "east";
        } else if (command.equals("w")) {
            command = "go";
            argument = "west";
        }

        // Command routing
        if (command.equals("go")) {
            return handleGo(argument);
        } else if (command.equals("take")) {
            return handleTake(argument);
        } else if (command.equals("read")) {
            return handleRead(argument);
        } else if (command.equals("inventory") || command.equals("i")) {
            return "You are carrying: " + player.getInventoryString();
        } else if (command.equals("look") || command.equals("l")) {
            return displayRoomText();
        } else if (command.equals("hint")) {
            return handleHint();
        } else if (command.equals("help")) {
            return handleHelp();
        } else if (command.equals("quit")) {
            gameOver = true;
            return "Thank you for playing Haunted Mansion Escape. Goodbye!";
        } else {
            return "Unknown command. Type 'help' to see the available commands.";
        }
    }

    /**
     * Handles movement in a specified direction, evaluating locked room conditions,
     * victory conditions, and move limits.
     *
     * @param direction Target exit direction.
     * @return Response message string.
     */
    private String handleGo(String direction) {
        if (direction.isEmpty()) {
            return "Go where? Specify a direction (e.g., 'go west').";
        }

        Room currentRoom = player.getCurrentRoom();
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            return "You can't go that way.";
        }

        StringBuilder unlockMsg = new StringBuilder();

        // 1. LOCKED ROOM CHECK
        if (nextRoom.isLocked()) {
            String requiredItem = nextRoom.getRequiredItem();
            if (player.hasItem(requiredItem)) {
                nextRoom.unlock();
                unlockMsg.append("You use the ").append(requiredItem).append(". The door unlocks with a loud click!\n");
            } else {
                return "The door is locked. You need the " + requiredItem + ".";
            }
        }

        // Perform movement & increment moves
        player.setCurrentRoom(nextRoom);
        player.incrementMoves();

        // 2. WIN CHECK FIRST (winning on the 30th move counts as a win)
        if (nextRoom.isExitRoom()) {
            won = true;
            gameOver = true;
            StringBuilder winSb = new StringBuilder();
            if (unlockMsg.length() > 0) {
                winSb.append(unlockMsg.toString()).append("\n");
            }
            winSb.append("--------------------------------------------------\n");
            winSb.append("Location: ").append(nextRoom.getName()).append("\n");
            winSb.append(nextRoom.getDescription()).append("\n");
            winSb.append("--------------------------------------------------\n\n");
            winSb.append("🎉 CONGRATULATIONS! You escaped the Haunted Mansion!\n");
            winSb.append("Total moves taken: ").append(player.getMoves()).append("/").append(MAX_MOVES).append("\n");
            winSb.append("==================================================");
            return winSb.toString();
        }

        // 3. MOVE LIMIT CHECK SECOND
        if (player.getMoves() >= MAX_MOVES) {
            gameOver = true;
            StringBuilder loseSb = new StringBuilder();
            if (unlockMsg.length() > 0) {
                loseSb.append(unlockMsg.toString()).append("\n");
            }
            loseSb.append(displayRoomText()).append("\n\n");
            loseSb.append("--------------------------------------------------\n");
            loseSb.append("You ran out of time... The mansion's dark forces trap you forever!\n");
            loseSb.append("Total moves used: ").append(player.getMoves()).append("/").append(MAX_MOVES).append("\n");
            loseSb.append("==================================================");
            return loseSb.toString();
        }

        // Normal room entry output
        if (unlockMsg.length() > 0) {
            return unlockMsg.toString() + "\n" + displayRoomText();
        } else {
            return displayRoomText();
        }
    }

    /**
     * Handles picking up an item from the current room.
     *
     * @param itemName Target item name.
     * @return Result response string.
     */
    private String handleTake(String itemName) {
        if (itemName.isEmpty()) {
            return "Take what? Specify an item name (e.g., 'take candle').";
        }

        Room currentRoom = player.getCurrentRoom();

        // Dark room restriction check using room.getLightItem() dynamically
        if (isRoomTooDark(currentRoom)) {
            return "It's too dark to find anything.";
        }

        Item item = currentRoom.findItem(itemName);
        if (item != null) {
            currentRoom.removeItem(item);
            player.pickUp(item);
            player.incrementMoves();

            StringBuilder takeSb = new StringBuilder();
            takeSb.append("You picked up: ").append(item.getName());

            // Check move limit after successful take
            if (player.getMoves() >= MAX_MOVES) {
                gameOver = true;
                takeSb.append("\n\n--------------------------------------------------\n");
                takeSb.append("You ran out of time... The mansion's dark forces trap you forever!\n");
                takeSb.append("Total moves used: ").append(player.getMoves()).append("/").append(MAX_MOVES).append("\n");
                takeSb.append("==================================================");
            }

            return takeSb.toString();
        } else {
            return "There is no such item here.";
        }
    }

    /**
     * Handles reading an item carried in inventory.
     *
     * @param itemName Item name.
     * @return Description clue or error string.
     */
    private String handleRead(String itemName) {
        if (itemName.isEmpty()) {
            return "Read what? Specify an item name (e.g., 'read old book').";
        }

        if (!player.hasItem(itemName)) {
            return "You are not carrying that.";
        }

        Item item = player.getItem(itemName);
        if (item != null) {
            return item.getName() + ": " + item.getDescription();
        }
        return "You are not carrying that.";
    }

    /**
     * Evaluates game state and provides contextual hint string.
     *
     * @return Formatted hint message.
     */
    private String handleHint() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- HINT ---\n");
        if (!player.hasItem("Rusty Key")) {
            sb.append("Hint: Search the Hall carefully. There might be a key to unlock other rooms.");
        } else if (!player.hasItem("Candle")) {
            sb.append("Hint: The Kitchen might contain a light source to help you see in dark places.");
        } else if (!player.hasItem("Silver Key")) {
            sb.append("Hint: Explore the dark Basement now that you have a light source.");
        } else {
            sb.append("Hint: You have the Silver Key! Head to the Front Door to escape!");
        }
        sb.append("\n------------");
        return sb.toString();
    }

    /**
     * Generates formatted command help string.
     *
     * @return Help table.
     */
    private String handleHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- AVAILABLE COMMANDS ---\n");
        sb.append("  go <direction> (or n, s, e, w) : Move to an adjacent room\n");
        sb.append("  take <item>                  : Pick up an item from the current room\n");
        sb.append("  read <item>                  : Read/examine an item in your inventory\n");
        sb.append("  inventory (or i)             : View your carried items\n");
        sb.append("  look (or l)                  : Re-examine the current room\n");
        sb.append("  hint                         : Get a helpful hint\n");
        sb.append("  help                         : Show this help list\n");
        sb.append("  quit                         : Exit the game\n");
        sb.append("--------------------------");
        return sb.toString();
    }

    /**
     * Checks if a room is dark and the player lacks the required light item.
     * Dynamic check using room.getLightItem() instead of hardcoded strings.
     *
     * @param room Target room.
     * @return true if room is dark and player lacks light item, false otherwise.
     */
    public boolean isRoomTooDark(Room room) {
        if (!room.isDark()) {
            return false;
        }
        String lightItem = room.getLightItem();
        return lightItem != null && !player.hasItem(lightItem);
    }

    /**
     * Formats current room information into display text.
     *
     * @return Formatted room string.
     */
    public String displayRoomText() {
        Room current = player.getCurrentRoom();
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------------------\n");
        sb.append("Location: ").append(current.getName()).append("\n");

        if (isRoomTooDark(current)) {
            sb.append("It is pitch black. You can't see anything. Maybe a light source would help.\n");
            sb.append("Exits: ").append(current.getExitsString()).append("\n");
        } else {
            sb.append(current.getDescription()).append("\n");
            sb.append("Items here: ").append(current.getItemsString()).append("\n");
            sb.append("Exits: ").append(current.getExitsString()).append("\n");
        }

        sb.append("Moves left: ").append(getMovesLeft()).append("\n");
        sb.append("--------------------------------------------------");
        return sb.toString();
    }

    /**
     * Returns whether the game is over (win, lose, or quit).
     *
     * @return true if game is over, false otherwise.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Returns whether the player has won the game.
     *
     * @return true if won, false otherwise.
     */
    public boolean hasWon() {
        return won;
    }

    /**
     * Gets the player object.
     *
     * @return Player instance.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Calculates and returns the remaining moves.
     *
     * @return Moves remaining before loss condition.
     */
    public int getMovesLeft() {
        return Math.max(0, MAX_MOVES - player.getMoves());
    }
}
