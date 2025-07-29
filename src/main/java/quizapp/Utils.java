package quizapp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<Question> loadQuestionsFromFile(String filepath) {
        List<Question> questions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String questionText = line.trim();
                if (questionText.isEmpty()) continue;

                String type = br.readLine();
                if (type == null) break;
                type = type.trim();

                List<String> options = new ArrayList<>();
                int correctOption = 1;
                String explanation = "";

                if ("MCQ".equalsIgnoreCase(type)) {
                    for (int i = 0; i < 4; i++) {
                        String option = br.readLine();
                        if (option == null) break;
                        options.add(option.trim());
                    }
                    String correctStr = br.readLine();
                    if (correctStr != null) {
                        correctOption = Integer.parseInt(correctStr.trim());
                    }
                    explanation = br.readLine();
                } else if ("TRUE_FALSE".equalsIgnoreCase(type)) {
                    options.add("True");
                    options.add("False");
                    String correctStr = br.readLine();
                    if (correctStr != null) {
                        correctOption = Integer.parseInt(correctStr.trim());
                    }
                    explanation = br.readLine();
                } else {
                    // Unknown type skip to next
                    continue;
                }

                if (explanation == null) explanation = "";

                questions.add(new Question(questionText, options, correctOption, explanation));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questions;
    }
}
