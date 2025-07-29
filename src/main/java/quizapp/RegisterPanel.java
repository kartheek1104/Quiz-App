package quizapp;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {
    private QuizAppGUI mainGUI;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel messageLabel;

    public RegisterPanel(QuizAppGUI gui) {
        this.mainGUI = gui;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = new JLabel("Register");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(10, 10, 30, 10);
        add(titleLabel, gbc);

        gbc.gridwidth = 1; gbc.insets = new Insets(5, 10, 5, 10);

        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.LINE_END;
        add(userLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.LINE_START;
        add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.LINE_END;
        add(passLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.LINE_START;
        add(passwordField, gbc);

        JLabel confirmPassLabel = new JLabel("Confirm Password:");
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.LINE_END;
        add(confirmPassLabel, gbc);

        confirmPasswordField = new JPasswordField(20);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.LINE_START;
        add(confirmPasswordField, gbc);

        JButton registerButton = new JButton("Register");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        add(registerButton, gbc);

        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        gbc.gridy = 5;
        add(messageLabel, gbc);

        JButton backToLoginButton = new JButton("Back to Login");
        gbc.gridy = 6;
        add(backToLoginButton, gbc);

        registerButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                messageLabel.setText("Please fill all fields.");
                return;
            }
            if (!password.equals(confirmPassword)) {
                messageLabel.setText("Passwords do not match.");
                return;
            }
            boolean success = mainGUI.getUserStatsManager().registerUser(username, password);
            if (success) {
                JOptionPane.showMessageDialog(this, "Registration successful! Please login.");
                mainGUI.showLogin();
            } else {
                messageLabel.setText("Username already exists.");
            }
        });

        backToLoginButton.addActionListener(e -> mainGUI.showLogin());
    }
}
