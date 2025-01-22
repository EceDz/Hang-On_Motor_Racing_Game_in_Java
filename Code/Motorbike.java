package Project;

import java.awt.*;

public abstract class Motorbike {
    protected Image bikeImage;
    protected static final int BASE_HEIGHT = 150;
    protected static final int BASE_WIDTH = 120;

    public Image getBikeImage() {
        return bikeImage;
    }

    public void setBikeImage(Image bikeImage) {
        this.bikeImage = bikeImage;
    }

    public static int getBaseHeight() {
        return BASE_HEIGHT;
    }

    public static int getBaseWidth() {
        return BASE_WIDTH;
    }

    public abstract void loadImages();
    public abstract void draw(Graphics2D g2d, int screenHeight);
}