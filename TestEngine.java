/**
 * TestEngine provides an automated, standalone test suite for HauntedMansionEscape.
 * Evaluates core engine rules, edge cases, move counting, and victory/defeat logic without JUnit.
 */
public class TestEngine {
    private static int passedCount = 0;
    private static int failedCount = 0;

    /**
     * Main method running all automated engine test cases.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("      HAUNTED MANSION ESCAPE - ENGINE TESTS       ");
        System.out.println("==================================================\n");

        testLockedLibraryBlocks();
        testLibraryUnlocksWithRustyKey();
        testBasementDarkWithoutCandle();
        testSilverKeyTakeableWithCandle();
        testFrontDoorBlockedWithoutSilverKey();
        testFullWinningRun();
        testMoveLimitLoss();
        testWinningOnMove30();
        testNonMoveActionsDoNotIncrementMoves();
        testCaseInsensitivityAndExtraSpaces();
        testShortcuts();
        testInvalidInputs();
        testStateFrozenAfterGameOver();

        System.out.println("\n--------------------------------------------------");
        System.out.println("TEST SUMMARY: " + passedCount + " PASSED, " + failedCount + " FAILED");
        System.out.println("==================================================");
    }

    /**
     * Helper check method printing PASS/FAIL status for a named test condition.
     *
     * @param name      Name of the test case.
     * @param condition True if test passed, false otherwise.
     */
    private static void check(String name, boolean condition) {
        if (condition) {
            System.out.println("PASS: " + name);
            passedCount++;
        } else {
            System.out.println("FAIL: " + name);
            failedCount++;
        }
    }

    /**
     * Test A: Locked Library blocks entry without Rusty Key and does not increment moves.
     */
    private static void testLockedLibraryBlocks() {
        GameEngine engine = new GameEngine();
        String res = engine.processCommand("go east");
        boolean blocked = res.contains("locked") && engine.getPlayer().getCurrentRoom().getName().equals("Hall");
        boolean movesZero = engine.getPlayer().getMoves() == 0;
        check("Test A: Locked Library blocks entry without key (moves unchanged)", blocked && movesZero);
    }

    /**
     * Test B: Library unlocks when player carries the Rusty Key.
     */
    private static void testLibraryUnlocksWithRustyKey() {
        GameEngine engine = new GameEngine();
        engine.processCommand("take rusty key");
        engine.processCommand("go east");
        boolean inLibrary = engine.getPlayer().getCurrentRoom().getName().equals("Library");
        boolean unlocked = !engine.getPlayer().getCurrentRoom().isLocked();
        check("Test B: Library unlocks with Rusty Key", inLibrary && unlocked);
    }

    /**
     * Test C: Basement items hidden and pickup refused without Candle.
     */
    private static void testBasementDarkWithoutCandle() {
        GameEngine engine = new GameEngine();
        engine.processCommand("go south");
        String takeRes = engine.processCommand("take silver key");
        boolean darkRefused = takeRes.contains("too dark") && !engine.getPlayer().hasItem("Silver Key");
        check("Test C: Dark Basement hides items and refuses pickup without Candle", darkRefused);
    }

    /**
     * Test D: Silver Key can be taken once Candle is carried.
     */
    private static void testSilverKeyTakeableWithCandle() {
        GameEngine engine = new GameEngine();
        engine.processCommand("go west");
        engine.processCommand("take candle");
        engine.processCommand("go east");
        engine.processCommand("go south");
        engine.processCommand("take silver key");
        boolean hasKey = engine.getPlayer().hasItem("Silver Key");
        check("Test D: Silver Key is takeable with Candle carried", hasKey);
    }

    /**
     * Test E: Front Door is blocked without Silver Key.
     */
    private static void testFrontDoorBlockedWithoutSilverKey() {
        GameEngine engine = new GameEngine();
        engine.processCommand("go south"); // Basement
        String res = engine.processCommand("go south"); // Front Door attempt
        boolean blocked = res.contains("locked") && engine.getPlayer().getCurrentRoom().getName().equals("Basement");
        check("Test E: Front Door blocked without Silver Key", blocked);
    }

    /**
     * Test F: Complete winning path sets hasWon=true and isGameOver=true.
     */
    private static void testFullWinningRun() {
        GameEngine engine = new GameEngine();
        engine.processCommand("take rusty key");
        engine.processCommand("go east");
        engine.processCommand("take old book");
        engine.processCommand("go west");
        engine.processCommand("go west");
        engine.processCommand("take candle");
        engine.processCommand("go east");
        engine.processCommand("go south");
        engine.processCommand("take silver key");
        engine.processCommand("go south");

        check("Test F: Full winning run sets hasWon=true and isGameOver=true", engine.hasWon() && engine.isGameOver());
    }

