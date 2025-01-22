package Project;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Bot extends Motorbike {
    private static final int TOTAL_BOT_IMAGES = 6;
    private static final int BASE_WIDTH = 100;
    private static final int BASE_HEIGHT = 100;

    private Image[] BotImages;
    private Image[] rightBotImages;
    private Image[] leftBotImages;
    private int botX;
    private int distance;
    private int width;

    public Bot(int width) {
        this.width = width;
        this.botX = width / 2;
        this.distance = 0;
        new ArrayList<>();
        BotImages = new Image[TOTAL_BOT_IMAGES];
        rightBotImages = new Image[TOTAL_BOT_IMAGES];
        leftBotImages = new Image[TOTAL_BOT_IMAGES];
        loadImages();
    }
    
    public void setDistance(int distance) {
        this.distance = distance;
        loadImages();
    }

    public int getBotX() {
        return botX;
    }

    public void setBotX(int x) {
        this.botX = x;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public void draw(Graphics2D graphics, int height) {
        int distanceIndex = Math.min(Math.max(distance / 20, 0), TOTAL_BOT_IMAGES - 1);
        Image currentImage = BotImages[distanceIndex];

        int drawX = botX - (currentImage.getWidth(null) / 2);
        int drawY = height - (distance * 2);
        graphics.drawImage(currentImage, drawX, drawY, null);
    }

    public void draw(Graphics2D graphics, RoadManager roadManager, int width) {
        int distanceIndex = Math.min(Math.max(distance / 5, 0), TOTAL_BOT_IMAGES - 1);
        int roadCenterX = roadManager.getLeftOffset(width) +
                          (roadManager.getRightOffset(width) - roadManager.getLeftOffset(width)) / 2;

        Image currentImage;
        RoadSegment currentSegment = roadManager.getCurrentSegment();
        RoadSegment previousSegment = roadManager.getPreviousSegment();

        if (Math.abs(botX - roadCenterX) < 50) {
            currentImage = BotImages[distanceIndex];
        } else if (currentSegment == RoadSegment.RIGHT_CURVE) {
            currentImage = rightBotImages[distanceIndex];
        } else if (currentSegment == RoadSegment.LEFT_CURVE) {
            currentImage = leftBotImages[distanceIndex];
        } else if (currentSegment == RoadSegment.STRAIGHT && previousSegment == RoadSegment.LEFT_CURVE) {
            currentImage = rightBotImages[distanceIndex];
        } else if (currentSegment == RoadSegment.STRAIGHT && previousSegment == RoadSegment.RIGHT_CURVE) {
            currentImage = leftBotImages[distanceIndex];
        } else {
            currentImage = BotImages[distanceIndex];
        }

        int drawX = botX - (currentImage.getWidth(null) / 2);
        int drawY = 400 + (distance * 4);
        graphics.drawImage(currentImage, drawX, drawY, null);
    }

    @Override
    public void loadImages() {
        for (int i = 0; i < TOTAL_BOT_IMAGES; i++) {
            String botStraight= "bot" + (i + 1) + ".png";
            String botRight = "botright" + (i + 1) + ".png";
            String botLeft = "botleft" + (i + 1) + ".png";

            try {
                Image straight = new ImageIcon(getClass().getResource(botStraight)).getImage();
                Image right = new ImageIcon(getClass().getResource(botRight)).getImage();
                Image left = new ImageIcon(getClass().getResource(botLeft)).getImage();

                int scaledHeight = BASE_HEIGHT - ((BASE_HEIGHT - (BASE_HEIGHT / 2)) * distance / 100);
                int scaledWidth = (BASE_WIDTH * scaledHeight) / BASE_HEIGHT;

                BotImages[i] = straight.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                rightBotImages[i] = right.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                leftBotImages[i] = left.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            } catch (NullPointerException e) {
                System.err.println("Image not found: " + botStraight + ", " + botRight + ", or " + botLeft);
            } catch (IllegalArgumentException e) {
                System.err.println("Error scaling image: " + e.getMessage());
            }
        }
        bikeImage = BotImages[0];
    }

    public void updatePosition(RoadManager roadManager, int speed) {
        int roadCenterX = roadManager.getLeftOffset(800) +
                          (roadManager.getRightOffset(800) - roadManager.getLeftOffset(800)) / 2;

        if (Math.abs(botX - roadCenterX) > 5) {
            if (botX < roadCenterX) {
                botX += speed * 0.1;
            } else {
                botX -= speed * 0.1;
            }
        }

        int leftBoundary = roadManager.getLeftOffset(800);
        int rightBoundary = roadManager.getRightOffset(800);
        botX = Math.max(leftBoundary, Math.min(botX, rightBoundary));
    }
    
    public void reset() {
        botX = width / 2;
        distance = 0;
        loadImages();
    }
}