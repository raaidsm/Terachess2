package raaidsm.spring.test.models.pieces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.Square;
import raaidsm.spring.test.models.moves_and_attacks.AttackDir;
import raaidsm.spring.test.models.moves_and_attacks.AttackType;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.piece_properties.PieceType;
import raaidsm.spring.test.models.utils.Direction;
import raaidsm.spring.test.models.moves_and_attacks.MoveCalcResultStruct;
import raaidsm.spring.test.models.square_properties.SqrStat;
import raaidsm.spring.test.models.square_properties.SquarePreviewStruct;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    private final Logger logger = LoggerFactory.getLogger(King.class);
    private boolean canCastle = true;

    public King() {}
    public King(PieceType name, Colour colour, String location) {
        super(name, colour, location);
    }

    public void removeCastlingRights() {
        canCastle = false;
    }

    @Override
    protected List<MoveCalcResultStruct> calculateSquarePreviewResults() {
        List<MoveCalcResultStruct> results = new ArrayList<>();
        List<MoveCalcResultStruct> tempResults = new ArrayList<>();

        tempResults.add(checkAdjacentSquare(Direction.UP, AttackDir.VERTICAL));
        tempResults.add(checkAdjacentSquare(Direction.UP_RIGHT, AttackDir.DIAGONAL_ASCENDING));
        tempResults.add(checkAdjacentSquare(Direction.RIGHT, AttackDir.HORIZONTAL));
        tempResults.add(checkAdjacentSquare(Direction.DOWN_RIGHT, AttackDir.DIAGONAL_DESCENDING));
        tempResults.add(checkAdjacentSquare(Direction.DOWN, AttackDir.VERTICAL));
        tempResults.add(checkAdjacentSquare(Direction.DOWN_LEFT, AttackDir.DIAGONAL_ASCENDING));
        tempResults.add(checkAdjacentSquare(Direction.LEFT, AttackDir.HORIZONTAL));
        tempResults.add(checkAdjacentSquare(Direction.UP_LEFT, AttackDir.DIAGONAL_DESCENDING));

        for (MoveCalcResultStruct tempResult : tempResults) {
            if (tempResult != null) results.add(tempResult);
        }
        return results;
    }
    private MoveCalcResultStruct checkAdjacentSquare(Direction dir, AttackDir attackDir) {
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
        if (squarePreviewed.isAttacked(colour)) {
            return new MoveCalcResultStruct(null, squareName, attackType, attackDir, false);
        }
        //Guard clause for relative square having same coloured piece that can't be captured
        if (colour == preview.pieceColour) {
            return new MoveCalcResultStruct(null, squareName, attackType, attackDir, false);
        }
        //If code reaches this point, it means the square is either empty or has an opposite coloured piece
        return new MoveCalcResultStruct(null, squareName, attackType, attackDir);
    }
}