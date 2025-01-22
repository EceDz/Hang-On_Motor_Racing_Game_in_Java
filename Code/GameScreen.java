package Project;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GameScreen extends JPanel {
    private static final long serialVersionUID = 1L;
    private String currentUser;
    private Player player;
    private RoadManager roadManager;
    private GameThread gameThread;
    private JButton pauseButton;
    private JButton restartButton;
    private JButton exitButton;
    private boolean gameOver = false;
    private boolean isPaused = false;
    private int score = 0;
    private int speed = 0;
    private int time = 60;
    private Timer gameTimer;
    private Timer animationTimer;
    private Timer scoreTimer;
    private boolean isCountdownActive = false;
    private int countdownNumber = 3;
    private long countdownStartTime;
    private boolean isTimeOver = false;
    private Clip motorbikeSound;
    private Clip crashSound;
    private Clip beep;
    private ArrayList<Bot> bots;
    private static final int NUM_BOTS = 7;    
    
    public GameScreen(JFrame loginFrame) {
        this.currentUser = UserReader.getCurrentUser();
        setPreferredSize(new Dimension(800, 600));
        gameStart();
        loadSounds();
        setButtons();
        setTimers();
        setControls();
        setFocusable(true);
        
        SwingUtilities.invokeLater(() -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                JFrame frame = (JFrame) window;
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        returnToLoginScreen();
                    }
                });
            }
        });
    }
    
    public RoadManager getRoadManager() {
        return roadManager;
    }

    public int getSpeed() {
        return speed;
    }

    private void gameStart() {
        player = new Player();
        roadManager = new RoadManager();
        gameThread = new GameThread(this);
        speed = 0;
        
        isCountdownActive = true;
        countdownNumber = 3;
        countdownStartTime = System.currentTimeMillis();
        
        int width = 800;
        bots = new ArrayList<>();
        Random random = new Random();
        
        Set<Point> occupiedPositions = new HashSet<>();
        
        int centerX = width / 2;
        int playerY = 400;
        int exclusionRadius = 50;
        
        for (int x = centerX - exclusionRadius; x <= centerX + exclusionRadius; x += exclusionRadius) {
            for (int y = playerY - exclusionRadius; y <= playerY + exclusionRadius; y += exclusionRadius) {
                occupiedPositions.add(new Point(x, y));
            }
        }
        
        int roadLeft = roadManager.getLeftOffset(width);
        int roadRight = roadManager.getRightOffset(width);
        int roadWidth = roadRight - roadLeft;
        
        int sectionsX = 3;
        int sectionsY = 3;
        int sectionWidth = roadWidth / sectionsX;
        int sectionHeight = 100 / sectionsY;
        
        for (int i = 0; i < NUM_BOTS; i++) {
            Bot bot = new Bot(width);
            Point position;
            int attempts = 0;
            final int MAX_ATTEMPTS = 100;
            
            do {
                double perspective = 0.2 + (random.nextDouble() * 0.6);
                int sectionX = random.nextInt(sectionsX);
                int sectionY = random.nextInt(sectionsY);
                
                int minX = roadLeft + (sectionWidth * sectionX);
                int maxX = minX + sectionWidth;
                
                minX = (int)(minX + (50 * perspective));
                maxX = (int)(maxX - (50 * perspective));
                
                int x = minX + random.nextInt(maxX - minX);
                int y = 350 + (sectionY * sectionHeight) + random.nextInt(sectionHeight);
                
                position = new Point(x, y);
                
                boolean tooClose = false;
                for (Point occupied : occupiedPositions) {
                    if (position.distance(occupied) < exclusionRadius) {
                        tooClose = true;
                        break;
                    }
                }
                
                if (!tooClose) {
                    break;
                }
                
                attempts++;
                if (attempts >= MAX_ATTEMPTS) {
                    x = roadLeft + exclusionRadius + random.nextInt(roadWidth - 2 * exclusionRadius);
                    y = 350 + random.nextInt(100);
                    position = new Point(x, y);
                    break;
                }
            } while (true);
            
            for (int x = position.x - exclusionRadius; x <= position.x + exclusionRadius; x += exclusionRadius) {
                for (int y = position.y - exclusionRadius; y <= position.y + exclusionRadius; y += exclusionRadius) {
                    occupiedPositions.add(new Point(x, y));
                }
            }
            
            bot.setBotX(position.x);
            bot.setDistance((position.y - 400) / 5);
            bot.updatePosition(roadManager, 0);
            
            bots.add(bot);
        }
        
        gameThread.start();
    }
    
    private void setButtons() {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        pauseButton = new JButton("Pause");
        restartButton = new JButton("Restart");
        exitButton = new JButton("Exit");

        pauseButton.setBounds(670, 10, 100, 25);
        restartButton.setBounds(670, 40, 100, 25);
        exitButton.setBounds(670, 70, 100, 25);

        pauseButton.setFocusable(false);
        restartButton.setFocusable(false);
        exitButton.setFocusable(false);

        pauseButton.addActionListener(e -> {
        	pause();
            requestFocusInWindow();
        });
        restartButton.addActionListener(e -> {
            resetGame();
            requestFocusInWindow();
        });
        exitButton.addActionListener(e -> {
        ScoreboardManager.updateScore(currentUser, score);
        returnToLoginScreen();
        });

        Color buttonColor = Color.WHITE;
        pauseButton.setBackground(buttonColor);
        restartButton.setBackground(buttonColor);
        exitButton.setBackground(buttonColor);

        add(pauseButton);
        add(restartButton);
        add(exitButton);
    }
    
    private void loadBeep() {
        try {
            InputStream beepStream = getClass().getResourceAsStream("beep.wav");
            if (beepStream == null) {
                throw new IOException("Sound file not found: gameOST.wav");
            }
            AudioInputStream gameAudioStream = AudioSystem.getAudioInputStream(beepStream);
             beep = AudioSystem.getClip();
            beep.open(gameAudioStream);
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void countdownBeep() {
        if (beep == null || !beep.isRunning()) {
            loadBeep();
            if (beep != null) {
                beep.setFramePosition(0); 
                beep.start();
            }
        }
    }

    private void loadSounds() {
        try {
            InputStream motorbikeStream = getClass().getResourceAsStream("motorbikesound.wav");
            if (motorbikeStream == null) {
                throw new IOException("Sound file not found: motorbikesound.wav");
            }
            AudioInputStream motorbikeAudioStream = AudioSystem.getAudioInputStream(motorbikeStream);
            motorbikeSound = AudioSystem.getClip();
            motorbikeSound.open(motorbikeAudioStream);

            InputStream crashStream = getClass().getResourceAsStream("crashsound.wav");
            if (crashStream == null) {
                throw new IOException("Sound file not found: crashsound.wav");
            }
            AudioInputStream crashAudioStream = AudioSystem.getAudioInputStream(crashStream);
            crashSound = AudioSystem.getClip();
            crashSound.open(crashAudioStream);
        } catch (UnsupportedAudioFileException ex) {
            JOptionPane.showMessageDialog(this, "Audio format not supported", "Sound Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Sound file not found or corrupted", "Sound Error", JOptionPane.ERROR_MESSAGE);
        } catch (LineUnavailableException ex) {
            JOptionPane.showMessageDialog(this, "Audio system unavailable", "Sound Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startMotorbikeSound() {
        if (motorbikeSound != null && !motorbikeSound.isRunning()) {
            motorbikeSound.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private void stopMotorbikeSound() {
        if (motorbikeSound != null && motorbikeSound.isRunning()) {
            motorbikeSound.stop();
        }
    }

    private void playCrashSound() {
        if (crashSound != null) {
            crashSound.setFramePosition(0);
            crashSound.start();
        }
    }
    
    private void drawCountdown(Graphics2D graphics) {
        if (!isCountdownActive) return;
        
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Arial", Font.BOLD, 120));
        
        String text;
        if (countdownNumber > 0) {
            text = String.valueOf(countdownNumber);
        } else if (countdownNumber == 0) {
            text = "START!";
        } else {
            return;
        }
        
        FontMetrics metrics = graphics.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(text)) / 2;
        int y = 225;
        
        graphics.drawString(text, x, y);
    }

    private void updateCountdown() {
        if (!isCountdownActive) return;
        
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - countdownStartTime;
        
        countdownNumber = 3 - (int)(elapsedTime / 1000);
        countdownBeep();
        
        if (countdownNumber <= 0) {
            if (countdownNumber == 0) {
                startMotorbikeSound();
            }
            if (elapsedTime > 4000) {
                isCountdownActive = false;
                gameTimer.start();
                time = 60;
            }
        }
    }
    
    private void setTimers() {
        gameTimer = new Timer(1000, e -> {
            if (!isPaused && !gameOver && !isCountdownActive) {
                if (roadManager.checkpointPassed()) {
                    time = 61;
                }
                time--;
                if (time <= 0) {
                    time = 0;
                    if (!isTimeOver) {
                        isTimeOver = true;
                        stopMotorbikeSound();
                        scoreTimer.stop();
                        repaint();
                        endGame();
                    }
                }
            }
        });
        
        scoreTimer = new Timer(100, e -> {
            if (!isPaused && !gameOver && !isCountdownActive && !isTimeOver) {
                score++;
            }
        });
        scoreTimer.start();

        animationTimer = new Timer(16, e -> {
            if (!isPaused) {
                if (isCountdownActive) {
                    updateCountdown();
                } else if (!gameOver && !isTimeOver) {
                    roadManager.update(speed);
                    checkBounds();
                    for (Bot bot : bots) {
                        bot.updatePosition(roadManager, speed);
                    }
                }
                repaint();
            }
        });
        animationTimer.start();
    }
    
    private void pause() {
        isPaused = !isPaused;
        if (isPaused) {
            gameTimer.stop();
            animationTimer.stop();
            scoreTimer.stop();
            gameThread.setPaused(true);
            stopMotorbikeSound();
        } else {
            gameTimer.start();
            animationTimer.start();
            scoreTimer.start();
            gameThread.setPaused(false);
            if (!isTimeOver && !gameOver) {
                startMotorbikeSound();
            }
        }
        pauseButton.setText(isPaused ? "Resume" : "Pause");
    }
    
    private void setControls() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameOver && !isPaused && !isCountdownActive && !isTimeOver) {
                    roadManager.setMoving(true);
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            if (speed > 0) {
                                player.setLeftPress(true);
                                player.moveLeft(roadManager, getWidth());
                                checkBounds();
                            }
                            break;
                        case KeyEvent.VK_RIGHT:
                            if (speed > 0) {
                                player.setRightPress(true);
                                player.moveRight(roadManager, getWidth());
                                checkBounds();
                            }
                            break;
                        case KeyEvent.VK_UP:
                            speed = Math.min(speed + 5, 300);
                            break;
                        case KeyEvent.VK_DOWN:
                            speed = Math.max(speed - 5, 0);
                            break;
                        case KeyEvent.VK_ESCAPE:
                        	pause();
                            break;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) player.setLeftPress(false);
                else if (e.getKeyCode() == KeyEvent.VK_RIGHT) player.setRightPress(false);
            }
        });
    }

    private void checkBounds() {
        if (!gameOver && !player.getFallAnimation().isPlaying() && 
            player.isOutOfBounds(roadManager, getWidth())) {
            endGame();
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics painted) {
        if (painted == null) return;
        super.paintComponent(painted);
        Graphics2D graphics = (Graphics2D) painted.create();
       
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
       
        drawBackground(graphics);
        roadManager.drawRoad(graphics, getWidth(), getHeight(), speed);
       
        if (!gameOver) {
            for (Bot bot : bots) {
                bot.draw(graphics, roadManager, getWidth());
            }
        }
        
        if (gameOver) {
            player.drawFallAnimation(graphics, getHeight());
        } else {
            player.draw(graphics, getHeight());
        }
       
        drawUI(graphics);
        
        if (isPaused) {
            drawPauseOverlay(graphics);
        } else if (isTimeOver) {
            drawTimeOver(graphics);
        } else if (isCountdownActive) {
            drawCountdown(graphics);
        }

        pauseButton.setVisible(true);
        restartButton.setVisible(true);
        exitButton.setVisible(true);
        
        graphics.dispose();
    }

    private void drawBackground(Graphics2D graphics) {
    	graphics.setColor(Color.BLUE);
    	graphics.fillRect(0, 0, getWidth(), getHeight());

    	graphics.setColor(Color.GREEN);
    	graphics.fillRect(0, getHeight()/2, getWidth(), getHeight()/2);

        int centerX = getWidth() / 2;
        float perspectiveShift = (roadManager.getLeftOffset(getWidth()) + 
                                roadManager.getRightOffset(getWidth())) / 2 - centerX;
        
        perspectiveShift *= 0.5f;

        graphics.setColor(Color.GRAY);
        int[] xPoints = {
            (int)(0 + perspectiveShift), 
            (int)(200 + perspectiveShift), 
            (int)(400 + perspectiveShift),
                    (int)(600 + perspectiveShift), 
                    (int)(800 + perspectiveShift)
                };
                int[] yPoints = {
                    getHeight()/2, 
                    getHeight()/2 - 100, 
                    getHeight()/2 - 50,
                    getHeight()/2 - 120, 
                    getHeight()/2
                };
                graphics.fillPolygon(xPoints, yPoints, 5);
            }

            private void drawUI(Graphics2D graphics) {
            	graphics.setColor(Color.WHITE);
            	graphics.setFont(new Font("Arial", Font.BOLD, 20));
               
            	graphics.drawString("SCORE: " + score, 20, 30);
            	graphics.drawString("TIME: " + time, 200, 30);
               
                String speedText = "SPEED: " + speed + " km/h";
                FontMetrics fontMetrics = graphics.getFontMetrics();
                int speedX = getWidth() - 280 - fontMetrics.stringWidth(speedText);
                graphics.drawString(speedText, speedX, 30);
            }

            private void drawPauseOverlay(Graphics2D graphics) {
            	graphics.setColor(new Color(0, 0, 0, 128));
            	graphics.fillRect(0, 0, getWidth(), getHeight());
            	graphics.setColor(Color.WHITE);
            	graphics.setFont(new Font("Arial", Font.BOLD, 40));
                String pauseText = "PAUSED";
                FontMetrics fontMetrics = graphics.getFontMetrics();
                int textX = (getWidth() - fontMetrics.stringWidth(pauseText)) / 2;
                int textY = getHeight() / 2;
                graphics.drawString(pauseText, textX, textY);
            }

    private void drawTimeOver(Graphics2D graphics) {
    	graphics.setColor(new Color(0, 0, 0, 180));
    	graphics.fillRect(0, 0, getWidth(), getHeight());
    	graphics.setColor(Color.RED);
    	graphics.setFont(new Font("Arial", Font.BOLD, 48));
        String gameOverText = "TIME OVER";
        FontMetrics metrics = graphics.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(gameOverText)) / 2;
        int y = getHeight() / 2;
        graphics.drawString(gameOverText, x, y);
    }

    private void endGame() {
        if (isTimeOver) {
            Timer timeOverTimer = new Timer(2000, e -> {
                ((Timer)e.getSource()).stop();
                gameOver = true;
                gameTimer.stop();
                scoreTimer.stop();
                animationTimer.stop();
                playCrashSound();
                stopMotorbikeSound();
                showGameOver();
            });
            timeOverTimer.setRepeats(false);
            timeOverTimer.start();
            return;
        }
        
        gameOver = true;
        gameTimer.stop();
        scoreTimer.stop();
        playCrashSound();
        stopMotorbikeSound();
        player.getFallAnimation().startAnimation(e -> {
            animationTimer.stop();
            showGameOver();
        });
    }

    private void showGameOver() {
        ScoreboardManager.updateScore(currentUser, score);
        int option = JOptionPane.showConfirmDialog(this,
            "Game Over!\nScore: " + score + "\nPlay Again?",
            "Game Over",
            JOptionPane.YES_NO_OPTION);
       
        if (option == JOptionPane.YES_OPTION) {
            resetGame();
        } else {
            returnToLoginScreen();
        }
    }

    private void resetGame() {
        score = 0;
        speed = 0;
        time = 60;
        gameOver = false;
        isPaused = false;
        isTimeOver = false;
        isCountdownActive = true;
        countdownNumber = 3;
        countdownStartTime = System.currentTimeMillis();
        player.reset();
        roadManager.reset();
        
        Random random = new Random();
        Set<Point> occupiedPositions = new HashSet<>();
        occupiedPositions.add(new Point(getWidth()/2, 400));
        
        for (int i = 0; i < bots.size(); i++) {
            Bot bot = bots.get(i);
            Point position;
            do {
                int minX = roadManager.getLeftOffset(getWidth()) + 50;
                int maxX = roadManager.getRightOffset(getWidth()) - 50;
                int x = minX + random.nextInt(maxX - minX);
                int y = 350 + random.nextInt(100);
                position = new Point(x, y);
            } while (occupiedPositions.contains(position));
            
            occupiedPositions.add(position);
            bot.reset();
            bot.setBotX(position.x);
            bot.setDistance((position.y - 400) / 5);
        }
        
        if (gameTimer != null) gameTimer.stop();
        if (animationTimer != null) animationTimer.stop();
        if (scoreTimer != null) scoreTimer.stop();
        
        setTimers();
        pauseButton.setText("Pause");
        requestFocus();
        roadManager.setMoving(true);
    }     
    
    private void returnToLoginScreen() throws NullPointerException {
        stopMotorbikeSound();
        if (gameTimer != null) gameTimer.stop();
        if (animationTimer != null) animationTimer.stop();
        if (scoreTimer != null) scoreTimer.stop();
        SwingUtilities.invokeLater(() -> {
            LoginScreen newLoginScreen = LoginScreen.createUsers();
            newLoginScreen.show();
        });
        Window gameWindow = SwingUtilities.getWindowAncestor(this);
        if (gameWindow != null) {
            gameWindow.dispose();
        }
    }
}