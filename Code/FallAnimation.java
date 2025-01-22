package Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class FallAnimation {
    private Image[] fallImages;
    private int width;
    private int height;
    private Timer animationTimer;
    private boolean isPlaying = false;
    private int currentFrame = 0;
    private static final int FRAME_DELAY = 100;
    private static final int TOTAL_FRAMES = 10;

    public FallAnimation(int width, int height) {
        this.width = width;
        this.height = height;
        loadFallImages();
    }

    private void loadFallImages() {
        fallImages = new Image[TOTAL_FRAMES];
        for (int i = 0; i < TOTAL_FRAMES; i++) {
            try {
                Image fallSequence = new ImageIcon(getClass().getResource("fall" + (i + 1) + ".png")).getImage();
                fallImages[i] = fallSequence.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            } catch (NullPointerException nullPointerException) {
                System.err.println("Image resource not found: fall" + (i + 1) + ".png");
            } catch (IllegalArgumentException illegalArgumentException) {
                System.err.println("Invalid scaling arguments for image: fall" + (i + 1) + ".png");
            }
        }
    }

    public void startAnimation(ActionListener isComplete) {
        if (!isPlaying) {
            isPlaying = true;
            currentFrame = 0;
            animationTimer = new Timer(FRAME_DELAY, e -> {
                currentFrame++;
                if (currentFrame >= TOTAL_FRAMES) {
                    stopAnimation();
                    isComplete.actionPerformed(null);
                }
            });
            animationTimer.start();
        }
    }
    
    public boolean isPlaying() {
        return isPlaying;
    }

    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        isPlaying = false;
        currentFrame = 0;
    }

    public void drawAnimation(Graphics2D graphics, int x, int y) {
        if (isPlaying && currentFrame < TOTAL_FRAMES && fallImages[currentFrame] != null) {
        	graphics.drawImage(fallImages[currentFrame], x - width / 2, y, width, height, null);
        }
    }


    public void reset() {
        stopAnimation();
        currentFrame = 0;
    }
}
