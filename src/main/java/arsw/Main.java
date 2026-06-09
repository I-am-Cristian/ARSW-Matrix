package arsw;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println(" THE MATRIX - RELOADED ");
        System.out.println("=".repeat(40));

        System.out.print("Número de filas (mínimo 5): ");
        int rows = scanner.nextInt();

        System.out.print("Número de columnas (mínimo 5): ");
        int cols = scanner.nextInt();

        System.out.print("Número de teléfonos: ");
        int numPhones = scanner.nextInt();

        System.out.print("Número de paredes: ");
        int numWalls = scanner.nextInt();

        System.out.print("Número de agentes: ");
        int numAgents = scanner.nextInt();

        int maxElements = rows * cols;
        if (numPhones + numWalls + numAgents + 1 > maxElements) {
            System.out.println("Demasiados elementos para el tablero de " + rows + "x" + cols);
            System.out.println(" Máximo recomendado: " + (maxElements - 1) + " elementos");
            scanner.close();
            return;
        }

        GameState gameState = new GameState();
        Board board = new Board(rows, cols, numPhones, numWalls, numAgents, gameState);

        System.out.println("\n Configuración inicial:");
        board.display();

        System.out.println("\n Presiona ENTER para comenzar la simulación...");
        scanner.nextLine();
        scanner.nextLine();

        Thread neoThread = new Thread(new Neo(board, gameState));

        List<Thread> agentThreads = new ArrayList<>();
        List<Position> agentPositions = board.getAgentPositions();
        for (int i = 0; i < agentPositions.size(); i++) {
            Agent agent = new Agent(board, gameState, agentPositions.get(i), i);
            agentThreads.add(new Thread(agent));
        }

        System.out.println("\n Iniciando simulación con:");
        System.out.println("   - 1 Neo");
        System.out.println("   - " + agentThreads.size() + " Agentes");
        System.out.println("   - " + numPhones + " Teléfonos");
        System.out.println("   - " + numWalls + " Paredes");
        System.out.println("\n ¡QUE COMIENCE EL JUEGO!\n");

        neoThread.start();
        for (Thread t : agentThreads) {
            t.start();
        }

        try {
            neoThread.join();
            for (Thread t : agentThreads) {
                t.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n JUEGO TERMINADO");
        System.out.println("Gracias por jugar!");

        scanner.close();
    }
}
