import java.util.ArrayList;
import java.util.HashMap;

/**
 * Room represents a single location in the Haunted Mansion.
 * It manages items present in the room, exits leading to other rooms,
 * and attributes such as whether the room is locked, dark, or the final exit.
 * Demonstrates encapsulation, ArrayList, and HashMap usage.
 */
public class Room {
    // Private encapsulated fields
    private String name;
    private String description;
    private ArrayList<Item> items;
    private HashMap<String, Room> exits;
    private boolean locked;
    private String requiredItem;
    private boolean dark;
    private String lightItem;
    private boolean exitRoom;

    /**
     * Constructor for Room. Sets default values for locked, dark, and exitRoom attributes.
     *
     * @param name        Name of the room.
     * @param description Atmospheric description of the room.
     */
    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        this.items = new ArrayList<>();
        this.exits = new HashMap<>();
        this.locked = false;
        this.requiredItem = null;
        this.dark = false;
        this.lightItem = null;
        this.exitRoom = false;
    }

    /**
     * Adds an item to the room.
     *
     * @param item The item to add.
     */
    public void addItem(Item item) {
        if (item != null) {
            items.add(item);
        }
    }

    /**
     * Removes an item from the room.
     *
     * @param item The item to remove.
     */
    public void removeItem(Item item) {
        items.remove(item);
    }

    /**
     * Finds an item in the room by its name (case-insensitive).
     *
     * @param name The name of the item to look for.
     * @return The Item if found, or null if not found.
     */
    public Item findItem(String name) {
        if (name == null) {
            return null;
        }
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Sets an exit in a given direction leading to a target Room.
     *
     * @param direction Direction of exit (e.g., "north", "south", "east", "west").
     * @param room      Target room connected in that direction.
     */
    public void setExit(String direction, Room room) {
        exits.put(direction.toLowerCase(), room);
    }

    /**
     * Gets the Room located in the specified direction.
     *
     * @param direction Direction to travel.
     * @return The target Room, or null if no exit exists in that direction.
     */
    public Room getExit(String direction) {
        if (direction == null) {
            return null;
        }
        return exits.get(direction.toLowerCase());
    }

    /**
     * Returns a string listing all available exit directions for this room.
     *
     * @return Comma-separated list of exit directions.
     */
    public String getExitsString() {
        if (exits.isEmpty()) {
            return "none";
        }
        return String.join(", ", exits.keySet());
    }

    /**
     * Locks the room and specifies the required item needed to unlock it.
     *
     * @param requiredItem The name of the item required to unlock.
     */
    public void lock(String requiredItem) {
        this.locked = true;
        this.requiredItem = requiredItem;
    }

    /**
     * Unlocks the room.
     */
    public void unlock() {
        this.locked = false;
    }

    /**
     * Checks if the room is locked.
     *
     * @return true if locked, false otherwise.
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Gets the name of the item required to unlock the room.
     *
     * @return Required item name, or null if not locked.
     */
    public String getRequiredItem() {
        return requiredItem;
    }

    /**
     * Makes the room dark and sets the item required to illuminate it.
     *
     * @param lightItem The name of the item needed to see in the room.
     */
    public void makeDark(String lightItem) {
        this.dark = true;
        this.lightItem = lightItem;
    }

    /**
     * Checks if the room is dark.
     *
     * @return true if dark, false otherwise.
     */
    public boolean isDark() {
        return dark;
    }

    /**
     * Gets the name of the light source item needed to see in this room.
     *
     * @return Light item name, or null if not dark.
     */
    public String getLightItem() {
        return lightItem;
    }

    /**
     * Checks if this room is the final exit room of the game.
     *
     * @return true if this is the exit room, false otherwise.
     */
    public boolean isExitRoom() {
        return exitRoom;
    }

    /**
     * Sets whether this room is the final exit room.
     *
     * @param exitRoom true if exit room, false otherwise.
     */
    public void setExitRoom(boolean exitRoom) {
        this.exitRoom = exitRoom;
    }

    /**
     * Gets a formatted comma-separated string of item names in the room.
     *
     * @return List of item names, or "nothing" if the room contains no items.
     */
    public String getItemsString() {
        if (items.isEmpty()) {
            return "nothing";
        }
        ArrayList<String> names = new ArrayList<>();
        for (Item item : items) {
            names.add(item.getName());
        }
        return String.join(", ", names);
    }

    /**
     * Gets the name of the room.
     *
     * @return Room name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the room.
     *
     * @return Room description.
     */
    public String getDescription() {
        return description;
    }
}
