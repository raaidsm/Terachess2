package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.utils.Colour;

public class Bishop extends Piece {
    public Bishop() {}
    public Bishop(String name, Colour colour, String location) {
        super(name, colour, location);
    }
}