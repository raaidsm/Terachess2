package raaidsm.spring.test.models.pieces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.moves_and_attacks.AttackType;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.piece_properties.PieceType;
import raaidsm.spring.test.models.moves_and_attacks.MoveCalcResultsStruct;
import raaidsm.spring.test.models.square_properties.SquarePreviewStruct;
import raaidsm.spring.test.models.square_properties.SqrStat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pawn extends Piece {
    private final Logger logger = LoggerFactory.getLogger(Pawn.class);
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
        MoveCalcResultsStruct up1Result = up1();
        MoveCalcResultsStruct up2Result = up2();
        MoveCalcResultsStruct upCaptureResult1 = upCapture(-1);
        MoveCalcResultsStruct upCaptureResult2 = upCapture(1);
        if (up1Result != null) results.add(up1Result);
        if (up2Result != null) results.add(up2Result);
        if (upCaptureResult1 != null) results.add(upCaptureResult1);
        if (upCaptureResult2 != null) results.add(upCaptureResult2);
        return results;
    }
    private MoveCalcResultsStruct up1() {
        //OVERVIEW: ONLY_MOVE
        int directionByColour = colour == Colour.WHITE ? 1 : -1;
        //AttackType for this collection of attacks (yes, collection even though there's only one)
        AttackType attackType = AttackType.ONLY_MOVE;
        SquarePreviewStruct preview = previewRelativeSquare(0, directionByColour);
        SqrStat status = preview.squareStatus;
        String squareName = preview.squareName;
        //Guard clause for relative point going off the board
        if (status == SqrStat.NO_SQUARE) return null;
        //Guard clause for there being a piece in the way
        if (status != SqrStat.EMPTY) return null;
        //Square is free to move to
        return new MoveCalcResultsStruct(null, squareName, attackType, true);
    }
    private MoveCalcResultsStruct up2() {
        //OVERVIEW: ONLY_MOVE
        int directionByColour = colour == Colour.WHITE ? 1 : -1;
        //AttackType for this collection of attacks (yes, collection even though there's only one)
        AttackType attackType = AttackType.ONLY_MOVE;
        String squareName = null;
        //Guard clause for not having initial pawn move to make
        if (!hasInitialPawnMove) return null;
        for (int i = 1; i <= 2; i++) {
            SquarePreviewStruct preview = previewRelativeSquare(0, i * directionByColour);
            SqrStat status = preview.squareStatus;
            squareName = preview.squareName;
            //Guard clause for relative point going off the board
            if (status == SqrStat.NO_SQUARE) return null;
            //Guard clause for there being a piece in the way
            if (status != SqrStat.EMPTY) return null;
        }
        //Both squares above are open so up2 is valid
        return new MoveCalcResultsStruct(null, squareName, attackType, true);
    }
    private MoveCalcResultsStruct upCapture(int direction) {
        //OVERVIEW: ONLY_CAPTURE
        assert direction == 1 || direction == -1;
        //This variable inverts "left" and "right" for black pieces
        int directionByColour = colour == Colour.WHITE ? 1 : -1;
        //AttackType for this collection of attacks (yes, collection even though there's only one)
        AttackType attackType = AttackType.ONLY_CAPTURE;
        SquarePreviewStruct preview = previewRelativeSquare(direction * directionByColour, directionByColour);
        SqrStat status = preview.squareStatus;
        String squareName = preview.squareName;
        Piece piece = preview.piece;
        //Guard clause for relative point going off the board
        if (status == SqrStat.NO_SQUARE) return null;
        //Guard clause for there being no piece to capture
        if (status == SqrStat.EMPTY) return new MoveCalcResultsStruct(
                null, squareName, attackType, false);
        //Square has a same-coloured piece that can't be captured
        if (colour == preview.pieceColour) return new MoveCalcResultsStruct(
                null, squareName, attackType, false);
        //Square has an enemy piece to capture at this square
        if (status == SqrStat.KING) return new MoveCalcResultsStruct((King)piece, squareName, attackType, true);
        return new MoveCalcResultsStruct(null, squareName, attackType, true);
    }
}