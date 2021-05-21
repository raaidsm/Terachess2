package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.piece_properties.AttackType;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.piece_properties.PieceType;
import raaidsm.spring.test.models.utils.AttackingPieceStruct;
import raaidsm.spring.test.models.utils.MoveCalcResultsStruct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pawn extends Piece {
    private boolean hasInitialPawnMove = true;

    public Pawn() {}
    public Pawn(PieceType name, Colour colour, String location) {
        super(name, colour, location);
        String[] promotionPieces = { "queen", "rook", "bishop", "knight" };
        promotion.addAll(Arrays.asList(promotionPieces));
    }

    public void removeInitialPawnMove() {
        hasInitialPawnMove = false;
    }

    @Override
    public MoveCalcResultsStruct calculateMoves() {
        //region Variables to return
        King checkedKing = null;
        AttackType checkAttackType = null;
        boolean hasMoves = false;
        //endregion

        List<MoveCalcResultsStruct> results = new ArrayList<>();
        results.add(up1());
        results.add(up2());
        results.add(upCapture(-1));
        results.add(upCapture(1));
        for (MoveCalcResultsStruct result : results) {
            hasMoves = true;
            if (result.hasMoves) {
                checkedKing = result.checkedKing;
                AttackType attackType = result.attackType;
                if (checkedKing != null) checkAttackType = attackType;
                legalMoves.add(new AttackingPieceStruct(this, attackType));
            }
        }
        return new MoveCalcResultsStruct(checkedKing, checkAttackType, hasMoves);
    }
    private MoveCalcResultsStruct up1() {
        //OVERVIEW: ONLY_MOVE
        String squareName = location.findRelativeByXAndY(0, 1);
        //Guard clause for relative point going off the board
        if (squareName == null) return new MoveCalcResultsStruct(null, null, false);
        Piece pieceAtSquare = board.get(squareName).containedPiece;
        //Guard clause for there being a piece in the way
        if (pieceAtSquare != null) return new MoveCalcResultsStruct(null, null, false);
        //Square is free to move to
        return new MoveCalcResultsStruct(null, AttackType.ONLY_MOVE, true);
    }
    private MoveCalcResultsStruct up2() {
        //OVERVIEW: ONLY_MOVE
        //Guard clause for not having initial pawn move to make
        if (!hasInitialPawnMove) return new MoveCalcResultsStruct(null, null, false);
        for (int i = 1; i <= 2; i++) {
            String squareName = location.findRelativeByXAndY(0, i);
            //Guard clause for relative point going off the board
            if (squareName == null) return new MoveCalcResultsStruct(null, null, false);
            Piece pieceAtSquare = board.get(squareName).containedPiece;
            //Guard clause for there being a piece in the way
            if (pieceAtSquare != null) return new MoveCalcResultsStruct(null, null, false);
        }
        //Both squares above are open so up2 is valid
        return new MoveCalcResultsStruct(null, AttackType.ONLY_MOVE, true);
    }
    private MoveCalcResultsStruct upCapture(int direction) {
        assert direction == 1 || direction == -1;
        //This variable inverts "left" and "right" for black pieces
        int directionByColour = colour == Colour.WHITE ? 1 : -1;
        String squareName = location.findRelativeByXAndY(direction * directionByColour, 1);
        //Guard clause for relative point going off the board
        if (squareName == null) return new MoveCalcResultsStruct(null, null, false);
        Piece pieceAtSquare = board.get(squareName).containedPiece;
        //Guard clause for there being no piece to capture
        if (pieceAtSquare == null) return new MoveCalcResultsStruct(null, null, false);
        //Square has a same-coloured piece that can't be captured
        if (colour == pieceAtSquare.getColour()) return new MoveCalcResultsStruct(null, null, false);
        //Square has an enemy piece to capture at this square
        if (pieceAtSquare.getType() == PieceType.KING) return new MoveCalcResultsStruct((King)pieceAtSquare, AttackType.ONLY_CAPTURE, true);
        return new MoveCalcResultsStruct(null, AttackType.ONLY_CAPTURE, true);
    }
}