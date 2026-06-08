package main.java.arsw;

public class GameState {
    private volatile boolean gameRunning = true;
    private volatile boolean neoWon = false;

    public synchronized boolean isGameRunning() {
        return gameRunning;
    }

    public synchronized void stopGame() {
        gameRunning = false;
    }

    public synchronized void setNeoWon(boolean won) {
        neoWon = won;
    }

    public synchronized boolean hasNeoWon() {
        return neoWon;
    }
}
