package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.piece_properties.PieceType;
import raaidsm.spring.test.models.utils.Direction;
import raaidsm.spring.test.models.moves_and_attacks.MoveCalcResultsStruct;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    private boolean canCastle = true;

    public Rook() {}
    public Rook(PieceType name, Colour colour, String location) {
        super(name, colour, location);
    }

    public void removeCastlingRights() {
        canCastle = false;
    }

    @Override
    protected List<MoveCalcResultsStruct> calculateSquarePreviewResults() {
        List<MoveCalcResultsStruct> results = new ArrayList<>();
        results.addAll(moveOrCaptureInALine(Direction.UP));
        results.addAll(moveOrCaptureInALine(Direction.RIGHT));
        results.addAll(moveOrCaptureInALine(Direction.DOWN));
        results.addAll(moveOrCaptureInALine(Direction.LEFT));
        return results;
    }
}