package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.utils.Colour;
import raaidsm.spring.test.models.utils.PieceType;

public class Rook extends Piece {
    private boolean canCastle = true;

    public Rook() {}
    public Rook(PieceType name, Colour colour, String location) {
        super(name, colour, location);
    }
}