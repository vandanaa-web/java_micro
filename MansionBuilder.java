/**
 * MansionBuilder is a helper class responsible for constructing the haunted mansion layout.
 * It instantiates all rooms, items, connects room exits in pairs, and configures rules
 * such as locked doors and dark rooms.
 */
public class MansionBuilder {

    /**
     * Builds the entire mansion map with rooms, items, exits, and special conditions.
     *
     * @return The starting room of the mansion (the Hall).
     */
    public static Room buildMansion() {
        // 1. Create Room objects with short atmospheric descriptions
        Room hall = new Room("Hall", "A dusty, cavernous foyer with faded wallpaper and a cobweb-covered chandelier.");
        Room kitchen = new Room("Kitchen", "A cold stone kitchen filled with rusted copper pots and ominous shadow-covered alcoves.");
        Room library = new Room("Library", "A majestic room lined with towering bookshelves containing ancient decaying volumes.");
        Room basement = new Room("Basement", "A cold underground chamber smelling of damp earth and old stone.");
        Room frontDoor = new Room("Front Door", "A heavy oak portal bound in iron bars, leading to freedom under the moonlight.");

        // 2. Connect Exits (bidirectional mappings)
        hall.setExit("west", kitchen);
        hall.setExit("east", library);
        hall.setExit("south", basement);

        kitchen.setExit("east", hall);
        library.setExit("west", hall);
        basement.setExit("north", hall);

        basement.setExit("south", frontDoor);
        frontDoor.setExit("north", basement);

        // 3. Create and place Items in Rooms
        KeyItem rustyKey = new KeyItem("Rusty Key", "An old corroded key that looks like it opens an ornate lock.", "Library");
        hall.addItem(rustyKey);

        Item candle = new Item("Candle", "A flickering wax candle that emits a soft warm light.");
        kitchen.addItem(candle);

        Item oldBook = new Item("Old Book", "A heavy leather tome. A faded margin note hints at a silver key hidden in the dark basement.");
        library.addItem(oldBook);

        KeyItem silverKey = new KeyItem("Silver Key", "A shiny silver key engraved with intricate patterns.", "Front Door");
        basement.addItem(silverKey);

        // 4. Encode Room Data Rules (Locks, Darkness, Exit Room)
        library.lock("Rusty Key");
        basement.makeDark("Candle");
        frontDoor.lock("Silver Key");
        frontDoor.setExitRoom(true);

        return hall;
    }
}
