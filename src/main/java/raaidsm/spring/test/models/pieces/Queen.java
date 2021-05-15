package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.utils.Colour;

public class Queen extends Piece {
    public Queen() {}
    public Queen(String name, Colour colour, String location) {
        super(name, colour, location);
    }
}