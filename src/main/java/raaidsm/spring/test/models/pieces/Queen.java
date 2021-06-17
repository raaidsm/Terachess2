package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.moves_and_attacks.AttackDirection;
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
        results.addAll(moveOrCaptureInALine(Direction.UP, AttackDirection.VERTICAL));
        results.addAll(moveOrCaptureInALine(Direction.UP_RIGHT, AttackDirection.DIAGONAL_ASCENDING));
        results.addAll(moveOrCaptureInALine(Direction.RIGHT, AttackDirection.HORIZONTAL));
        results.addAll(moveOrCaptureInALine(Direction.DOWN_RIGHT, AttackDirection.DIAGONAL_DESCENDING));
        results.addAll(moveOrCaptureInALine(Direction.DOWN, AttackDirection.VERTICAL));
        results.addAll(moveOrCaptureInALine(Direction.DOWN_LEFT, AttackDirection.DIAGONAL_ASCENDING));
        results.addAll(moveOrCaptureInALine(Direction.LEFT, AttackDirection.HORIZONTAL));
        results.addAll(moveOrCaptureInALine(Direction.UP_LEFT, AttackDirection.DIAGONAL_DESCENDING));

        return results;
    }
}