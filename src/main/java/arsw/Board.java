package main.java.arsw;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

public class Board {
    public static final char EMPTY = '.';
    public static final char NEO = 'N';
    public static final char AGENT = 'A';
    public static final char PHONE = 'T';
    public static final char WALL = '#';

    private static final int[][] DIRECTIONS = {
        {1, 0},
        {-1, 0},
        {0, 1},
        {0, -1},
        {1, 1},
        {1, -1},
        {-1, 1},
        {-1, -1}
    };

    private final int rows;
    private final int cols;
    private final char[][] grid;
    private Position neoPos;
    private final List<Phone> phones;
    private final List<Position> agents;
    private final List<Wall> walls;
    private final GameState gameState;

    public Board(int rows, int cols, int numPhones, int numWalls, int numAgents, GameState gameState) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new char[rows][cols];
        this.phones = new ArrayList<>();
        this.agents = new ArrayList<>();
        this.walls = new ArrayList<>();
        this.gameState = gameState;

        initializeBoard(numPhones, numWalls, numAgents);
    }

    private void initializeBoard(int numPhones, int numWalls, int numAgents) {
        for (int i = 0; i < rows; i++) {
            Arrays.fill(grid[i], EMPTY);
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();

        int wallsPlaced = 0;
        while (wallsPlaced < numWalls) {
            int x = random.nextInt(rows);
            int y = random.nextInt(cols);
            if (grid[x][y] == EMPTY) {
                grid[x][y] = WALL;
                walls.add(new Wall(new Position(x, y)));
                wallsPlaced++;
            }
        }

        int phonesPlaced = 0;
        while (phonesPlaced < numPhones) {
            int x = random.nextInt(rows);
            int y = random.nextInt(cols);
            if (grid[x][y] == EMPTY) {
                grid[x][y] = PHONE;
                phones.add(new Phone(new Position(x, y)));
                phonesPlaced++;
            }
        }

        while (true) {
            int x = random.nextInt(rows);
            int y = random.nextInt(cols);
            if (grid[x][y] == EMPTY) {
                neoPos = new Position(x, y);
                grid[x][y] = NEO;
                break;
            }
        }

        int agentsPlaced = 0;
        while (agentsPlaced < numAgents) {
            int x = random.nextInt(rows);
            int y = random.nextInt(cols);
            if (grid[x][y] == EMPTY) {
                grid[x][y] = AGENT;
                agents.add(new Position(x, y));
                agentsPlaced++;
            }
        }
    }

    public synchronized boolean moveNeo(Position newPos) {
        if (!gameState.isGameRunning()) {
            return false;
        }

        if (!isValidMoveForNeo(newPos)) {
            return false;
        }

        grid[neoPos.x][neoPos.y] = EMPTY;
        neoPos = newPos;
        grid[neoPos.x][neoPos.y] = NEO;

        if (isPhone(neoPos)) {
            gameState.setNeoWon(true);
            gameState.stopGame();
            System.out.println("\n✨ ¡NEO LLEGÓ AL TELÉFONO! ✨");
            return true;
        }

        return true;
    }

    public synchronized boolean moveAgent(int agentIndex, Position newPos) {
        if (!gameState.isGameRunning()) {
            return false;
        }

        Position oldPos = agents.get(agentIndex);

        if (!isValidMoveForAgent(newPos)) {
            return false;
        }

        if (newPos.equals(neoPos)) {
            gameState.setNeoWon(false);
            gameState.stopGame();
            System.out.println("\n🔴 ¡AGENTE " + agentIndex + " ATRAPÓ A NEO! 🔴");
            return false;
        }

        grid[oldPos.x][oldPos.y] = EMPTY;
        agents.set(agentIndex, newPos);
        grid[newPos.x][newPos.y] = AGENT;

        return true;
    }

    public synchronized boolean isValidMoveForNeo(Position pos) {
        if (pos.x < 0 || pos.x >= rows || pos.y < 0 || pos.y >= cols) {
            return false;
        }
        char cell = grid[pos.x][pos.y];
        return cell == EMPTY || cell == PHONE;
    }

    public synchronized boolean isValidMoveForAgent(Position pos) {
        if (pos.x < 0 || pos.x >= rows || pos.y < 0 || pos.y >= cols) {
            return false;
        }
        char cell = grid[pos.x][pos.y];
        return cell == EMPTY || cell == PHONE || cell == NEO;
    }

    public synchronized Position getNextStepTowards(Position from, Position target) {
        boolean[][] visited = new boolean[rows][cols];
        Position[][] parent = new Position[rows][cols];
        Queue<Position> queue = new ArrayDeque<>();

        queue.add(from);
        visited[from.x][from.y] = true;

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            if (current.equals(target)) {
                break;
            }
            for (int[] dir : DIRECTIONS) {
                Position next = new Position(current.x + dir[0], current.y + dir[1]);
                if (!isInsideBounds(next) || visited[next.x][next.y]) {
                    continue;
                }
                if (!isPassableForAgent(next) && !next.equals(target)) {
                    continue;
                }
                visited[next.x][next.y] = true;
                parent[next.x][next.y] = current;
                queue.add(next);
            }
        }

        if (!visited[target.x][target.y]) {
            return null;
        }

        Position step = target;
        while (parent[step.x][step.y] != null && !parent[step.x][step.y].equals(from)) {
            step = parent[step.x][step.y];
        }
        return step.equals(from) ? target : step;
    }

    private boolean isInsideBounds(Position pos) {
        return pos.x >= 0 && pos.x < rows && pos.y >= 0 && pos.y < cols;
    }

    private boolean isPassableForAgent(Position pos) {
        char cell = grid[pos.x][pos.y];
        return cell == EMPTY || cell == PHONE || cell == NEO;
    }

    public synchronized Position getNeoPosition() {
        return new Position(neoPos.x, neoPos.y);
    }

    public synchronized List<Position> getPhones() {
        List<Position> result = new ArrayList<>();
        for (Phone phone : phones) {
            result.add(new Position(phone.getPosition().x, phone.getPosition().y));
        }
        return result;
    }

    public synchronized List<Position> getAgentPositions() {
        List<Position> result = new ArrayList<>();
        for (Position pos : agents) {
            result.add(new Position(pos.x, pos.y));
        }
        return result;
    }

    public synchronized Position getClosestPhone(Position from) {
        Phone closest = null;
        int minDist = Integer.MAX_VALUE;
        for (Phone phone : phones) {
            Position p = phone.getPosition();
            int dist = Math.abs(from.x - p.x) + Math.abs(from.y - p.y);
            if (dist < minDist) {
                minDist = dist;
                closest = phone;
            }
        }
        return closest != null ? closest.getPosition() : null;
    }

    public synchronized void display() {
        System.out.println("\n" + "─".repeat(cols * 2 + 1));
        for (int i = 0; i < rows; i++) {
            System.out.print("|");
            for (int j = 0; j < cols; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println("|");
        }
        System.out.println("─".repeat(cols * 2 + 1));
        System.out.println("📍 Neo en: " + neoPos);
        System.out.println("👥 Agentes: " + agents);
        System.out.println("📱 Teléfonos: " + getPhones());
    }

    public boolean isGameRunning() {
        return gameState.isGameRunning();
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    private boolean isPhone(Position pos) {
        for (Phone phone : phones) {
            if (phone.getPosition().equals(pos)) {
                return true;
            }
        }
        return false;
    }
}
