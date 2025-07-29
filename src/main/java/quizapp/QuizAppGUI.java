package quizapp;

import javax.swing.*;
import java.awt.CardLayout;

public class QuizAppGUI extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private CategoryPanel categoryPanel;
    private QuizPanel quizPanel;
    private ResultPanel resultPanel;
    private StatsDashboardPanel statsDashboardPanel;

    private QuizProgress quizProgress;
    private UserStatsManager userStatsManager;
    private String currentUser;

    public QuizAppGUI() {
        setTitle("Quiz Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        quizProgress = new QuizProgress();
        userStatsManager = new UserStatsManager();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        categoryPanel = new CategoryPanel(this);
        quizPanel = new QuizPanel(this);
        resultPanel = new ResultPanel(this);
        statsDashboardPanel = new StatsDashboardPanel(this);

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registerPanel, "REGISTER");
        mainPanel.add(categoryPanel, "CATEGORY");
        mainPanel.add(quizPanel, "QUIZ");
        mainPanel.add(resultPanel, "RESULT");
        mainPanel.add(statsDashboardPanel, "STATS");

        add(mainPanel);

        showLogin();
    }

    public void showLogin() {
        cardLayout.show(mainPanel, "LOGIN");
    }

    public void showRegister() {
        cardLayout.show(mainPanel, "REGISTER");
    }

    public void showCategory() {
        categoryPanel.loadCategories();
        cardLayout.show(mainPanel, "CATEGORY");
    }

    public void showQuiz() {
        quizPanel.loadQuestion();
        cardLayout.show(mainPanel, "QUIZ");
    }

    public void showResult() {
        resultPanel.displayResults();
        cardLayout.show(mainPanel, "RESULT");
    }

    public void showStatsDashboard() {
        statsDashboardPanel.refreshData();
        cardLayout.show(mainPanel, "STATS");
    }

    public QuizProgress getQuizProgress() {
        return quizProgress;
    }

    public UserStatsManager getUserStatsManager() {
        return userStatsManager;
    }

    public void setCurrentUser(String user) {
        this.currentUser = user;
        quizProgress.loadProgress(user); // Load quiz progress on login
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuizAppGUI gui = new QuizAppGUI();
            gui.setVisible(true);
        });
    }
}
