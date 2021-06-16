package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.Square;
import raaidsm.spring.test.models.piece_properties.AttackType;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.piece_properties.PieceType;
import raaidsm.spring.test.models.utils.Direction;
import raaidsm.spring.test.models.utils.MoveCalcResultsStruct;
import raaidsm.spring.test.models.utils.SqrStat;
import raaidsm.spring.test.models.utils.SquarePreviewStruct;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    private boolean canCastle = true;

    public King() {}
    public King(PieceType name, Colour colour, String location) {
        super(name, colour, location);
    }

    public void removeCastlingRights() {
        canCastle = false;
    }

    @Override
    protected List<MoveCalcResultsStruct> calculateSquarePreviewResults() {
        List<MoveCalcResultsStruct> results = new ArrayList<>();
        List<MoveCalcResultsStruct> tempResults = new ArrayList<>();

        tempResults.add(checkAdjacentSquare(Direction.UP));
        tempResults.add(checkAdjacentSquare(Direction.UP_RIGHT));
        tempResults.add(checkAdjacentSquare(Direction.RIGHT));
        tempResults.add(checkAdjacentSquare(Direction.DOWN_RIGHT));
        tempResults.add(checkAdjacentSquare(Direction.DOWN));
        tempResults.add(checkAdjacentSquare(Direction.DOWN_LEFT));
        tempResults.add(checkAdjacentSquare(Direction.LEFT));
        tempResults.add(checkAdjacentSquare(Direction.UP_LEFT));

        for (MoveCalcResultsStruct tempResult : tempResults) {
            if (tempResult != null) results.add(tempResult);
        }
        return results;
    }
    private MoveCalcResultsStruct checkAdjacentSquare(Direction dir) {
        //AttackType for this collection of attacks (yes, collection even though there's only one)
        AttackType attackType = AttackType.MOVE_OR_CAPTURE;

        dir.resetMagnitude();
        SquarePreviewStruct preview = previewRelativeSquare(dir.x, dir.y);
        SqrStat status = preview.squareStatus;
        String squareName = preview.squareName;
        Square squarePreviewed = boardManager.getSquare(squareName);

        //Guard clause for relative square going off the board
        if (status == SqrStat.NO_SQUARE) return null;
        //Guard clause for relative square being attacked (thus King can't move to it)
        if (squarePreviewed.isAttacked(colour)) return null;
        //Guard clause for relative square having same coloured piece that can't be captured
        if (colour == preview.pieceColour) return null;
        //If code reaches this point, it means the square is either empty or has an opposite coloured piece
        return new MoveCalcResultsStruct(null, squareName, attackType, true);
    }
}