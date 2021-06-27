package raaidsm.spring.test.models;

import raaidsm.spring.test.exceptions.PieceNotOnBoardException;

import java.io.Serializable;

public class Point implements Serializable {
    private final int boardLength = 8;
    private final char[] letters = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H' };
    private int x;
    private int y;

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