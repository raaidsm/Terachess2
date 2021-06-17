package raaidsm.spring.test.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raaidsm.spring.test.models.managers.BoardManager;
import raaidsm.spring.test.models.moves_and_attacks.AttackOnSquareStruct;
import raaidsm.spring.test.models.moves_and_attacks.MoveCalcResultsStruct;
import raaidsm.spring.test.models.moves_and_attacks.AttackType;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.pieces.King;
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
    protected boolean isPinned;
    protected Piece pinningPiece;
    protected List<String> promotion;
    protected BoardManager boardManager;
    //endregion

    public Piece() {}
    public Piece(PieceType type, Colour colour, String location) {
        this.type = type;
        this.colour = colour;
        this.location = new Point(location);
        this.legalMoves = new ArrayList<>();
        this.isPinned = false;
        this.pinningPiece = null;
        this.promotion = new ArrayList<>();
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
    public void setLegalMoves(List<AttackOnSquareStruct> legalMoves) {
        this.legalMoves = legalMoves;
    }
    public boolean isPinned() {
        return isPinned;
    }
    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }
    public Piece getPinningPiece() {
        return pinningPiece;
    }
    public void setPinningPiece(Piece pinningPiece) {
        this.pinningPiece = pinningPiece;
    }
    public List<String> getPromotion() {
        return promotion;
    }
    public void setPromotion(List<String> promotion) {
        this.promotion = promotion;
    }
    public BoardManager getBoardManager() {
        return boardManager;
    }
    public void setBoardManager(BoardManager boardManager) {
        this.boardManager = boardManager;
    }
    //endregion

    public MoveCalcResultsStruct calculateMoves() {
        //OVERVIEW: Return checked king (null if none) and whether piece has any legal moves
        logger.trace("calculateMoves() runs");

        //region Variables to Return
        King checkedKing = null;
        AttackType checkAttackType = null;
        boolean hasMoves = false;
        //endregion

        clearAllMoves();
        List<MoveCalcResultsStruct> results = calculateSquarePreviewResults();
        for (MoveCalcResultsStruct result : results) {
            AttackType attackType = result.attackType;
            String squareName = result.squareName;
            if (result.hasMoves) {
                //Change variables for check
                hasMoves = true;
                checkedKing = result.checkedKing;
                if (checkedKing != null) checkAttackType = attackType;
                //Add result to legal moves
                legalMoves.add(new AttackOnSquareStruct(this, attackType, squareName));
            }
            //Record attack in the square attacked
            Square squareAttacked = boardManager.getSquare(squareName);
            if (attackType != AttackType.ONLY_MOVE) squareAttacked.setAttack(this, colour);
        }
        //Returning squareName as null because many different squares are possibly attacked
        return new MoveCalcResultsStruct(checkedKing, null, checkAttackType, hasMoves);
    }
    public MoveCalcResultsStruct reduceMovesDueToPin() {
        //OVERVIEW:
        //Return whether piece has any legal moves
        //TODO: For now, returning default value
        return new MoveCalcResultsStruct(null, null, null, true);
    }
    public MoveCalcResultsStruct reduceMovesDueToCheck() {
        //OVERVIEW:
        //Return whether piece has any legal moves
        //TODO: For now, returning default value
        return new MoveCalcResultsStruct(null, null, null, true);
    }
    public void clearAllMoves() {
        legalMoves.clear();
    }

    protected List<MoveCalcResultsStruct> calculateSquarePreviewResults() {
        logger.trace("calculateSquarePreviewResults() runs");
        //Default value
        return new ArrayList<>();
    }
    protected List<MoveCalcResultsStruct> moveOrCaptureInALine(Direction dir) {
        /* OVERVIEW:
            -MOVE_OR_CAPTURE
            -Calculate moves in a line.
            -If an opposite colour piece is hit, keep calculating to check for pin
            -If a same colour piece or the edge of the board is hit, stop calculating further
        */
        AttackType attackType = AttackType.MOVE_OR_CAPTURE;

        dir.resetMagnitude();
        List<MoveCalcResultsStruct> results = new ArrayList<>();
        int i = 1;
        boolean notHitKing = true;      //Once King is hit, no moves past are "legal", but must still be calculated
        boolean pinnablePieceHit = false;
        boolean unpinnablePieceHit = false;
        boolean edgeOfBoardHit = false;
        Piece pinnablePiece = null;
        while (!pinnablePieceHit && !unpinnablePieceHit && !edgeOfBoardHit) {
            dir.setMagnitude(i++);
            SquarePreviewStruct preview = previewRelativeSquare(dir.x, dir.y);
            SqrStat status = preview.squareStatus;
            String squareName = preview.squareName;
            Piece piece = preview.piece;
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
            //Continue calculating past a King because it is not possible to hit a King during your turn
            if (status != SqrStat.EMPTY) {
                if (piece.getType() == PieceType.KING) {
                    assert(notHitKing);
                    notHitKing = false;
                    results.add(new MoveCalcResultsStruct((King)piece, squareName, attackType, false));
                }
                else {
                    pinnablePieceHit = true;
                    pinnablePiece = piece;
                    results.add(new MoveCalcResultsStruct(null, squareName, attackType, notHitKing));
                }
                continue;
            }
            //If code reaches this point, it means the square is empty
            results.add(new MoveCalcResultsStruct(null, squareName, attackType, notHitKing));
        }

        if (pinnablePieceHit) continueToFindPin(pinnablePiece, dir);
        return results;
    }
    protected void continueToFindPin(Piece pinnablePiece, Direction dir) {
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
                pinnablePiece.setPinned(true);
                pinnablePiece.setPinningPiece(this);
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
        Piece pieceAtSquare = boardManager.getSquare(squareName).getContainedPiece();
        if (pieceAtSquare == null) {
            return new SquarePreviewStruct(SqrStat.EMPTY, squareName, null, null);
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