package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.piece_properties.AttackType;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.piece_properties.PieceType;
import raaidsm.spring.test.models.utils.AttackingPieceStruct;
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
    private MoveCalcResultsStruct up1() {
        //OVERVIEW: ONLY_MOVE
        //Take away initial pawn move since a move has been made
        hasInitialPawnMove = false;
        String squareName = location.findRelativeByXAndY(0, 1);
        //Guard clause for relative point going off the board
        if (squareName == null) return new MoveCalcResultsStruct(null, null, false);
        Piece pieceAtSquare = board.get(squareName).containedPiece;
        //There is a piece in the way
        if (pieceAtSquare != null) return new MoveCalcResultsStruct(null, null, false);
        //Square is free to move to
        else return new MoveCalcResultsStruct(null, AttackType.ONLY_MOVE, true);
    }
    private MoveCalcResultsStruct up2() {
        //TODO: For now, returning default value
        return null;
    }
    private MoveCalcResultsStruct upLeft() {
        //Take away initial pawn move since a move has been made
        hasInitialPawnMove = false;
        //TODO: For now, returning default value
        return null;
    }
    private MoveCalcResultsStruct upRight() {
        //Take away initial pawn move since a move has been made
        hasInitialPawnMove = false;
        //TODO: For now, returning default value
        return null;
    }
}