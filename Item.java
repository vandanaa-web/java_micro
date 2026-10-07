/**
 * Item represents a general item in the Haunted Mansion game.
 * It stores basic properties such as the item's name and description.
 * This class serves as the base class for specialized items like KeyItem.
 */
public class Item {
    // Encapsulated private fields
    private String name;
    private String description;

    /**
     * Constructor to initialize an Item with a name and a description.
     *
     * @param name        The name of the item.
     * @param description A short atmospheric description of the item.
     */
    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the name of the item.
     *
     * @return The item's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the item.
     *
     * @return The item's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns a string representation of the item.
     *
     * @return Item name and description formatted cleanly.
     */
    @Override
    public String toString() {
        return name + ": " + description;
    }
}
