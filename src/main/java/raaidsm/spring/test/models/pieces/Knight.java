package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.piece_properties.AttackType;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.piece_properties.PieceType;
import raaidsm.spring.test.models.utils.MoveCalcResultsStruct;
import raaidsm.spring.test.models.utils.SqrStat;
import raaidsm.spring.test.models.utils.SquarePreviewStruct;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    public Knight() {}
    public Knight(PieceType name, Colour colour, String location) {
        super(name, colour, location);
    }

    @Override
    protected List<MoveCalcResultsStruct> calculateSquarePreviewResults() {
        List<MoveCalcResultsStruct> results = new ArrayList<>();
        results.add(hop(-1, 2));
        results.add(hop(1, 2));
        results.add(hop(-2, 1));
        results.add(hop(2, 1));
        results.add(hop(-2, -1));
        results.add(hop(2, -1));
        results.add(hop(-1, -2));
        results.add(hop(1, -2));
        return results;
    }
    private MoveCalcResultsStruct hop(int x, int y) {
        //OVERVIEW: HOP_MOVE_OR_CAPTURE
        assert (x == 1 || x == 2) && (y == 1 || y == 2);
        SquarePreviewStruct preview = previewRelativeSquare(x, y);
        SqrStat status = preview.squareStatus;
        //Guard clause for relative point going off the board
        if (status == SqrStat.NO_SQUARE) return new MoveCalcResultsStruct(null, null, false);
        //Square has a same-coloured piece that can't be captured
        if (colour == preview.pieceColour) return new MoveCalcResultsStruct(null, null, false);
        //Attacking enemy king
        if (status == SqrStat.KING) return new MoveCalcResultsStruct((King)preview.piece, AttackType.HOP_MOVE_OR_CAPTURE, true);
        return new MoveCalcResultsStruct(null, AttackType.HOP_MOVE_OR_CAPTURE, true);
    }
}