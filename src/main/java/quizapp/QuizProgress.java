package quizapp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles quiz questions, user answers, save/load functionality, and exporting.
 */
public class QuizProgress implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Question> questions;
    private List<Integer> userAnswers; // 1-based index of selected option; -1 = unanswered
    private int currentIndex; // 0-based index
    private String currentCategory;

    public QuizProgress() {
        resetProgress();
    }

    // Start a new quiz for the given category
    public void startNewQuiz(String category) {
        currentCategory = category;
        questions = Utils.loadQuestionsFromFile("src/main/resources/questions/" + category + ".txt");
        userAnswers = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            userAnswers.add(-1); // mark all as unanswered
        }
        currentIndex = 0;
    }

    // Get the current question
    public Question getCurrentQuestion() {
        if (currentIndex < questions.size()) {
            return questions.get(currentIndex);
        }
        return null;
    }

    // Answer the current question
    public boolean answerCurrentQuestion(int selectedOption) {
        Question q = getCurrentQuestion();
        if (q == null) return false;
        userAnswers.set(currentIndex, selectedOption);
        return selectedOption == q.getCorrectOption();
    }

    // Move to next question
    public void nextQuestion() {
        currentIndex++;
    }

    // Count correct answers
    public int getCorrectCount() {
        int correct = 0;
        for (int i = 0; i < userAnswers.size(); i++) {
            if (userAnswers.get(i) == questions.get(i).getCorrectOption()) {
                correct++;
            }
        }
        return correct;
    }

    public int getTotalQuestions() {
        return questions.size();
    }

    public String getCurrentCategory() {
        return currentCategory;
    }

    // Save progress to disk
    public void saveProgress(String username) {
        if (username == null || username.isBlank()) return;

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("progress_" + username + ".dat"))) {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load progress from disk and jump to first unanswered question
    public void loadProgress(String username) {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("progress_" + username + ".dat"))) {

            QuizProgress loaded = (QuizProgress) ois.readObject();
            this.questions = loaded.questions;
            this.userAnswers = loaded.userAnswers;
            this.currentCategory = loaded.currentCategory;

            // Jump to first unanswered question
            this.currentIndex = 0;
            while (currentIndex < userAnswers.size() && userAnswers.get(currentIndex) != -1) {
                currentIndex++;
            }

        } catch (Exception e) {
            resetProgress();
        }
    }

    // Clear progress and delete file
    public void clearProgress(String username) {
        File f = new File("progress_" + username + ".dat");
        if (f.exists()) f.delete();
        resetProgress();
    }

    private void resetProgress() {
        questions = new ArrayList<>();
        userAnswers = new ArrayList<>();
        currentIndex = 0;
        currentCategory = null;
    }

    // Export results to CSV
    public boolean exportResultsToCSV(String username) {
        try (PrintWriter pw = new PrintWriter(new File("quiz_results_" + username + ".csv"))) {
            pw.println("Question,Your Answer,Correct Answer,Result,Explanation");

            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                int ua = userAnswers.get(i);

                String yourAnswer = (ua == -1) ? "No Answer" : q.getOptions().get(ua - 1);
                String correctAnswer = q.getOptions().get(q.getCorrectOption() - 1);
                String result = (ua == q.getCorrectOption()) ? "Correct" : "Incorrect";

                pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        q.getQuestionText().replace("\"", "\"\""),
                        yourAnswer.replace("\"", "\"\""),
                        correctAnswer.replace("\"", "\"\""),
                        result,
                        q.getExplanation().replace("\"", "\"\""));
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
