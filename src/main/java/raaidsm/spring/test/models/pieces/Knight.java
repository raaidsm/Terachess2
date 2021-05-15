package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.utils.Colour;

public class Knight extends Piece {
    public Knight() {}
    public Knight(String name, Colour colour, String location) {
        super(name, colour, location);
    }
}