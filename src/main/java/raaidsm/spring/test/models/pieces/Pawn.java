package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;

import java.util.HashMap;

public class Pawn extends Piece {
    private boolean hasInitialPawnMove = true;

    public Pawn() {}
    public Pawn(String name, String colour, String location, HashMap<String, Piece> board) {
        super(name, colour, location, board);
    }
}