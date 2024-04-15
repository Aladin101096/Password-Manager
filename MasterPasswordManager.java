import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class MasterPasswordManager extends JFrame {
    private boolean unlocked = false;
    private String masterPassword;
    private PasswordManagerGUI passwordManagerGUI;
    private DatabaseManager dbManager;

    public MasterPasswordManager() {
        super("Master Password");
        dbManager = new DatabaseManager();
        if (displayWarning() == JOptionPane.YES_OPTION) {
            setupUI();
        } else {
            System.exit(0);
        }


    }


    private int displayWarning() {
        return JOptionPane.showOptionDialog(this,
                "Please note that the master password cannot be reset. If forgotten, all data will be lost.\n\nDo you understand?",
                "Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                new ImageIcon(Objects.requireNonNull(getClass().getResource("/WarningTriangle.png"))),
                null,
                null);
    }

    public void setupUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        addComponentsToPanel(panel, gbc);
        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void addComponentsToPanel(JPanel panel, GridBagConstraints gbc) {
        JLabel titleLabel = new JLabel("Create Master Password");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(titleLabel, gbc);


        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Create Username:"), gbc);

        JTextField masterUsernameField = new JTextField(20);
        panel.add(masterUsernameField, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Create Password:"), gbc);


        JPasswordField masterPasswordField = new JPasswordField(20);
        panel.add(masterPasswordField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx++;
        JPasswordField confirmMasterPasswordField = new JPasswordField(20);
        panel.add(confirmMasterPasswordField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton createButton = new JButton("Create");
        createButton.setPreferredSize(new Dimension(300, 40));
        createButton.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(createButton, gbc);

        createButton.addActionListener(e -> handleCreateButtonClick(masterPasswordField, confirmMasterPasswordField, masterUsernameField));
        getRootPane().setDefaultButton(createButton);

        JButton signIn = new JButton("Sign In");
        signIn.addActionListener(e -> {
            // Close the current window
            this.dispose();

            // Create and show the new window
            JFrame signInFrame = new JFrame("Sign In");
            signInFrame.setSize(300, 200);
            signInFrame.setLayout(new GridLayout(3, 2));

            JLabel usernameLabel = new JLabel("Username:");
            JTextField usernameField = new JTextField();
            JLabel passwordLabel = new JLabel("Password:");
            JPasswordField passwordField = new JPasswordField();

            signInFrame.add(usernameLabel);
            signInFrame.add(usernameField);
            signInFrame.add(passwordLabel);
            signInFrame.add(passwordField);

            JButton submitButton = new JButton("Submit");
            signInFrame.add(submitButton);
            submitButton.addActionListener(d -> {
                // Get the username and password from the text fields
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Check if the user exists in the database
                if (dbManager.checkUser(username, password)) {
                    // If the user exists, unlock the GUI
                    passwordManagerGUI.unlockGUI();
                    // Close the sign in window
                    signInFrame.dispose();
                } else {
                    // If the user does not exist, display an error message
                    JOptionPane.showMessageDialog(signInFrame, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            JButton registerButton = new JButton("Register");
            registerButton.addActionListener(a -> {
                // Close the current window
                signInFrame.dispose();

                // Return to the setupUI
                setupUI();
            });
            signInFrame.add(registerButton);

            signInFrame.setVisible(true);
        });

        gbc.gridy++;
        panel.add(signIn, gbc);
    }

    private void handleCreateButtonClick(JPasswordField masterPasswordField, JPasswordField confirmMasterPasswordField, JTextField masterUsernameField) {
        if (validateInput(masterPasswordField, confirmMasterPasswordField, masterUsernameField)) {
            dbManager.createUser(masterUsernameField.getText(), masterPassword);
            if (passwordManagerGUI != null) {
                passwordManagerGUI.unlockGUI();
                dispose(); // Close the setupUI window
            }
        }
        masterPasswordField.setText("");
        confirmMasterPasswordField.setText("");
    }

    private boolean validateInput(JPasswordField masterPasswordField, JPasswordField confirmMasterPasswordField, JTextField masterUsernameField) {
        masterPassword = new String(masterPasswordField.getPassword());
        String confirmPassword = new String(confirmMasterPasswordField.getPassword());
        String username = masterUsernameField.getText();
        if (masterPassword.isEmpty() || confirmPassword.isEmpty() || username.isEmpty()) {
            displayErrorMessage("Please fill in all fields.");
            return false;
        }
        if (!confirmPassword.equals(masterPassword)) {
            displayErrorMessage("Passwords do not match.");
            return false;
        }
        return true;
    }

    private void displayErrorMessage(String message) {
        JOptionPane.showMessageDialog(MasterPasswordManager.this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isUnlocked() {
        return unlocked;
    }


}