import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * GameGUI provides a rich Swing graphical user interface for HauntedMansionEscape.
 * 
 * Layout Structure (BorderLayout):
 *  - CENTER : Monospaced dark text area showing output inside a JScrollPane.
 *  - SOUTH  : JTextField input and "Send" JButton.
 *  - EAST   : Side panel displaying Room name, Moves Left, and Inventory list.
 *  - WEST   : Button panel for quick navigation (North/South/East/West) and actions.
 */
public class GameGUI extends JFrame {
    // Backend engine reference
    private GameEngine engine;

    // Swing UI Components
    private JTextArea outputArea;
    private JTextField inputField;
    private JButton sendButton;
    private JLabel roomLabel;
    private JLabel movesLabel;
    private JTextArea inventoryArea;

    // List of action buttons to easily disable when game ends
    private List<JButton> controlButtons;

    /**
     * Constructor for GameGUI. Initializes window settings, builds UI layout,
     * and displays initial game welcome text.
     */
    public GameGUI() {
        this.engine = new GameEngine();
        this.controlButtons = new ArrayList<>();

        // Frame Window Configuration
        setTitle("Haunted Mansion Escape");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window on screen
        setLayout(new BorderLayout(10, 10));

        // Construct Panels
        initCenterPanel();
        initSouthPanel();
        initEastPanel();
        initWestPanel();

        // Print initial welcome text and update sidebar
        outputArea.setText(engine.getWelcomeText());
        updateSidebar();
    }

