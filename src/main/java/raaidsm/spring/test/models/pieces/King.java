package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;

public class King extends Piece {
    private boolean canCastle = true;

    public King() {}
    public King(String name, String colour, String location) {
        super(name, colour, location);
    }
}