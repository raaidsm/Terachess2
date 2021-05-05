package raaidsm.spring.test.models;

import java.io.Serializable;

public class MoveForm implements Serializable {
    private String firstSquare = "";
    private String secondSquare = "";

    public MoveForm() {}

    public String getFirstSquare() {
        return firstSquare;
    }
    public void setFirstSquare(String firstSquare) {
        this.firstSquare = firstSquare;
    }
    public String getSecondSquare() {
        return secondSquare;
    }
    public void setSecondSquare(String secondSquare) {
        this.secondSquare = secondSquare;
    }
}
