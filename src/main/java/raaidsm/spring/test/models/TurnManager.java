package raaidsm.spring.test.models;

public class TurnManager {
    private Colour colour;

    public TurnManager() {
        this.colour = Colour.WHITE;
    }

    public Colour getColour() {
        return colour;
    }

    public void switchColour() {
        if (colour == Colour.WHITE) colour = Colour.BLACK;
        else if (colour == Colour.BLACK) colour = Colour.WHITE;
    }
    public Colour getColourAndSwitch() {
        Colour tempColour = colour;
        switchColour();
        return tempColour;
    }
}