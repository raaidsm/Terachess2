package raaidsm.spring.test.models.pieces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raaidsm.spring.test.models.GameEngine;
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
    private final Logger logger = LoggerFactory.getLogger(Knight.class);

    public Knight() {}
    public Knight(PieceType name, Colour colour, String location) {
        super(name, colour, location);
    }

    @Override
    protected List<MoveCalcResultsStruct> calculateSquarePreviewResults() {
        List<MoveCalcResultsStruct> results = new ArrayList<>();
        List<MoveCalcResultsStruct> tempResults = new ArrayList<>();

        tempResults.add(hop(-1, 2));
        tempResults.add(hop(1, 2));
        tempResults.add(hop(-2, 1));
        tempResults.add(hop(2, 1));
        tempResults.add(hop(-2, -1));
        tempResults.add(hop(2, -1));
        tempResults.add(hop(-1, -2));
        tempResults.add(hop(1, -2));

        //Add every non-null result to list of results
        for (MoveCalcResultsStruct tempResult : tempResults) {
            if (tempResult != null) results.add(tempResult);
        }

        return results;
    }
    private MoveCalcResultsStruct hop(int x, int y) {
        //OVERVIEW: HOP_MOVE_OR_CAPTURE
        assert (x == 1 || x == 2) && (y == 1 || y == 2);
        //AttackType for this collection of attacks (yes, collection even though there's only one)
        AttackType attackType = AttackType.HOP_MOVE_OR_CAPTURE;
        SquarePreviewStruct preview = previewRelativeSquare(x, y);
        SqrStat status = preview.squareStatus;
        String squareName = preview.squareName;
        Piece piece = preview.piece;
        //Guard clause for relative point going off the board
        if (status == SqrStat.NO_SQUARE) return null;
        //Square has a same-coloured piece that can't be captured
        if (colour == preview.pieceColour) return null;
        //Attacking enemy king
        if (status == SqrStat.KING) {
            return new MoveCalcResultsStruct((King)piece, squareName, attackType, true);
        }
        return new MoveCalcResultsStruct(null, squareName, attackType, true);
    }
}