package quizapp;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;

public class QuizPanel extends JPanel {

    private QuizAppGUI mainGUI;

    private JLabel questionLabel = new JLabel("", SwingConstants.CENTER);
    private JPanel optionsPanel = new JPanel();
    private ButtonGroup optionsGroup = new ButtonGroup();

    private JButton submitButton = new JButton("Submit");
    private JButton saveExitButton = new JButton("Save & Exit");
    private JLabel timerLabel = new JLabel("Time left: 15", SwingConstants.CENTER);
    private JLabel explanationLabel = new JLabel(" ", SwingConstants.CENTER);

    private Timer countdownTimer;
    private int timeLeft;

    private static final int TIME_LIMIT = 15; // seconds

    public QuizPanel(QuizAppGUI gui) {
        this.mainGUI = gui;

        // Match LoginPanel style:
        setBackground(new Color(40, 44, 52));
        setLayout(new BorderLayout(10, 10));

        questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        questionLabel.setForeground(new Color(200, 200, 200));
        questionLabel.setPreferredSize(new Dimension(750, 90));
        add(questionLabel, BorderLayout.NORTH);

        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(new Color(40, 44, 52));
        add(optionsPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(40, 44, 52));

        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timerLabel.setForeground(new Color(255, 150, 0));
        bottomPanel.add(timerLabel, BorderLayout.NORTH);

        explanationLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        explanationLabel.setForeground(new Color(67, 181, 129));
        bottomPanel.add(explanationLabel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(new Color(40, 44, 52));
        styleButton(submitButton, new Color(67, 181, 129));
        styleButton(saveExitButton, new Color(29, 155, 240));
        buttonsPanel.add(submitButton);
        buttonsPanel.add(saveExitButton);

        bottomPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(e -> submitAnswer());
        saveExitButton.addActionListener(e -> saveAndExit());
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    public void loadQuestion() {
        optionsGroup.clearSelection();
        optionsPanel.removeAll();
        explanationLabel.setText(" ");

        Question currentQ = mainGUI.getQuizProgress().getCurrentQuestion();
        if (currentQ == null) {
            mainGUI.showResult();
            return;
        }

        currentQ.shuffleOptions();

        questionLabel.setText("<html><body style='width: 700px'>" + currentQ.getQuestionText() + "</body></html>");

        for (int i = 0; i < currentQ.getOptions().size(); i++) {
            JRadioButton rb = new JRadioButton(currentQ.getOptions().get(i));
            rb.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            rb.setForeground(Color.WHITE);
            rb.setBackground(new Color(40, 44, 52));
            rb.setActionCommand(String.valueOf(i + 1));
            optionsGroup.add(rb);
            optionsPanel.add(rb);
        }
        optionsPanel.revalidate();
        optionsPanel.repaint();

        // Reset and start timer
        timeLeft = TIME_LIMIT;
        timerLabel.setText("Time left: " + timeLeft + " seconds");

        if (countdownTimer != null) countdownTimer.stop();
        countdownTimer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Time left: " + timeLeft + " seconds");
            if (timeLeft <= 0) {
                countdownTimer.stop();
                submitAnswer(); // auto submit
            }
        });
        countdownTimer.start();

        submitButton.setEnabled(true);
        for (Component comp : optionsPanel.getComponents()) {
            comp.setEnabled(true);
        }
    }

    private void submitAnswer() {
        if (countdownTimer != null) countdownTimer.stop();

        Question currentQ = mainGUI.getQuizProgress().getCurrentQuestion();

        if (currentQ == null) {
            mainGUI.showResult();
            return;
        }

        Enumeration<AbstractButton> buttons = optionsGroup.getElements();
        int selected = -1;
        while (buttons.hasMoreElements()) {
            AbstractButton btn = buttons.nextElement();
            if (btn.isSelected()) {
                selected = Integer.parseInt(btn.getActionCommand());
                break;
            }
        }

        if (selected == -1) {
            // No selection, count as wrong
            mainGUI.getQuizProgress().answerCurrentQuestion(-1);
            explanationLabel.setText("No answer selected. Correct answer: " + currentQ.getOptions().get(currentQ.getCorrectOption() - 1));
        } else {
            boolean correct = mainGUI.getQuizProgress().answerCurrentQuestion(selected);
            if (correct) {
                explanationLabel.setText("Correct!");
            } else {
                explanationLabel.setText("<html>Wrong! Correct answer: " + currentQ.getOptions().get(currentQ.getCorrectOption() - 1) +
                        "<br>" + currentQ.getExplanation() + "</html>");
            }
        }

        // Disable options and buttons to prevent resubmission
        for (Component comp : optionsPanel.getComponents()) {
            comp.setEnabled(false);
        }
        submitButton.setEnabled(false);

        // After 2 seconds, load next question or show result
        Timer delay = new Timer(2000, e -> {
            submitButton.setEnabled(true);
            mainGUI.getQuizProgress().nextQuestion();
            loadQuestion();
        });
        delay.setRepeats(false);
        delay.start();
    }

    private void saveAndExit() {
        if (countdownTimer != null) countdownTimer.stop();
        mainGUI.getQuizProgress().saveProgress(mainGUI.getCurrentUser());
        JOptionPane.showMessageDialog(this, "Progress saved. You can resume next time.");
        mainGUI.showCategory();
    }
}
