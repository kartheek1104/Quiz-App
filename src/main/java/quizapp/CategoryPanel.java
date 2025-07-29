package quizapp;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CategoryPanel extends JPanel {

    private QuizAppGUI mainGUI;
    private DefaultListModel<String> categoryListModel;
    private JList<String> categoryList;
    private JLabel welcomeLabel;

    public CategoryPanel(QuizAppGUI gui) {
        this.mainGUI = gui;

        // Match LoginPanel style:
        setBackground(new Color(40, 44, 52));
        setLayout(new BorderLayout(10, 10));

        welcomeLabel = new JLabel("", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(200, 200, 200));
        add(welcomeLabel, BorderLayout.NORTH);

        categoryListModel = new DefaultListModel<>();
        categoryList = new JList<>(categoryListModel);
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryList.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        categoryList.setBackground(new Color(60, 63, 65));
        categoryList.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(categoryList);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(new Color(40, 44, 52));

        JButton startQuizButton = new JButton("Start Quiz");
        JButton logoutButton = new JButton("Logout");
        JButton statsButton = new JButton("View Stats");

        // Style buttons like login panel buttons:
        styleButton(startQuizButton, new Color(67, 181, 129));
        styleButton(logoutButton, new Color(29, 155, 240));
        styleButton(statsButton, new Color(241, 196, 15)); // gold-ish for stats

        buttonsPanel.add(startQuizButton);
        buttonsPanel.add(statsButton);
        buttonsPanel.add(logoutButton);

        add(buttonsPanel, BorderLayout.SOUTH);

        startQuizButton.addActionListener(e -> {
            String selected = categoryList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a category.");
                return;
            }
            mainGUI.getQuizProgress().startNewQuiz(selected);
            mainGUI.showQuiz();
        });

        logoutButton.addActionListener(e -> {
            mainGUI.setCurrentUser(null);
            mainGUI.showLogin();
        });

        statsButton.addActionListener(e -> mainGUI.showStatsDashboard());
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    public void loadCategories() {
        welcomeLabel.setText("Welcome, " + mainGUI.getCurrentUser() + "! Select a Quiz Category:");

        categoryListModel.clear();
        File folder = new File("src/main/resources/questions");
        if (!folder.exists() || !folder.isDirectory()) {
            JOptionPane.showMessageDialog(this, "Questions folder not found.");
            return;
        }
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            JOptionPane.showMessageDialog(this, "No categories found.");
            return;
        }
        for (File f : files) {
            String name = f.getName();
            categoryListModel.addElement(name.substring(0, name.lastIndexOf('.')));
        }
    }
}
