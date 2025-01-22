package Project;

public class GameThread extends Thread {
    private final GameScreen gameScreen;
    private volatile boolean running = true;
    private volatile boolean paused = false;
    private static final int FPS_DELAY = 16;

    public GameThread(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    @Override
    public void run() {
        while (running) {
            if (!paused) {
                gameScreen.getRoadManager().update(gameScreen.getSpeed());

                try {
                    Thread.sleep(FPS_DELAY);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt(); 
                    break;
                }
            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isPaused() {
        return paused;
    }

    public void stopGame() {
        running = false;
        interrupt(); 
    }
}
