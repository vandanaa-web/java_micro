/**
 * WebMain provides the entry point for starting the HauntedMansionEscape HTTP Web Server.
 */
public class WebMain {

    /**
     * Main entry point. Instantiates and starts WebServer.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        new WebServer().start();
    }
}
