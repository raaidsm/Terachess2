package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;

import java.util.HashMap;

public class Rook extends Piece {
    private boolean canCastle = true;

    public Rook() {}
    public Rook(String name, String colour, String location) {
        super(name, colour, location);
    }
}