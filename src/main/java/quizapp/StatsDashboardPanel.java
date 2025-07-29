package quizapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class StatsDashboardPanel extends JPanel {

    private QuizAppGUI mainGUI;

    private JLabel welcomeLabel = new JLabel("", SwingConstants.CENTER);
    private JTabbedPane tabbedPane = new JTabbedPane();

    // History tab components
    private JTable historyTable;
    private DefaultTableModel historyTableModel;

    // Graphs tab components
    private JPanel chartsPanel = new JPanel(new GridLayout(1, 2));

    public StatsDashboardPanel(QuizAppGUI gui) {
        this.mainGUI = gui;

        // Style like LoginPanel:
        setBackground(new Color(40, 44, 52));
        setLayout(new BorderLayout(10, 10));

        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(200, 200, 200));
        add(welcomeLabel, BorderLayout.NORTH);

        add(tabbedPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(40, 44, 52));

        JButton backToCategoriesButton = new JButton("Back to Categories");
        JButton logoutButton = new JButton("Logout");

        styleButton(backToCategoriesButton, new Color(241, 196, 15)); // gold
        styleButton(logoutButton, new Color(29, 155, 240)); // blue

        backToCategoriesButton.addActionListener(e -> mainGUI.showCategory());

        logoutButton.addActionListener(e -> {
            mainGUI.setCurrentUser(null);
            mainGUI.showLogin();
        });

        bottomPanel.add(backToCategoriesButton);
        bottomPanel.add(logoutButton);

        add(bottomPanel, BorderLayout.SOUTH);

        setupHistoryTab();
        setupGraphsTab();
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    @SuppressWarnings("serial")
    private void setupHistoryTab() {
        String[] columns = {"Date", "Score", "Total Questions", "Percentage"};
        historyTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        historyTable = new JTable(historyTableModel);
        historyTable.setBackground(new Color(60, 63, 65));
        historyTable.setForeground(Color.WHITE);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        historyTable.getTableHeader().setBackground(new Color(40, 44, 52));
        historyTable.getTableHeader().setForeground(new Color(241, 196, 15));
        JScrollPane scrollPane = new JScrollPane(historyTable);

        tabbedPane.addTab("Quiz History", scrollPane);
    }

    private void setupGraphsTab() {
        tabbedPane.addTab("Graphs & Comparisons", chartsPanel);
    }

    public void refreshData() {
        String user = mainGUI.getCurrentUser();
        welcomeLabel.setText("Welcome, " + user + "! Your Quiz Statistics:");

        // Update history table
        updateHistoryTable(user);

        // Update charts
        updateCharts(user);
    }

    private void updateHistoryTable(String user) {
        historyTableModel.setRowCount(0); // clear old data

        // Get quiz history from UserStatsManager
        List<UserStatsManager.QuizAttempt> history = mainGUI.getUserStatsManager().getQuizHistory(user);

        if (history == null || history.isEmpty()) {
            historyTableModel.addRow(new Object[]{"No quiz history available", "", "", ""});
            return;
        }

        for (UserStatsManager.QuizAttempt attempt : history) {
            double percentage = attempt.getTotalQuestions() == 0 ? 0.0 :
                    (attempt.getCorrectAnswers() * 100.0) / attempt.getTotalQuestions();

            historyTableModel.addRow(new Object[]{
                    attempt.getDate(),
                    attempt.getCorrectAnswers(),
                    attempt.getTotalQuestions(),
                    String.format("%.2f%%", percentage)
            });
        }
    }

    private void updateCharts(String user) {
        chartsPanel.removeAll();

        UserStatsManager.Stats stats = mainGUI.getUserStatsManager().getStats(user);

        // Bar chart dataset: quizzes taken, correct answers, questions attempted
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        barDataset.addValue(stats.getTotalQuizzesTaken(), "Count", "Quizzes Taken");
        barDataset.addValue(stats.getTotalCorrectAnswers(), "Count", "Correct Answers");
        barDataset.addValue(stats.getTotalQuestionsAttempted(), "Count", "Questions Attempted");

        JFreeChart barChart = ChartFactory.createBarChart(
                "Quiz Performance",
                "Metric",
                "Count",
                barDataset
        );
        ChartPanel barChartPanel = new ChartPanel(barChart);
        chartsPanel.add(barChartPanel);

        // Pie chart dataset: accuracy percentage
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        double accuracy = stats.getTotalQuestionsAttempted() == 0 ? 0 :
                (double) stats.getTotalCorrectAnswers() / stats.getTotalQuestionsAttempted() * 100;

        pieDataset.setValue("Correct", accuracy);
        pieDataset.setValue("Incorrect", 100 - accuracy);

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Accuracy %",
                pieDataset,
                true,
                true,
                false
        );
        ChartPanel pieChartPanel = new ChartPanel(pieChart);
        chartsPanel.add(pieChartPanel);

        chartsPanel.revalidate();
        chartsPanel.repaint();
    }
}
