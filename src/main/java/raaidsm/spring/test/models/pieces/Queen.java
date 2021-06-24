package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.moves_and_attacks.AttackDir;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.piece_properties.PieceType;
import raaidsm.spring.test.models.utils.Direction;
import raaidsm.spring.test.models.moves_and_attacks.MoveCalcResultStruct;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    public Queen() {}
    public Queen(PieceType name, Colour colour, String location) {
        super(name, colour, location);
    }

    @Override
    protected List<MoveCalcResultStruct> calculateSquarePreviewResults() {
        List<MoveCalcResultStruct> results = new ArrayList<>();
        results.addAll(moveOrCaptureInALine(Direction.UP, AttackDir.VERTICAL));
        results.addAll(moveOrCaptureInALine(Direction.UP_RIGHT, AttackDir.DIAGONAL_ASCENDING));
        results.addAll(moveOrCaptureInALine(Direction.RIGHT, AttackDir.HORIZONTAL));
        results.addAll(moveOrCaptureInALine(Direction.DOWN_RIGHT, AttackDir.DIAGONAL_DESCENDING));
        results.addAll(moveOrCaptureInALine(Direction.DOWN, AttackDir.VERTICAL));
        results.addAll(moveOrCaptureInALine(Direction.DOWN_LEFT, AttackDir.DIAGONAL_ASCENDING));
        results.addAll(moveOrCaptureInALine(Direction.LEFT, AttackDir.HORIZONTAL));
        results.addAll(moveOrCaptureInALine(Direction.UP_LEFT, AttackDir.DIAGONAL_DESCENDING));

        return results;
    }
}