package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.piece_properties.PieceType;
import raaidsm.spring.test.models.utils.MoveCalcResultsStruct;

import java.util.Arrays;

public class Pawn extends Piece {
    private boolean hasInitialPawnMove = true;

    public Pawn() {}
    public Pawn(PieceType name, Colour colour, String location) {
        super(name, colour, location);
        String[] promotionPieces = { "queen", "rook", "bishop", "knight" };
        promotion.addAll(Arrays.asList(promotionPieces));
    }

    @Override
    public MoveCalcResultsStruct calculateMoves() {
        //TODO: For now, returning default value
        return new MoveCalcResultsStruct(null, null, true);
    }
    private void up1() {}
    private void up2() {}
    private void upLeft() {}
    private void upRight() {}
}