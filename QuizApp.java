import java.util.*;

public class QuizApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Question> questions = new ArrayList<>();

        // Sample Questions
        questions.add(new Question(
            "What is the capital of France?",
            new String[]{"Berlin", "London", "Paris", "Rome"},
            3
        ));
        questions.add(new Question(
            "Which language is used for Android development?",
            new String[]{"Swift", "Kotlin", "Python", "C#"},
            2
        ));
        questions.add(new Question(
            "What does JVM stand for?",
            new String[]{"Java Virtual Machine", "Java Variable Model", "Joint Venture Mechanism", "Java Visual Model"},
            1
        ));

        int score = 0;

        System.out.println("Welcome to the Online Quiz!\n");

        for (int i = 0; i < questions.size(); i++) {
            System.out.println("Q" + (i + 1) + ":");
            questions.get(i).display();

            int userAnswer = 0;
            while (true) {
                System.out.print("Your answer (1-4): ");
                try {
                    userAnswer = Integer.parseInt(scanner.nextLine());
                    if (userAnswer >= 1 && userAnswer <= 4) break;
                } catch (NumberFormatException e) {
                    // ignore and prompt again
                }
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
            }

            if (questions.get(i).isCorrect(userAnswer)) {
                System.out.println("Correct!\n");
                score++;
            } else {
                System.out.println("Incorrect.\n");
            }
        }

        System.out.println("Quiz Finished!");
        System.out.println("Your Score: " + score + "/" + questions.size());
    }
}
