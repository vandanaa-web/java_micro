/**
 * TestPhase1 preserves the Phase 1 verification tests for reference/grading.
 */
public class TestPhase1 {

    public static void runTests() {
        System.out.println("==================================================");
        System.out.println("   HAUNTED MANSION ESCAPE - PHASE 1 VERIFICATION  ");
        System.out.println("==================================================\n");

        Room startingRoom = MansionBuilder.buildMansion();
        System.out.println("[TEST 1] Mansion Created Successfully.");
        System.out.println("Starting Room Name       : " + startingRoom.getName());
        System.out.println("Starting Room Description: " + startingRoom.getDescription());
        System.out.println("Available Exits          : " + startingRoom.getExitsString());
        System.out.println("Items in Starting Room   : " + startingRoom.getItemsString());
        System.out.println();

        Player player = new Player(startingRoom);
        System.out.println("[TEST 2] Player Initialized.");
        System.out.println("Player Current Room      : " + player.getCurrentRoom().getName());
        System.out.println("Player Inventory         : " + player.getInventoryString());
        System.out.println("Player Initial Moves     : " + player.getMoves());
        System.out.println();

        System.out.println("[TEST 3] Testing Item Pickup Mechanism...");
        Item key = startingRoom.findItem("Rusty Key");
        if (key != null) {
            System.out.println("Found item in room       : " + key.getName());
            if (key instanceof KeyItem) {
                KeyItem keyItem = (KeyItem) key;
                System.out.println("Key Unlocks Room         : " + keyItem.getUnlocks());
            }

            startingRoom.removeItem(key);
            player.pickUp(key);
            player.incrementMoves();

            System.out.println("After pickup - Room Items: " + startingRoom.getItemsString());
            System.out.println("After pickup - Inventory : " + player.getInventoryString());
            System.out.println("Player Moves Counter     : " + player.getMoves());
        }
        System.out.println();

        System.out.println("[TEST 4] Verifying Room Rules & Exit Graph...");
        Room library = startingRoom.getExit("east");
        System.out.println("East of Hall             : " + library.getName() + " (Locked? " + library.isLocked() + ")");

        Room basement = startingRoom.getExit("south");
        System.out.println("South of Hall            : " + basement.getName() + " (Dark? " + basement.isDark() + ")");

        Room frontDoor = basement.getExit("south");
        System.out.println("South of Basement        : " + frontDoor.getName() + " (ExitRoom? " + frontDoor.isExitRoom() + ")");
        System.out.println("\n==================================================");
    }
}
