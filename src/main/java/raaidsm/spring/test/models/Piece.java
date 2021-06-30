package raaidsm.spring.test.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raaidsm.spring.test.models.managers.BoardManager;
import raaidsm.spring.test.models.moves_and_attacks.*;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.pieces.King;
import raaidsm.spring.test.models.pieces.Pawn;
import raaidsm.spring.test.models.square_properties.SqrStat;
import raaidsm.spring.test.models.square_properties.SquarePreviewStruct;
import raaidsm.spring.test.models.utils.*;
import raaidsm.spring.test.models.piece_properties.PieceType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Piece implements Serializable {
    //region Field Variables
    private final Logger logger = LoggerFactory.getLogger(Piece.class);
    protected PieceType type;
    protected Colour colour;
    protected Point location;
    protected List<AttackOnSquareStruct> legalMoves;
    protected List<PinOnPieceStruct> pins;
    protected List<String> promotion;
    protected BoardManager boardManager;
    //endregion

    public Piece() {}
    public Piece(PieceType type, Colour colour, String location) {
        this.type = type;
        this.colour = colour;
        this.location = new Point(location);
        legalMoves = new ArrayList<>();
        pins = new ArrayList<>();
        promotion = new ArrayList<>();
        this.boardManager = null;
    }

    //region Getters & Setters
    public PieceType getType() {
        return type;
    }
    public void setType(PieceType type) {
        this.type = type;
    }
    public Colour getColour() {
        return colour;
    }
    public void setColour(Colour colour) {
        this.colour = colour;
    }
    public String getLocation() {
        return location.getPoint();
    }
    public void setLocation(String location) {
        this.location.setPoint(location);
    }
    public List<AttackOnSquareStruct> getLegalMoves() {
        return legalMoves;
    }
    public boolean isPinned() {
        return !pins.isEmpty();
    }
    public void setBoardManager(BoardManager boardManager) {
        this.boardManager = boardManager;
    }
    //endregion

    public boolean hasMoves() {
        return !legalMoves.isEmpty();
    }
    public MoveCalcSummaryStruct calculateMoves() {
        //OVERVIEW: Return checked king (null if none) and whether piece has any legal moves

        //region Variables to Return
        King checkedKing = null;
        AttackType checkAttackType = null;
        boolean hasMoves = false;
        List<String> squareNamesOnPathOfCheck = new ArrayList<>();
        //endregion

        clearAllMoves();
        List<MoveCalcResultStruct> results = calculateSquarePreviewResults();
        for (MoveCalcResultStruct result : results) {
            //Variables for Convenience
            AttackType attackType = result.attackType;
            AttackDir attackDir = result.attackDir;
            String squareName = result.attackedSquareName;

            //If on path of check
            if (result.isOnPathOfCheck) squareNamesOnPathOfCheck.add(squareName);
            //If possible move
            if (result.hasMoves) {
                hasMoves = true;
                if (result.checkedKing != null) {
                    checkedKing = result.checkedKing;
                    checkAttackType = attackType;
                }
                //Add result to legal moves
                legalMoves.add(new AttackOnSquareStruct(this, attackType, attackDir, squareName));
            }

            //Record attack in the square attacked
            Square squareAttacked = boardManager.getSquare(squareName);
            if (attackType != AttackType.ONLY_MOVE) squareAttacked.setAttack(this, colour);
        }

        //Return summary for this piece
        return new MoveCalcSummaryStruct(checkedKing, checkAttackType, hasMoves)
                .setSquareNamesOnPathOfCheck(squareNamesOnPathOfCheck);
    }
    public boolean reduceMovesDueToPin() {
        //OVERVIEW: Return whether piece has any legal moves
        //If piece has no moves, there is nothing to reduce (the only important argument here is hasMoves)
        if (!hasMoves()) return false;

        //If piece has moves and isn't pinned then just return true
        if (!isPinned()) return true;

        //If code gets this far, it means the piece is pinned and there are moves to possibly reduce
        List<AttackOnSquareStruct> toRemove = new ArrayList<>();
        for (AttackOnSquareStruct move : legalMoves) {
            //If this move isn't of the same movement axis as the pin, it is no longer a legal move
            //Checking only first element from pins list because all pins should have the same pin direction
            if (move.attackDir != pins.get(0).attackDirOfPin) toRemove.add(move);
        }
        //Remove the invalidated moves from the list of legal moves
        legalMoves.removeAll(toRemove);

        //After the invalidated moves have been removed, check to see if the piece still has legal moves left
        return hasMoves();
    }
    public boolean reduceMovesDueToCheck(List<String> squaresFacilitatingCheck) {
        //OVERVIEW: Return whether piece has any legal moves

        //Find and remove all the moves that don't let this piece intercept check
        List<AttackOnSquareStruct> toRemove = new ArrayList<>();
        for (AttackOnSquareStruct legalMove : legalMoves) {
            if (!squaresFacilitatingCheck.contains(legalMove.attackedSquareName)) toRemove.add(legalMove);
        }
        legalMoves.removeAll(toRemove);

        //After the invalidated moves have been removed, check to see if the piece still has legal moves left
        return hasMoves();
    }
    public void setPin(Piece pinningPiece, AttackDir attackDirOfPin) {
        pins.add(new PinOnPieceStruct(pinningPiece, attackDirOfPin));
    }
    public void clearAllMoves() {
        legalMoves.clear();
    }
    public void clearAllPins() {
        pins.clear();
    }

    protected List<MoveCalcResultStruct> calculateSquarePreviewResults() {
        logger.trace("calculateSquarePreviewResults() runs");
        //Default value
        return new ArrayList<>();
    }
    protected List<MoveCalcResultStruct> moveOrCaptureInALine(Direction dir, AttackDir attackDir) {
        /* OVERVIEW:
            -MOVE_OR_CAPTURE
            -Calculate moves in a line.
            -If an opposite colour piece is hit, keep calculating to check for pin
            -If a same colour piece or the edge of the board is hit, stop calculating further
        */
        AttackType attackType = AttackType.MOVE_OR_CAPTURE;

        //For direction and magnitude
        dir.resetMagnitude();
        int magnitude = 1;
        //For collecting results for each square
        List<MoveCalcResultStruct> results = new ArrayList<>();
        //Once King is hit, no moves past are "legal", but must still be calculated
        boolean notHitKing = true;

        while (true) {
            dir.setMagnitude(magnitude++);
            SquarePreviewStruct preview = previewRelativeSquare(dir.x, dir.y);
            SqrStat status = preview.squareStatus;
            String squareName = preview.squareName;
            Piece piece = preview.piece;
            //Guard clause for hitting edge of the board or same coloured piece
            if (status == SqrStat.NO_SQUARE || colour == preview.pieceColour) break;
            //Guard clause for opposite colour piece being hit
            //Continue calculating past a King because not possible to hit a King during your turn (only opponent's)
            if (status != SqrStat.EMPTY) {
                if (piece.getType() == PieceType.KING) {
                    assert(notHitKing);
                    notHitKing = false;
                    declareResultAsOnCheckPath(results);
                    results.add(new MoveCalcResultStruct((King)piece, squareName, attackType, attackDir));
                    //Even after King is hit, other squares can be still be attacked, so continue
                    continue;
                }
                else {
                    //Add the attack on this square
                    results.add(new MoveCalcResultStruct(null, squareName, attackType,
                            attackDir, notHitKing));
                    //If King has not been hit yet then this piece is pinnable
                    if (notHitKing) continueToFindPin(piece, dir, attackDir);
                    //No attacks can be recorded past a piece so break
                    break;
                }
            }
            //If code reaches this point, it means the square is empty
            results.add(new MoveCalcResultStruct(null, squareName, attackType, attackDir, notHitKing));
        }
        return results;
    }
    protected void declareResultAsOnCheckPath(List<MoveCalcResultStruct> results) {
        //Declares each result as being part of the line of attack from the checking piece to the King
        for (MoveCalcResultStruct result : results) {
            result.isOnPathOfCheck = true;
        }
    }
    protected void continueToFindPin(Piece pinnablePiece, Direction dir, AttackDir attackDir) {
        int i = dir.getMagnitude();
        while (true) {
            dir.setMagnitude(++i);
            SquarePreviewStruct preview = previewRelativeSquare(dir.x, dir.y);
            SqrStat status = preview.squareStatus;
            //Guard clause for hitting the edge of the board
            if (status == SqrStat.NO_SQUARE) break;
            //Guard clause for hitting piece that isn't opposite-colour king
            if (status != SqrStat.EMPTY && (colour == preview.pieceColour || status == SqrStat.NON_KING_PIECE)) break;
            //Guard clause for hitting opposite-colour king, meaning pinnable piece is pinned
            if (status != SqrStat.EMPTY) {
                pinnablePiece.setPin(this, attackDir);
                break;
            }
            //If code reaches this point, it means this square is empty (therefore do nothing and keep the loop going)
        }
    }
    protected SquarePreviewStruct previewRelativeSquare(int x, int y) {
        String squareName = location.findRelativeByXAndY(x, y);
        if (squareName == null) {
            return new SquarePreviewStruct(SqrStat.NO_SQUARE, null, null, null);
        }
        Square square = boardManager.getSquare(squareName);
        Piece pieceAtSquare = square.getContainedPiece();
        if (pieceAtSquare == null) {
            if (square.getShadowedPawn() != null) {
                return new SquarePreviewStruct(SqrStat.EMPTY, squareName, null, null, square.getShadowedPawn());
            }
            else {
                return new SquarePreviewStruct(SqrStat.EMPTY, squareName, null, null);
            }
        }
        if (pieceAtSquare.getType() == PieceType.KING) {
            return new SquarePreviewStruct(SqrStat.KING, squareName, pieceAtSquare, pieceAtSquare.getColour());
        }
        else {
            return new SquarePreviewStruct(SqrStat.NON_KING_PIECE,
                    squareName, pieceAtSquare, pieceAtSquare.getColour());
        }
    }

    @Override
    public String toString() {
        return "Piece{" +
                "name='" + type + '\'' +
                ", colour=" + colour +
                ", location='" + location + '\'' +
                ", isBoard=" + (boardManager != null) +
                '}';
    }
}