import java.util.ArrayList;

/**
 * Player represents the user navigating the Haunted Mansion.
 * Tracks the player's current location (Room), inventory of items (ArrayList),
 * and the number of moves taken.
 */
public class Player {
    // Encapsulated private fields
    private Room currentRoom;
    private ArrayList<Item> inventory;
    private int moves;

    /**
     * Constructor for Player.
     *
     * @param startingRoom The room where the player begins the game.
     */
    public Player(Room startingRoom) {
        this.currentRoom = startingRoom;
        this.inventory = new ArrayList<>();
        this.moves = 0;
    }

    /**
     * Gets the room where the player is currently located.
     *
     * @return Current Room.
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Updates the player's current room.
     *
     * @param room The new room to move the player to.
     */
    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    /**
     * Adds an item to the player's inventory.
     *
     * @param item The item picked up by the player.
     */
    public void pickUp(Item item) {
        if (item != null) {
            inventory.add(item);
        }
    }

    /**
     * Checks if the player has an item with the specified name in their inventory (case-insensitive).
     *
     * @param name Name of the item to check for.
     * @return true if the item is present, false otherwise.
     */
    public boolean hasItem(String name) {
        if (name == null) {
            return false;
        }
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds and returns an Item from the player's inventory by name (case-insensitive).
     *
     * @param name Name of the item to find.
     * @return The Item if found in inventory, or null otherwise.
     */
    public Item getItem(String name) {
        if (name == null) {
            return null;
        }
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Returns a formatted comma-separated string listing all items in the player's inventory.
     *
     * @return Inventory item names, or "nothing" if empty.
     */
    public String getInventoryString() {
        if (inventory.isEmpty()) {
            return "nothing";
        }
        ArrayList<String> names = new ArrayList<>();
        for (Item item : inventory) {
            names.add(item.getName());
        }
        return String.join(", ", names);
    }

    /**
     * Gets the total count of moves made by the player.
     *
     * @return Number of moves.
     */
    public int getMoves() {
        return moves;
    }

    /**
     * Increments the player's move counter by 1.
     */
    public void incrementMoves() {
        this.moves++;
    }
}
