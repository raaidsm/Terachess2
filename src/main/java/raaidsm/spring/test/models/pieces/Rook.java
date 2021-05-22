package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.piece_properties.PieceType;
import raaidsm.spring.test.models.utils.Direction;
import raaidsm.spring.test.models.utils.MoveCalcResultsStruct;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    private boolean canCastle = true;

    public Rook() {}
    public Rook(PieceType name, Colour colour, String location) {
        super(name, colour, location);
    }

    @Override
    protected List<MoveCalcResultsStruct> calculateSquarePreviewResults() {
        List<MoveCalcResultsStruct> results = new ArrayList<>();
        results.addAll(moveInALine(Direction.UP));
        results.addAll(moveInALine(Direction.DOWN));
        results.addAll(moveInALine(Direction.LEFT));
        results.addAll(moveInALine(Direction.RIGHT));
        //TODO: For now, return default value
        return results;
    }
    private List<MoveCalcResultsStruct> moveInALine(Direction direction) {
        //TODO: For now, return default value
        return new ArrayList<>();
    }
}