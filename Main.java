import javax.swing.SwingUtilities;

/**
 * Main launcher class for HauntedMansionEscape GUI application.
 */
public class Main {

    /**
     * Main entry point launching Swing GUI thread.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameGUI().setVisible(true));
    }
}
