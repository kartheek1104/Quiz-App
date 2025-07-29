package quizapp;

import java.io.*;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserStatsManager {

    private static final String USERS_FILE = "users.txt";
    private static final String STATS_FILE = "stats.dat";

    private Map<String, String> users = new HashMap<>();
    private Map<String, Stats> userStats = new HashMap<>();
    private Map<String, List<QuizAttempt>> userQuizHistory = new HashMap<>();

    // Removed unused currentUser and sdf fields

    public UserStatsManager() {
        loadUsers();
        loadStats();
    }

    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUsers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (var entry : users.entrySet()) {
                bw.write(entry.getKey() + ":" + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean registerUser(String username, String password) {
        if (users.containsKey(username)) return false;
        users.put(username, hashPassword(password));
        saveUsers();
        return true;
    }

    public boolean authenticateUser(String username, String password) {
        String storedHash = users.get(username);
        if (storedHash == null) return false;
        return storedHash.equals(hashPassword(password));
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public void setCurrentUser(String username) {
        // Removed unused field - no need to store currentUser here
    }

    @SuppressWarnings("unchecked")
    private void loadStats() {
        File f = new File(STATS_FILE);
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            userStats = (Map<String, Stats>) ois.readObject();
            userQuizHistory = (Map<String, List<QuizAttempt>>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveStats() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STATS_FILE))) {
            oos.writeObject(userStats);
            oos.writeObject(userQuizHistory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateStats(String username, int correctAnswers, int totalQuestions) {
        if (username == null) return;
        Stats stats = userStats.getOrDefault(username, new Stats());
        stats.totalQuizzesTaken++;
        stats.totalCorrectAnswers += correctAnswers;
        stats.totalQuestionsAttempted += totalQuestions;
        userStats.put(username, stats);

        // Add quiz attempt to history
        List<QuizAttempt> history = userQuizHistory.getOrDefault(username, new ArrayList<>());
        history.add(new QuizAttempt(new Date(), correctAnswers, totalQuestions));
        userQuizHistory.put(username, history);

        saveStats();
    }

    public Stats getStats(String username) {
        return userStats.getOrDefault(username, new Stats());
    }

    public List<QuizAttempt> getQuizHistory(String username) {
        return userQuizHistory.getOrDefault(username, Collections.emptyList());
    }

    public static class Stats implements Serializable {
        public int totalQuizzesTaken = 0;
        public int totalCorrectAnswers = 0;
        public int totalQuestionsAttempted = 0;

        public int getTotalQuizzesTaken() {
            return totalQuizzesTaken;
        }

        public int getTotalCorrectAnswers() {
            return totalCorrectAnswers;
        }

        public int getTotalQuestionsAttempted() {
            return totalQuestionsAttempted;
        }
    }

    public static class QuizAttempt implements Serializable {
        private Date date;
        private int correctAnswers;
        private int totalQuestions;

        public QuizAttempt(Date date, int correctAnswers, int totalQuestions) {
            this.date = date;
            this.correctAnswers = correctAnswers;
            this.totalQuestions = totalQuestions;
        }

        public String getDate() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
        }

        public int getCorrectAnswers() {
            return correctAnswers;
        }

        public int getTotalQuestions() {
            return totalQuestions;
        }
    }
}
