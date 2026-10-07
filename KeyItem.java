/**
 * KeyItem represents a specialized Item that can unlock a specific room.
 * Demonstrates inheritance by extending the base Item class.
 */
public class KeyItem extends Item {
    // Encapsulated field specific to KeyItem
    private String unlocks;

    /**
     * Constructor to create a KeyItem.
     *
     * @param name        The name of the key item.
     * @param description A short atmospheric description.
     * @param unlocks     The name of the room this key can unlock.
     */
    public KeyItem(String name, String description, String unlocks) {
        super(name, description);
        this.unlocks = unlocks;
    }

    /**
     * Gets the name of the room this key unlocks.
     *
     * @return The target room name.
     */
    public String getUnlocks() {
        return unlocks;
    }
}
