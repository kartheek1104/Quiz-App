package quizapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Question implements Serializable {

    private String questionText;
    private List<String> options;
    private int correctOption; // 1-based index
    private String explanation;

    public Question(String questionText, List<String> options, int correctOption, String explanation) {
        this.questionText = questionText;
        this.options = new ArrayList<>(options);
        this.correctOption = correctOption;
        this.explanation = explanation == null ? "" : explanation;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectOption() {
        return correctOption;
    }

    public String getExplanation() {
        return explanation;
    }

    // Shuffle options but keep correct answer tracking
    public void shuffleOptions() {
        String correctAnswer = options.get(correctOption - 1);
        Collections.shuffle(options);
        correctOption = options.indexOf(correctAnswer) + 1;
    }
}
