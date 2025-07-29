package quizapp;

import javax.swing.*;
import java.awt.*;

public class ResultPanel extends JPanel {

    private QuizAppGUI mainGUI;
    private JLabel resultLabel;
    private JButton exportButton;
    private JButton backToCategoryButton;

    public ResultPanel(QuizAppGUI gui) {
        this.mainGUI = gui;
        setLayout(new BorderLayout(10, 10));

        resultLabel = new JLabel("", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(resultLabel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();

        exportButton = new JButton("Export Results to CSV");
        backToCategoryButton = new JButton("Back to Categories");

        buttonsPanel.add(exportButton);
        buttonsPanel.add(backToCategoryButton);

        add(buttonsPanel, BorderLayout.SOUTH);

        exportButton.addActionListener(e -> {
            boolean success = mainGUI.getQuizProgress().exportResultsToCSV(mainGUI.getCurrentUser());
            if (success) {
                JOptionPane.showMessageDialog(this, "Results exported successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Error exporting results.");
            }
        });

        backToCategoryButton.addActionListener(e -> {
            mainGUI.getQuizProgress().clearProgress(mainGUI.getCurrentUser()); // ✅ moved here
            mainGUI.showCategory();
        });
    }

    public void displayResults() {
        int correct = mainGUI.getQuizProgress().getCorrectCount();
        int total = mainGUI.getQuizProgress().getTotalQuestions();

        resultLabel.setText("You answered " + correct + " out of " + total + " correctly!");

        // Update user stats
        mainGUI.getUserStatsManager().updateStats(mainGUI.getCurrentUser(), correct, total);

        // Removed clearProgress() from here to prevent wiping data before export
    }
}
