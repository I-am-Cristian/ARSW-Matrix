package arsw;

public class Neo implements Runnable {
    private final Board board;
    private final GameState gameState;

    public Neo(Board board, GameState gameState) {
        this.board = board;
        this.gameState = gameState;
    }

    @Override
    public void run() {
        System.out.println("🟢 Neo inició en " + board.getNeoPosition());

        while (gameState.isGameRunning()) {
            Position currentPos = board.getNeoPosition();
            Position targetPhone = board.getClosestReachablePhone(currentPos);

            if (targetPhone == null) {
                System.out.println("❌ No hay teléfonos alcanzables");
                break;
            }

            Position nextMove = board.getNextStepTowards(currentPos, targetPhone, true);
            if (nextMove != null && board.moveNeo(nextMove)) {
                board.display();
                sleep();
            } else {
                System.out.println("⚠️ Neo no puede moverse, esperando...");
                sleep();
            }
        }

        if (!gameState.isGameRunning()) {
            if (gameState.hasNeoWon()) {
                System.out.println("\n🏆 ¡VICTORIA! Neo escapó de la Matrix 🏆");
            } else {
                System.out.println("\n💀 DERROTA - Neo fue capturado 💀");
            }
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
