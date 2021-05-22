package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.piece_properties.AttackType;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.piece_properties.PieceType;
import raaidsm.spring.test.models.utils.MoveCalcResultsStruct;
import raaidsm.spring.test.models.utils.SquarePreviewStruct;
import raaidsm.spring.test.models.utils.SqrStat;

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
    protected List<MoveCalcResultsStruct> calculateSquarePreviewResults() {
        List<MoveCalcResultsStruct> results = new ArrayList<>();
        results.add(up1());
        results.add(up2());
        results.add(upCapture(-1));
        results.add(upCapture(1));
        return results;
    }
    private MoveCalcResultsStruct up1() {
        //OVERVIEW: ONLY_MOVE
        int directionByColour = colour == Colour.WHITE ? 1 : -1;
        SquarePreviewStruct preview = previewRelativeSquare(0, directionByColour);
        SqrStat status = preview.squareStatus;
        //Guard clause for relative point going off the board
        if (status == SqrStat.NO_SQUARE) return new MoveCalcResultsStruct(null, null, false);
        //Guard clause for there being a piece in the way
        if (status != SqrStat.EMPTY) return new MoveCalcResultsStruct(null, null, false);
        //Square is free to move to
        return new MoveCalcResultsStruct(null, AttackType.ONLY_MOVE, true);
    }
    private MoveCalcResultsStruct up2() {
        //OVERVIEW: ONLY_MOVE
        int directionByColour = colour == Colour.WHITE ? 1 : -1;
        //Guard clause for not having initial pawn move to make
        if (!hasInitialPawnMove) return new MoveCalcResultsStruct(null, null, false);
        for (int i = 1; i <= 2; i++) {
            SquarePreviewStruct preview = previewRelativeSquare(0, i * directionByColour);
            SqrStat status = preview.squareStatus;
            //Guard clause for relative point going off the board
            if (status == SqrStat.NO_SQUARE) return new MoveCalcResultsStruct(null, null, false);
            //Guard clause for there being a piece in the way
            if (status != SqrStat.EMPTY) return new MoveCalcResultsStruct(null, null, false);
        }
        //Both squares above are open so up2 is valid
        return new MoveCalcResultsStruct(null, AttackType.ONLY_MOVE, true);
    }
    private MoveCalcResultsStruct upCapture(int direction) {
        //OVERVIEW: ONLY_CAPTURE
        assert direction == 1 || direction == -1;
        //This variable inverts "left" and "right" for black pieces
        int directionByColour = colour == Colour.WHITE ? 1 : -1;
        SquarePreviewStruct preview = previewRelativeSquare(direction * directionByColour, directionByColour);
        SqrStat status = preview.squareStatus;
        //Guard clause for relative point going off the board
        if (status == SqrStat.NO_SQUARE) return new MoveCalcResultsStruct(null, null, false);
        //Guard clause for there being no piece to capture
        if (status == SqrStat.EMPTY) return new MoveCalcResultsStruct(null, null, false);
        //Square has a same-coloured piece that can't be captured
        if (colour == preview.pieceColour) return new MoveCalcResultsStruct(null, null, false);
        //Square has an enemy piece to capture at this square
        if (status == SqrStat.KING) return new MoveCalcResultsStruct((King)preview.piece, AttackType.ONLY_CAPTURE, true);
        return new MoveCalcResultsStruct(null, AttackType.ONLY_CAPTURE, true);
    }
}