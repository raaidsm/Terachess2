package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.piece_properties.PieceType;
import raaidsm.spring.test.models.utils.Direction;
import raaidsm.spring.test.models.moves_and_attacks.MoveCalcResultsStruct;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    public Queen() {}
    public Queen(PieceType name, Colour colour, String location) {
        super(name, colour, location);
    }

    @Override
    protected List<MoveCalcResultsStruct> calculateSquarePreviewResults() {
        List<MoveCalcResultsStruct> results = new ArrayList<>();
        results.addAll(moveOrCaptureInALine(Direction.UP));
        results.addAll(moveOrCaptureInALine(Direction.UP_RIGHT));
        results.addAll(moveOrCaptureInALine(Direction.RIGHT));
        results.addAll(moveOrCaptureInALine(Direction.DOWN_RIGHT));
        results.addAll(moveOrCaptureInALine(Direction.DOWN));
        results.addAll(moveOrCaptureInALine(Direction.DOWN_LEFT));
        results.addAll(moveOrCaptureInALine(Direction.LEFT));
        results.addAll(moveOrCaptureInALine(Direction.UP_LEFT));
        return results;
    }
}