    /**
     * Builds CENTER panel: Scrollable monospaced dark terminal text area.
     */
    private void initCenterPanel() {
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        outputArea.setBackground(new Color(20, 24, 30));
        outputArea.setForeground(new Color(80, 230, 120)); // Matrix Green text
        outputArea.setCaretColor(Color.WHITE);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            "Mansion Log",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font(Font.SANS_SERIF, Font.BOLD, 12),
            Color.LIGHT_GRAY
        ));

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Builds SOUTH panel: Input command textfield and Send button.
     */
    private void initSouthPanel() {
        JPanel southPanel = new JPanel(new BorderLayout(5, 5));
        southPanel.setBorder(new EmptyBorder(5, 10, 10, 10));

        JLabel promptLabel = new JLabel(" Command: > ");
        promptLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));

        inputField = new JTextField();
        inputField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        inputField.addActionListener(e -> processUserInput(inputField.getText()));

        sendButton = new JButton("Send");
        sendButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        sendButton.addActionListener(e -> processUserInput(inputField.getText()));

        southPanel.add(promptLabel, BorderLayout.WEST);
        southPanel.add(inputField, BorderLayout.CENTER);
        southPanel.add(sendButton, BorderLayout.EAST);

        add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * Builds EAST panel: Room name, Moves Left, and Inventory viewer.
     */
    private void initEastPanel() {
        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
        eastPanel.setPreferredSize(new Dimension(200, 0));
        eastPanel.setBorder(new EmptyBorder(10, 5, 10, 10));

        // Status Sub-panel
        JPanel statusBox = new JPanel(new GridLayout(2, 1, 5, 5));
        statusBox.setBorder(BorderFactory.createTitledBorder("Status"));

        roomLabel = new JLabel("Room: Hall");
        roomLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));

        movesLabel = new JLabel("Moves left: 30");
        movesLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        movesLabel.setForeground(new Color(180, 50, 50));

        statusBox.add(roomLabel);
        statusBox.add(movesLabel);

        // Inventory Sub-panel
        inventoryArea = new JTextArea();
        inventoryArea.setEditable(false);
        inventoryArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        inventoryArea.setBackground(new Color(245, 245, 245));
        inventoryArea.setLineWrap(true);
        inventoryArea.setWrapStyleWord(true);

        JScrollPane invScroll = new JScrollPane(inventoryArea);
        invScroll.setBorder(BorderFactory.createTitledBorder("Inventory"));

        eastPanel.add(statusBox);
        eastPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        eastPanel.add(invScroll);

        add(eastPanel, BorderLayout.EAST);
    }

    /**
     * Builds WEST panel: Direction navigation buttons and quick action buttons.
     */
    private void initWestPanel() {
        JPanel westPanel = new JPanel();
        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
        westPanel.setBorder(new EmptyBorder(10, 10, 10, 5));
        westPanel.setPreferredSize(new Dimension(160, 0));

        // 1. Directional D-Pad Panel
        JPanel dPad = new JPanel(new GridLayout(3, 3, 2, 2));
        dPad.setBorder(BorderFactory.createTitledBorder("Navigation"));

        JButton btnNorth = createButton("North", e -> processUserInput("go north"));
        JButton btnSouth = createButton("South", e -> processUserInput("go south"));
        JButton btnEast  = createButton("East",  e -> processUserInput("go east"));
        JButton btnWest  = createButton("West",  e -> processUserInput("go west"));

        dPad.add(new JLabel("")); dPad.add(btnNorth); dPad.add(new JLabel(""));
        dPad.add(btnWest);       dPad.add(new JLabel("")); dPad.add(btnEast);
        dPad.add(new JLabel("")); dPad.add(btnSouth); dPad.add(new JLabel(""));

        // 2. Actions Panel
        JPanel actionBox = new JPanel(new GridLayout(6, 1, 4, 4));
        actionBox.setBorder(BorderFactory.createTitledBorder("Actions"));

        JButton btnLook = createButton("Look", e -> processUserInput("look"));
        JButton btnTake = createButton("Take Item...", e -> handleTakeDialog());
        JButton btnRead = createButton("Read Item...", e -> handleReadDialog());
        JButton btnHint = createButton("Hint", e -> processUserInput("hint"));
        JButton btnHelp = createButton("Help", e -> processUserInput("help"));
        JButton btnQuit = createButton("Quit", e -> processUserInput("quit"));

        actionBox.add(btnLook);
        actionBox.add(btnTake);
        actionBox.add(btnRead);
        actionBox.add(btnHint);
        actionBox.add(btnHelp);
        actionBox.add(btnQuit);

        westPanel.add(dPad);
        westPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        westPanel.add(actionBox);

        add(westPanel, BorderLayout.WEST);
    }

    /**
     * Helper to create styled JButtons and add them to control tracking list.
     */
    private JButton createButton(String text, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        btn.setFocusable(false);
        btn.addActionListener(listener);
        controlButtons.add(btn);
        return btn;
    }

    /**
     * Opens a dialog prompting user to enter an item name to take.
     */
    private void handleTakeDialog() {
        if (engine.isGameOver()) return;
        String itemName = JOptionPane.showInputDialog(this, "Enter item name to take:", "Take Item", JOptionPane.QUESTION_MESSAGE);
        if (itemName != null && !itemName.trim().isEmpty()) {
            processUserInput("take " + itemName.trim());
        }
    }

    /**
     * Opens a dialog prompting user to enter an item name to read.
     */
    private void handleReadDialog() {
        if (engine.isGameOver()) return;
        String itemName = JOptionPane.showInputDialog(this, "Enter item name to read:", "Read Item", JOptionPane.QUESTION_MESSAGE);
        if (itemName != null && !itemName.trim().isEmpty()) {
            processUserInput("read " + itemName.trim());
        }
    }

    /**
     * Sends input command to GameEngine, appends output to JTextArea,
     * updates sidebar widgets, and handles game over states.
     *
     * @param command Command string.
     */
    private void processUserInput(String command) {
        if (engine.isGameOver() || command == null || command.trim().isEmpty()) {
            return;
        }

        // Echo user command to output area
        outputArea.append("\n> " + command + "\n");
        inputField.setText("");

        // Process command in backend engine
        String response = engine.processCommand(command);
        if (response != null && !response.isEmpty()) {
            outputArea.append(response + "\n");
        }

        // Auto-scroll output area to bottom
        outputArea.setCaretPosition(outputArea.getDocument().getLength());

        // Refresh sidebar UI
        updateSidebar();

        // Check for Game Over condition
        if (engine.isGameOver()) {
            disableAllControls();

            if (engine.hasWon()) {
                JOptionPane.showMessageDialog(this,
                    "🎉 CONGRATULATIONS!\nYou successfully escaped the Haunted Mansion in " + engine.getPlayer().getMoves() + " moves!",
                    "Victory!",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "GAME OVER!\nYou failed to escape the Haunted Mansion.",
                    "Game Over",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Refreshes the side panel widgets (Room Name, Moves Left, Inventory).
     */
    private void updateSidebar() {
        Room currentRoom = engine.getPlayer().getCurrentRoom();
        roomLabel.setText("Room: " + currentRoom.getName());
        movesLabel.setText("Moves left: " + engine.getMovesLeft());

        // Update inventory listing
        String inv = engine.getPlayer().getInventoryString();
        if (inv.equals("nothing")) {
            inventoryArea.setText("(Empty)");
        } else {
            // Display items separated by line breaks
            inventoryArea.setText(inv.replace(", ", "\n• "));
            inventoryArea.setText("• " + inventoryArea.getText());
        }
    }

    /**
     * Disables input field and all control buttons when game ends.
     */
    private void disableAllControls() {
        inputField.setEnabled(false);
        sendButton.setEnabled(false);
        for (JButton btn : controlButtons) {
            btn.setEnabled(false);
        }
    }
}
