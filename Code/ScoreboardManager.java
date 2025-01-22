package Project;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ScoreboardManager {
    private static final String SCORES_FILE_PATH = "src/Project/scoreboard.txt";

    private static class ScoreEntry implements Comparable<ScoreEntry> {
        private final String name;
        private final int score;
        private final long timestamp;

        public ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
            this.timestamp = System.currentTimeMillis();
        }

        public ScoreEntry(String name, int score, long timestamp) {
            this.name = name;
            this.score = score;
            this.timestamp = timestamp;
        }

        @Override
        public int compareTo(ScoreEntry other) {
            return Integer.compare(other.score, this.score);
        }
    }

    public static void updateScore(String username, int newScore) {
        ArrayList<ScoreEntry> scores = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORES_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    scores.add(new ScoreEntry(
                        parts[0].trim(), 
                        Integer.parseInt(parts[1].trim()),
                        Long.parseLong(parts[2].trim())
                    ));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading scoreboard: " + e.getMessage());
        }

        scores.add(new ScoreEntry(username, newScore));
        
        Collections.sort(scores);
        try (PrintWriter writer = new PrintWriter(new FileWriter(SCORES_FILE_PATH))) {
            for (ScoreEntry entry : scores) {
                writer.println(entry.name + ":" + entry.score + ":" + entry.timestamp);
            }
        } catch (IOException e) {
            System.out.println("Error saving scoreboard: " + e.getMessage());
        }
    }

    public static void showScoreboard() {
        ArrayList<ScoreEntry> scores = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORES_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    scores.add(new ScoreEntry(
                        parts[0].trim(), 
                        Integer.parseInt(parts[1].trim()),
                        Long.parseLong(parts[2].trim())
                    ));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading scoreboard: " + e.getMessage());
            return;
        }

        Collections.sort(scores);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("==SCOREBOARD==");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");

        for (int i = 0; i < scores.size(); i++) {
            ScoreEntry entry = scores.get(i);
            String dateStr = dateFormat.format(new Date(entry.timestamp));
            JLabel scoreLabel = new JLabel(String.format("%d. %s: %d points (%s)", 
                i + 1, entry.name, entry.score, dateStr));
            scoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            scoreLabel.setForeground(Color.WHITE);
            scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(scoreLabel);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JFrame frame = new JFrame("Scoreboard");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(Color.BLACK);
        frame.add(scrollPane);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}