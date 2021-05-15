package raaidsm.spring.test.models.utils;

public class TurnManager {
    private Colour colour;

    public TurnManager() {
        this.colour = Colour.WHITE;
    }

    public Colour getColour() {
        return colour;
    }

    public void switchC() {
        if (colour == Colour.WHITE) colour = Colour.BLACK;
        else if (colour == Colour.BLACK) colour = Colour.WHITE;
    }
    public Colour getAndSwitch() {
        Colour tempColour = colour;
        switchC();
        return tempColour;
    }
}