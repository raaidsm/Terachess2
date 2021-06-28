package raaidsm.spring.test.models;

import raaidsm.spring.test.exceptions.PieceNotOnBoardException;
import raaidsm.spring.test.models.utils.Direction;

import java.io.Serializable;

public class Point implements Serializable {
    private final int boardLength = 8;
    private final char[] letters = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H' };
    private int x;
    private int y;

    public static Direction getTwoPointDistAndDir(String squareName1, String squareName2) {
        //OVERVIEW: The two squares must be on the same horizontal or vertical plane, otherwise return -1
        Point point1 = new Point(squareName1);
        Point point2 = new Point(squareName2);

        if (point1.x == point2.x) {
            int dist = point2.y - point1.y;
            int magnitude = Math.abs(dist);
            Direction dir;

            if (dist == magnitude) dir = Direction.UP;
            else dir = Direction.DOWN;

            dir.setMagnitude(magnitude);
            return dir;
        }
        else if (point1.y == point2.y) {
            int dist = point2.x - point1.x;
            int magnitude = Math.abs(dist);
            Direction dir;

            if (dist == magnitude) dir = Direction.RIGHT;
            else dir = Direction.LEFT;

            dir.setMagnitude(magnitude);
            return dir;
        }
        else return null;
    }

    public Point(String squareName) {
        setPoint(squareName);
    }

    public String getPoint() {
        if (x == 0 && y == 0) return "00";
        char letterRep = letters[x - 1];
        int numberRep = y;
        return Character.toString(letterRep) + numberRep;
    }
    public void setPoint(String squareName) {
        if (squareName == null) {
            x = 0;
            y = 0;
            return;
        }
        String letterRep = squareName.substring(0, 1);
        int numberRep = Integer.parseInt(squareName.substring(1));
        x = new String(letters).indexOf(letterRep) + 1;
        y = numberRep;
    }
    public String findRelativeByXAndY(int x, int y) {
        if (x == 0 && y == 0) throw new PieceNotOnBoardException("No relative coordinate for piece off the board");
        int tempX = this.x + x;
        int numberRep = this.y + y;
        //Two guard clauses for relative point going off the board
        if (boardLength < tempX || boardLength < numberRep) return null;
        if (tempX < 1 || numberRep < 1) return null;
        int letterRep = letters[tempX - 1];
        return Character.toString(letterRep) + numberRep;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}