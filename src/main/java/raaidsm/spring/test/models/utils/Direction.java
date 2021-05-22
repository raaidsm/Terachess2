package raaidsm.spring.test.models.utils;

public enum Direction {
    UP(0, 1),
    UP_RIGHT(1, 1),
    RIGHT(1, 0),
    DOWN_RIGHT(1, -1),
    DOWN(0, -1),
    DOWN_LEFT(-1, -1),
    LEFT(-1, 0),
    UP_LEFT(-1, 1);

    public int defaultX;
    public int defaultY;
    public int x;
    public int y;

    Direction(int x, int y) {
        defaultX = x;
        defaultY = y;
        this.x = x;
        this.y = y;
    }

    public int getMagnitude() {
        checkDirectionValid();
        if (x != 0) return Math.abs(x);
        else return Math.abs(y);
    }
    public void setMagnitude(int magnitude) {
        x = defaultX * magnitude;
        y = defaultY * magnitude;
        checkDirectionValid();
    }
    public void resetMagnitude() {
        x = defaultX;
        y = defaultY;
    }
    private void checkDirectionValid() {
        assert (x == 0 || y == 0) || Math.abs(x) == Math.abs(y);
    }
}