package raaidsm.spring.test.models.utils;

public enum Direction {
    UP(0, 1),
    DOWN(0, -1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    public int x;
    public int y;

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getMagnitude() {
        assert x == 0 || y == 0;
        return x + y;
    }
    public void setMagnitude(int magnitude) {
        if (x != 0) x = magnitude;
        if (y != 0) y = magnitude;
    }
    public void resetMagnitude() {
        if (x != 0) x = 1;
        if (y != 0) y = 1;
    }
}