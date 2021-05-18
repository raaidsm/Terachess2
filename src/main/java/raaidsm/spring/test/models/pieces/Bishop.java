package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.piece_properties.PieceType;

public class Bishop extends Piece {
    public Bishop() {}
    public Bishop(PieceType name, Colour colour, String location) {
        super(name, colour, location);
    }
}