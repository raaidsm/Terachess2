package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;

import java.util.HashMap;

public class Queen extends Piece {
    public Queen() {}
    public Queen(String name, String colour, String location, HashMap<String, Piece> board) {
        super(name, colour, location, board);
    }
}