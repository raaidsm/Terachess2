package raaidsm.spring.test.models;

import java.io.Serializable;

public class SquareForm implements Serializable {
    private String squareName = "";

    public SquareForm() {}

    public String getSquareName() {
        return squareName;
    }
    public void setSquareName(String squareName) {
        this.squareName = squareName;
    }
}
