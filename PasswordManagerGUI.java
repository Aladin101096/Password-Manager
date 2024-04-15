import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;


/**
 * A graphical user interface for a password manager application.
 */
public class PasswordManagerGUI extends JFrame {

    // Instance variables
    private final Map<String, String[]> passwords = new HashMap<>();
    private final JTextField websiteField = new JTextField(20);
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JTextArea outputArea = new JTextArea(10, 20);
    private final JComboBox<String> websiteComboBox = new JComboBox<>();
    // Initialize MasterPasswordManager
    private final MasterPasswordManager masterPasswordManager = new MasterPasswordManager(); // Instance of MasterPasswordManager

    // Constructor
    // Method to unlock GUI

    public PasswordManagerGUI() {
        super("Password Manager");
        masterPasswordManager.setPasswordManagerGUI(this);
    }

    public void unlockGUI() {
        if (masterPasswordManager.isUnlocked()) {
            createGUI(); // Initialize GUI only if master password is unlocked
            setSize(600, 400);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

    // Method to create GUI
    private void createGUI() {
        // Creating the banner panel
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBackground(Color.LIGHT_GRAY);
        JLabel bannerLabel = new JLabel("Group 11 - Password Manager", SwingConstants.CENTER);
        bannerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        bannerLabel.setForeground(Color.RED);
        bannerPanel.add(bannerLabel, BorderLayout.CENTER);
        add(bannerPanel, BorderLayout.NORTH);

        // Creating the input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5); // Adjust spacing as needed

        JLabel websiteLabel = new JLabel("Website:");
        websiteLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        websiteLabel.setForeground(Color.BLUE);
        websiteLabel.setHorizontalAlignment(SwingConstants.LEFT);
        inputPanel.add(websiteLabel, gbc);

        gbc.gridy++;
        inputPanel.add(websiteField, gbc);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        usernameLabel.setForeground(Color.BLUE);
        usernameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridy++;
        inputPanel.add(usernameLabel, gbc);

        gbc.gridy++;
        inputPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        passwordLabel.setForeground(Color.BLUE);
        passwordLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridy++;
        inputPanel.add(passwordLabel, gbc);

        gbc.gridy++;
        inputPanel.add(passwordField, gbc);

        JButton addButton = new JButton("<html><div style='text-align: center;'>Add</div></html>");
        addButton.setPreferredSize(new Dimension(150, 40));
        addButton.addActionListener(e -> addPassword());
        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(addButton, gbc);

        JButton retrieveButton = new JButton("<html><div style='text-align: center;'>Retrieve</div></html>");
        retrieveButton.setPreferredSize(new Dimension(150, 40));
        retrieveButton.addActionListener(e -> retrievePassword());
        gbc.gridx++;
        inputPanel.add(retrieveButton, gbc);

        JButton generateButton = new JButton("<html><div style='text-align: center;'>Generate Password</div></html>");
        generateButton.setPreferredSize(new Dimension(150, 40));
        generateButton.addActionListener(e -> generateRandomPassword());
        gbc.gridx--;
        gbc.gridy++;
        inputPanel.add(generateButton, gbc);

        JButton deleteButton = new JButton("<html><div style='text-align: center;'>Delete</div></html>");
        deleteButton.setPreferredSize(new Dimension(150, 40));
        deleteButton.addActionListener(e -> deletePassword());
        gbc.gridx++;
        inputPanel.add(deleteButton, gbc);

        gbc.gridy++;
        gbc.gridx--;
        gbc.gridwidth = 2; // Span 2 columns for the JComboBox
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally
        gbc.anchor = GridBagConstraints.CENTER; // Center horizontally
        inputPanel.add(websiteComboBox, gbc);

        add(inputPanel, BorderLayout.CENTER);

        // Creating the output panel
        outputArea.setEditable(false); // Making the output area non-editable
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(400, 100)); // Adjust size as needed
        add(scrollPane, BorderLayout.SOUTH);

        // Set the default button
        getRootPane().setDefaultButton(addButton);

        // Maximize the window
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    // Method to add password
    private void addPassword() {
        String website = websiteField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (!website.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
            // Generate salt
            PasswordEncryptor encryptor = new PasswordEncryptor();
            byte[] salt = encryptor.generateSalt();

            // Hash and salt the password
            String hashedPassword = encryptor.hashAndSaltPassword(password, salt);

            // Store credentials
            passwords.put(website, new String[]{username, hashedPassword, Base64.getEncoder().encodeToString(salt)});
            outputArea.append("Credentials for " + website + " added.\n");
            updateWebsiteComboBox();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
        }
    }

    // Method to retrieve password
    private void retrievePassword() {
        String website = (String) websiteComboBox.getSelectedItem();
        if (website != null && passwords.containsKey(website)) {
            String[] credentials = passwords.get(website);
            outputArea.append("Website: " + website + "\n");
            outputArea.append("Username: " + credentials[0] + "\n");
            outputArea.append("Password: " + credentials[1] + "\n\n");
        } else {
            JOptionPane.showMessageDialog(this, "No credentials found for selected website.");
        }
    }

    // Method to delete password
    private void deletePassword() {
        String website = (String) websiteComboBox.getSelectedItem();
        if (website != null && passwords.containsKey(website)) {
            passwords.remove(website);
            outputArea.setText(""); // Clear the output area
            updateWebsiteComboBox();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "No credentials found for selected account.");
        }
    }

    // Method to generate random password
    private void generateRandomPassword() {
        String password = new PasswordEncryptor().generatePassword();
        passwordField.setText(password);
    }

    // Method to update website combo box
    private void updateWebsiteComboBox() {
        websiteComboBox.removeAllItems();
        for (String website : passwords.keySet()) websiteComboBox.addItem(website);
    }

    // Method to clear input fields
    private void clearFields() {
        websiteField.setText("");
        usernameField.setText("");
        passwordField.setText("");
    }

    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PasswordManagerGUI::new);
    }
}
