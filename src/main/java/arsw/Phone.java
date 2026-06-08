package main.java.arsw;

public class Phone {
    private final Position position;

    public Phone(Position position) {
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