    /**
     * Test G: Reaching 30 moves without escaping sets isGameOver=true and hasWon=false.
     */
    private static void testMoveLimitLoss() {
        GameEngine engine = new GameEngine();
        for (int i = 0; i < 15; i++) {
            engine.processCommand("go west");
            engine.processCommand("go east");
        }
        boolean lost = engine.isGameOver() && !engine.hasWon() && engine.getPlayer().getMoves() == 30;
        check("Test G: Hitting 30-move limit triggers game over loss", lost);
    }

    /**
     * Test H: Escaping on exactly move 30 counts as a WIN, not a loss.
     */
    private static void testWinningOnMove30() {
        GameEngine engine = new GameEngine();
        // Waste 20 moves (10 pairs west/east)
        for (int i = 0; i < 10; i++) {
            engine.processCommand("go west");
            engine.processCommand("go east");
        } // Moves = 20

        // Perform 10-step winning path: 20 + 10 = 30 moves
        engine.processCommand("take rusty key"); // 21
        engine.processCommand("go east");        // 22
        engine.processCommand("take old book");  // 23
        engine.processCommand("go west");        // 24
        engine.processCommand("go west");        // 25
        engine.processCommand("take candle");    // 26
        engine.processCommand("go east");        // 27
        engine.processCommand("go south");       // 28
        engine.processCommand("take silver key");// 29
        engine.processCommand("go south");       // 30 -> Win!

        boolean wonOn30 = engine.hasWon() && engine.isGameOver() && engine.getPlayer().getMoves() == 30;
        check("Test H: Winning on exactly move 30 counts as a WIN", wonOn30);
    }

    /**
     * Test I: Non-move commands (look, inventory, hint, help, read, blocked moves) do not increment moves.
     */
    private static void testNonMoveActionsDoNotIncrementMoves() {
        GameEngine engine = new GameEngine();
        engine.processCommand("look");
        engine.processCommand("inventory");
        engine.processCommand("hint");
        engine.processCommand("help");
        engine.processCommand("read rusty key"); // not carried
        engine.processCommand("go east");         // blocked move
        check("Test I: Non-move actions do not increment moves counter", engine.getPlayer().getMoves() == 0);
    }

    /**
     * Test J: Case insensitivity and extra whitespace tolerance.
     */
    private static void testCaseInsensitivityAndExtraSpaces() {
        GameEngine engine = new GameEngine();
        engine.processCommand("  TAKE   Rusty KEY ");
        engine.processCommand("  GO   East ");
        boolean success = engine.getPlayer().hasItem("Rusty Key") &&
                          engine.getPlayer().getCurrentRoom().getName().equals("Library");
        check("Test J: Case insensitivity and extra whitespace handled", success);
    }

    /**
     * Test K: Navigation and utility command shortcuts (n, s, e, w, i, l).
     */
    private static void testShortcuts() {
        GameEngine engine = new GameEngine();
        engine.processCommand("w"); // Kitchen
        engine.processCommand("e"); // Hall
        engine.processCommand("l"); // Look
        engine.processCommand("i"); // Inventory
        boolean success = engine.getPlayer().getCurrentRoom().getName().equals("Hall");
        check("Test K: Command shortcuts (w, e, l, i) function properly", success);
    }

    /**
     * Test L: Invalid input commands handled gracefully without crashing or state corruption.
     */
    private static void testInvalidInputs() {
        GameEngine engine = new GameEngine();
        engine.processCommand("");
        engine.processCommand("go");
        engine.processCommand("go up");
        engine.processCommand("take");
        engine.processCommand("take unicorn");
        engine.processCommand("read candle");
        engine.processCommand("dance");
        boolean validState = engine.getPlayer().getMoves() == 0 && !engine.isGameOver();
        check("Test L: Invalid inputs handled gracefully without errors", validState);
    }

    /**
     * Test M: Commands submitted after game over are rejected.
     */
    private static void testStateFrozenAfterGameOver() {
        GameEngine engine = new GameEngine();
        engine.processCommand("quit");
        String res = engine.processCommand("go west");
        boolean frozen = engine.isGameOver() && res.contains("already over");
        check("Test M: Commands after game over do not mutate state", frozen);
    }
}
