package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.piece_properties.PieceType;
import raaidsm.spring.test.models.utils.Direction;
import raaidsm.spring.test.models.utils.MoveCalcResultsStruct;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {
    public Bishop() {}
    public Bishop(PieceType name, Colour colour, String location) {
        super(name, colour, location);
    }

    @Override
    protected List<MoveCalcResultsStruct> calculateSquarePreviewResults() {
        List<MoveCalcResultsStruct> results = new ArrayList<>();
        results.addAll(moveOrCaptureInALine(Direction.UP_LEFT));
        results.addAll(moveOrCaptureInALine(Direction.UP_RIGHT));
        results.addAll(moveOrCaptureInALine(Direction.DOWN_RIGHT));
        results.addAll(moveOrCaptureInALine(Direction.DOWN_LEFT));
        return results;
    }
}