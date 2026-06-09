package arsw;

public class Wall {
    private final Position position;

    public Wall(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return position.toString();
    }
}
