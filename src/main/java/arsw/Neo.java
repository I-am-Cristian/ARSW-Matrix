package main.java.arsw;

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
            Position targetPhone = board.getClosestPhone(currentPos);

            if (targetPhone == null) {
                System.out.println("❌ No hay teléfonos disponibles");
                break;
            }

            Position nextMove = board.getNextStepTowards(currentPos, targetPhone);
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

    private Position getNextMove(Position current, Position target) {
        int dx = Integer.compare(target.x, current.x);
        int dy = Integer.compare(target.y, current.y);

        if (dx != 0) {
            Position newPos = new Position(current.x + dx, current.y);
            if (board.isValidMoveForNeo(newPos)) {
                return newPos;
            }
        }

        if (dy != 0) {
            Position newPos = new Position(current.x, current.y + dy);
            if (board.isValidMoveForNeo(newPos)) {
                return newPos;
            }
        }

        Position[] alternatives = {
            new Position(current.x + 1, current.y),
            new Position(current.x - 1, current.y),
            new Position(current.x, current.y + 1),
            new Position(current.x, current.y - 1)
        };

        for (Position alt : alternatives) {
            if (board.isValidMoveForNeo(alt)) {
                return alt;
            }
        }

        return null;
    }

    private void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
