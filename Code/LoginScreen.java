package Project;

import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


public class LoginScreen extends JFrame {
    private static final long serialVersionUID = 1L;
    private JFrame frame;
    private Map<String, String> userMap;
    public Clip gameOST;

    public LoginScreen(Map<String, String> userMap) {
        this.userMap = userMap;
        startLogin();
    }

    private void startLogin() {
        frame = new JFrame("WELCOME TO HANG-ON");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(null);
        frame.getContentPane().setBackground(Color.BLACK);
        loadSound();
        playMusic();
        JLabel gameNameLabel = new JLabel();
        
        try {
            ImageIcon imageIcon = new ImageIcon(getClass().getResource("hang_on.png"));
            if (imageIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image image = imageIcon.getImage();
                Image scaledImage = image.getScaledInstance(600, 150, Image.SCALE_SMOOTH);
                gameNameLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                throw new IllegalArgumentException("Image could not be loaded from resources");
            }
        } catch (IllegalArgumentException | NullPointerException ex) {
            try {
                gameNameLabel.setIcon(new ImageIcon("hang_on.png"));
            } catch (IllegalArgumentException imageException) {
                gameNameLabel.setText("Hang-On");
                gameNameLabel.setForeground(Color.WHITE);
                System.err.println("Error loading image: " + imageException.getMessage());
            }
        }

        gameNameLabel.setBounds(100, 50, 600, 200);
        frame.add(gameNameLabel);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(350, 300, 100, 40);
        loginButton.setBackground(Color.BLUE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        frame.add(loginButton);

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setBounds(350, 360, 100, 40);
        signUpButton.setBackground(Color.GREEN);
        signUpButton.setForeground(Color.BLACK);
        signUpButton.setFocusPainted(false);
        frame.add(signUpButton);

        JButton scoreboardButton = new JButton("Scoreboard");
        scoreboardButton.setBounds(350, 420, 100, 40);
        scoreboardButton.setBackground(Color.YELLOW);
        scoreboardButton.setForeground(Color.BLACK);
        scoreboardButton.setFocusPainted(false);
        scoreboardButton.addActionListener(e -> openScoreboard());
        frame.add(scoreboardButton);

        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(350, 480, 100, 40);
        exitButton.setBackground(Color.RED);
        exitButton.setForeground(Color.BLACK);
        exitButton.setFocusPainted(false);
        frame.add(exitButton);

        loginButton.addActionListener(e -> login());
        signUpButton.addActionListener(e -> signUp());
        exitButton.addActionListener(e -> exit());

        playMusic();
    }


    private void loadSound() {
        try {
            InputStream gameOSTStream = getClass().getResourceAsStream("gameOST.wav");
            if (gameOSTStream == null) {
                throw new IOException("Sound file not found: gameOST.wav");
            }
            AudioInputStream gameAudioStream = AudioSystem.getAudioInputStream(gameOSTStream);
             gameOST = AudioSystem.getClip();
            gameOST.open(gameAudioStream);
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void playMusic() {
        if (gameOST == null || !gameOST.isRunning()) {
            loadSound(); 
            if (gameOST != null) {
                gameOST.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }


    private void openScoreboard() {
        SwingUtilities.invokeLater(() -> ScoreboardManager.showScoreboard());
    }

    private void login() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username cannot be empty!");
                return;
            }

            if (!UserReader.userExists(username)) {
                JOptionPane.showMessageDialog(frame, "Player not found!");
                return;
            }

            if (UserReader.userCheck(username, password)) {
                UserReader.setCurrentUser(username);
                JOptionPane.showMessageDialog(frame, "Login successful! Starting the game...");
                startGame();
            } else {
                JOptionPane.showMessageDialog(frame, "Incorrect password!");
            }
        }
    }

    private void signUp() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Sign Up",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username cannot be empty!");
                return;
            }

            if (UserReader.userExists(username)) {
                JOptionPane.showMessageDialog(frame, "Player with the same name already exists!");
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(frame, "Passwords do not match!");
                return;
            }

            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Password cannot be empty!");
                return;
            }

            User newUser = new User(username, password);
            UserReader.writeUser(newUser.getName(), newUser.getPassword());
            userMap.put(username, password);
            JOptionPane.showMessageDialog(frame, "Sign-up successful! You can now log in.");
        }
    }

    private void exit() {
        int response = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit?",
                "Exit Confirmation", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            if ( gameOST != null && gameOST.isRunning()) {
            	gameOST.stop();
            }
            System.exit(0);
        }
    }

    private void startGame() {
        SwingUtilities.invokeLater(() -> {
            if (gameOST != null && gameOST.isRunning()) {
                gameOST.stop();
            }
            frame.dispose();
            
            JFrame gameFrame = new JFrame("HANG-ON RACE MODE");
            gameFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            
            GameScreen gameScreen = new GameScreen(null);
            gameFrame.add(gameScreen);
            
            gameFrame.pack();
            gameFrame.setLocationRelativeTo(null);
            gameFrame.setVisible(true);
            gameScreen.requestFocus();
        });
    }

    public void show() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static LoginScreen createUsers() {
        Map<String, String> users = UserReader.readUsers();
        return new LoginScreen(users);
    }
}
