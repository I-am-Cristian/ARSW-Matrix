package arsw;

public class Agent implements Runnable {
    private final Board board;
    private final GameState gameState;
    private final int agentId;
    private Position currentPos;

    public Agent(Board board, GameState gameState, Position startPos, int agentId) {
        this.board = board;
        this.gameState = gameState;
        this.currentPos = startPos;
        this.agentId = agentId;
    }

    @Override
    public void run() {
        System.out.println("👤 Agente " + agentId + " comenzó en " + currentPos);

        while (gameState.isGameRunning()) {
            Position neoPos = board.getNeoPosition();
            Position nextMove = board.getNextStepTowards(currentPos, neoPos, false);

            if (nextMove != null && board.moveAgent(agentId, nextMove)) {
                currentPos = nextMove;
            }
            sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
