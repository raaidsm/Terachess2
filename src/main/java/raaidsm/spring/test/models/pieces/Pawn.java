package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;

import java.util.Arrays;

public class Pawn extends Piece {
    private boolean hasInitialPawnMove = true;

    public Pawn() {}
    public Pawn(String name, String colour, String location) {
        super(name, colour, location);
        String[] promotionPieces = { "queen", "rook", "bishop", "knight" };
        promotion.addAll(Arrays.asList(promotionPieces));
    }
}