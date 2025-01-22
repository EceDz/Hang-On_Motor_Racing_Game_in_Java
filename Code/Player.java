package Project;

import javax.swing.*;
import java.awt.*;

public class Player extends Motorbike {
    private Image playerImage;
    private Image playerLeft;
    private Image playerRight;
    private int playerX;
    private boolean isPressingLeft;
    private boolean isPressingRight;
    private static final int PLAYER_HEIGHT = 150;
    private static final int PLAYER_WIDTH = 120;
    private FallAnimation fallAnimation;

    public Player() {
        this.playerX = 400;
        this.isPressingLeft = false;
        this.isPressingRight = false;
        this.fallAnimation = new FallAnimation(PLAYER_WIDTH, PLAYER_HEIGHT);
        loadImages();
    }

    @Override
    public void loadImages() {
        try {
            Image straightPlayer = loadImage("player.png");
            Image leftPlayer = loadImage("playerLeft.png");
            Image rightPlayer = loadImage("playerRight.png");

            playerImage = scaleImage(straightPlayer);
            playerLeft = scaleImage(leftPlayer);
            playerRight = scaleImage(rightPlayer);
        } catch (NullPointerException e) {
            System.err.println("Resource not found: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
    }

    private Image loadImage(String pngName) throws NullPointerException, IllegalArgumentException {
        ImageIcon icon = new ImageIcon(getClass().getResource(pngName));
        if (icon.getImage() == null) {
            throw new NullPointerException("Image " + pngName + " is missing or could not be loaded.");
        }
        return icon.getImage();
    }

    private Image scaleImage(Image originalSize) {
        return originalSize.getScaledInstance(PLAYER_WIDTH, PLAYER_HEIGHT, Image.SCALE_SMOOTH);
    }

    public void moveLeft(RoadManager roadManager, int screenWidth) {
        if (isPressingLeft) {
            int oldX = playerX;
            playerX -= 10;
            if (isOutOfBounds(roadManager, screenWidth)) {
                playerX = oldX;
            }
        }
    }

    public void moveRight(RoadManager roadManager, int screenWidth) {
        if (isPressingRight) {
            int oldX = playerX;
            playerX += 10;
            if (isOutOfBounds(roadManager, screenWidth)) {
                playerX = oldX;
            }
        }
    }

    public void setLeftPress(boolean pressing) {
        this.isPressingLeft = pressing;
    }

    public void setRightPress(boolean pressing) {
        this.isPressingRight = pressing;
    }

    public boolean isOutOfBounds(RoadManager roadManager, int width) {
        int leftBoundary = roadManager.getLeftOffset(width);
        int rightBoundary =roadManager .getRightOffset(width);

        int centerX = width / 2;
        int roadCenterX = (leftBoundary + rightBoundary) / 2;

        boolean outOfBounds = playerX < leftBoundary || playerX > rightBoundary;
        boolean notFollowingCurve = (Math.abs(roadCenterX - centerX) > 175) &&
                (Math.abs(playerX - roadCenterX) > 150);

        return (outOfBounds || notFollowingCurve);
    }

    @Override
    public void draw(Graphics2D g2d, int screenHeight) {
        if (!fallAnimation.isPlaying()) {
            Image currentImage = playerImage;
            if (isPressingLeft) currentImage = playerLeft;
            else if (isPressingRight) currentImage = playerRight;

            if (currentImage != null) {
                g2d.drawImage(currentImage,
                        playerX - PLAYER_WIDTH / 2,
                        screenHeight - PLAYER_HEIGHT - 70,
                        PLAYER_WIDTH,
                        PLAYER_HEIGHT,
                        null);
            }
        }
    }

    public void drawFallAnimation(Graphics2D g2d, int height) {
        fallAnimation.drawAnimation(g2d, playerX, height - PLAYER_HEIGHT - 70);
    }

    public FallAnimation getFallAnimation() {
        return fallAnimation;
    }

    public void setPlayerX(int x) {
        this.playerX = x;
    }

    public int getPlayerX() {
        return playerX;
    }

    public void reset() {
        playerX = 400;
        isPressingLeft = false;
        isPressingRight = false;
        fallAnimation.reset();
    }
}
