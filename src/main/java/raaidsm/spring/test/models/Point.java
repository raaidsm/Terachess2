package raaidsm.spring.test.models;

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
        char letterRep = letters[x+1];
        int numberRep = y;
        return Character.toString(letterRep) + numberRep;
    }
    public void setPoint(String squareName) {
        String letterRep = squareName.substring(0, 1);
        int numberRep = Integer.parseInt(squareName.substring(1));
        x = new String(letters).indexOf(letterRep) + 1;
        y = numberRep;
    }
}