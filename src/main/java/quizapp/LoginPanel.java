package quizapp;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class LoginPanel extends JPanel {

    private QuizAppGUI mainGUI;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private JLabel titleLabel, messageLabel;

    public LoginPanel(QuizAppGUI gui) {
        this.mainGUI = gui;
        setLayout(new GridBagLayout());
        setBackground(new Color(40, 44, 52)); // dark background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title label
        titleLabel = new JLabel("Welcome to QuizApp");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(new Color(200, 200, 200));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Username label and field
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        add(userLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 1;
        add(usernameField, gbc);

        // Password label and field
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 2;
        gbc.gridx = 0;
        add(passLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Message label (for errors and info)
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(new Color(255, 100, 100));
        messageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(messageLabel, gbc);

        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(new Color(40, 44, 52));
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(new Color(67, 181, 129)); // green
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        buttonsPanel.add(loginButton);

        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        registerButton.setBackground(new Color(29, 155, 240)); // blue
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        buttonsPanel.add(registerButton);

        gbc.gridy = 4;
        add(buttonsPanel, gbc);

        // Button actions
        loginButton.addActionListener(e -> performLogin());
        registerButton.addActionListener(e -> mainGUI.showRegister());
    }

    // ---------- Updated method ----------
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both username and password.");
            return;
        }

        boolean authenticated = mainGUI.getUserStatsManager().authenticateUser(username, password);
        if (!authenticated) {
            messageLabel.setForeground(new Color(255, 100, 100));
            messageLabel.setText("Invalid username or password.");
            return;
        }

        // SUCCESS ────────────────────────────────────────────────
        messageLabel.setForeground(new Color(0, 200, 0));
        messageLabel.setText("Login successful! Checking saved progress");

        // Clear input boxes
        usernameField.setText("");
        passwordField.setText("");

        // --------------------------------------------------------
        boolean resumed = false;
        File progressFile = new File("progress_" + username + ".dat");
        if (progressFile.exists()) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Saved quiz progress found. Do you want to resume?",
                    "Resume Quiz",
                    JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                mainGUI.getQuizProgress().loadProgress(username);
                resumed = true;
            } else {
                mainGUI.getQuizProgress().clearProgress(username);
            }
        } else {
            mainGUI.getQuizProgress().clearProgress(username);
        }

        mainGUI.setCurrentUser(username);

        // Give the success message ~0.8 s, then move on
        boolean finalResumed = resumed;
        Timer t = new Timer(800, evt -> {
            messageLabel.setText(" ");
            messageLabel.setForeground(new Color(255, 100, 100));
            if (finalResumed) {
                mainGUI.showQuiz();      // jump straight into quiz
            } else {
                mainGUI.showCategory();  // go to category selection
            }
        });
        t.setRepeats(false);
        t.start();
    }
}
