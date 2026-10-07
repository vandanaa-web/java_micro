import java.util.Scanner;

/**
 * Game serves as a thin console wrapper around GameEngine.
 * Reads user input from Scanner and prints output from GameEngine to System.out.
 */
public class Game {
    private GameEngine engine;
    private Scanner scanner;

    /**
     * Constructor for Game. Instantiates the backend GameEngine and input Scanner.
     */
    public Game() {
        this.engine = new GameEngine();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Runs the console game loop using GameEngine.
     */
    public void run() {
        System.out.println(engine.getWelcomeText());

        while (!engine.isGameOver()) {
            System.out.print("\n> ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String input = scanner.nextLine();

            if (input.trim().isEmpty()) {
                continue;
            }

            String response = engine.processCommand(input);
            if (response != null && !response.isEmpty()) {
                System.out.println(response);
            }
        }

        scanner.close();
    }
}
