package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.utils.Colour;

public class King extends Piece {
    private boolean canCastle = true;

    public King() {}
    public King(String name, Colour colour, String location) {
        super(name, colour, location);
    }
}