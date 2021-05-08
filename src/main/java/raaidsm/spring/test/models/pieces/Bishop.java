package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;

import java.util.HashMap;

public class Bishop extends Piece {
    public Bishop() {}
    public Bishop(String name, String colour, String location, HashMap<String, Piece> board) {
        super(name, colour, location, board);
    }
}