package raaidsm.spring.test.models.pieces;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.piece_properties.AttackType;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.piece_properties.PieceType;
import raaidsm.spring.test.models.utils.Direction;
import raaidsm.spring.test.models.utils.MoveCalcResultsStruct;
import raaidsm.spring.test.models.utils.SqrStat;
import raaidsm.spring.test.models.utils.SquarePreviewStruct;

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
    private List<MoveCalcResultsStruct> moveInALine(Direction dir) {
        /* OVERVIEW:
        -MOVE_OR_CAPTURE
        -Calculate moves in a line.
        -If an opposite colour piece is hit, keep calculating to check for pin
        -If a same colour piece or the edge of the board is hit, stop calculating further
        */
        AttackType attackType = AttackType.MOVE_OR_CAPTURE;
        List<MoveCalcResultsStruct> results = new ArrayList<>();
        int i = 1;
        boolean pinnablePieceHit = false;
        boolean unpinnablePieceHit = false;
        boolean edgeOfBoardHit = false;
        Piece pinnablePiece = null;
        while (!pinnablePieceHit && !unpinnablePieceHit && !edgeOfBoardHit) {
            dir.setMagnitude(i++);
            SquarePreviewStruct preview = previewRelativeSquare(dir.x, dir.y);
            Piece piece = preview.piece;
            SqrStat status = preview.squareStatus;
            //Guard clause for hitting edge of the board
            if (status == SqrStat.NO_SQUARE) {
                edgeOfBoardHit = true;
                continue;
            }
            //Guard clause for same coloured piece being hit
            if (status != SqrStat.EMPTY && colour == preview.pieceColour) {
                unpinnablePieceHit = true;
                continue;
            }
            //Guard clause for opposite colour piece being hit
            if (colour != preview.pieceColour) {
                if (piece.getType() == PieceType.KING) {
                    unpinnablePieceHit = true;
                    results.add(new MoveCalcResultsStruct((King)piece, attackType, true));
                }
                else {
                    pinnablePieceHit = true;
                    pinnablePiece = piece;
                    results.add(new MoveCalcResultsStruct(null, attackType, true));
                }
                continue;
            }
            //If code reaches this point, it means the square is empty
            results.add(new MoveCalcResultsStruct(null, attackType, true));
        }

        dir.resetMagnitude();
        if (pinnablePieceHit) continueToFindPin(pinnablePiece, dir);
        return results;
    }
    private void continueToFindPin(Piece pinnablePiece, Direction dir) {
        int i = 1;
        while (true) {
            dir.setMagnitude(i++);
            SquarePreviewStruct preview = previewRelativeSquare(dir.x, dir.y);
            SqrStat status = preview.squareStatus;
            //Guard clause for hitting the edge of the board
            if (status == SqrStat.NO_SQUARE) break;
            //Guard clause for hitting piece that isn't opposite-colour king
            if (status != SqrStat.EMPTY && (colour == preview.pieceColour || status == SqrStat.NON_KING_PIECE)) break;
            //Guard clause for hitting opposite-colour king, meaning pinnable piece is pinned
            if (status != SqrStat.EMPTY) {
                pinnablePiece.setPinned(true);
                pinnablePiece.setPinningPiece(this);
                break;
            }
            //If code reaches this point, it means this square is empty (therefore do nothing and keep the loop going)
        }
    }
}