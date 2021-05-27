package raaidsm.spring.test.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raaidsm.spring.test.models.piece_properties.AttackType;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.pieces.King;
import raaidsm.spring.test.models.utils.*;
import raaidsm.spring.test.models.piece_properties.PieceType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Piece implements Serializable {
    //region Field Variables
    private final Logger logger = LoggerFactory.getLogger(Piece.class);
    protected PieceType type;
    protected Colour colour;
    protected Point location;
    protected List<AttackingPieceStruct> legalMoves;
    protected boolean isPinned;
    protected Piece pinningPiece;
    protected List<String> promotion;
    protected HashMap<String, Square> board;
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
        this.board = null;
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
    public List<AttackingPieceStruct> getLegalMoves() {
        return legalMoves;
    }
    public void setLegalMoves(List<AttackingPieceStruct> legalMoves) {
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
    public HashMap<String, Square> getBoard() {
        return board;
    }
    public void setBoard(HashMap<String, Square> board) {
        this.board = board;
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

        List<MoveCalcResultsStruct> results = calculateSquarePreviewResults();
        for (MoveCalcResultsStruct result : results) {
            hasMoves = true;
            if (result.hasMoves) {
                checkedKing = result.checkedKing;
                AttackType attackType = result.attackType;
                String squareName = result.squareName;
                if (checkedKing != null) checkAttackType = attackType;
                //Add result to legal moves
                legalMoves.add(new AttackingPieceStruct(this, attackType, squareName));
                //Record legal move in the square attacked
                Square squareAttacked = board.get(squareName);
                squareAttacked.piecesAttacking.add(this);
                squareAttacked.attackingColours.put(colour.toString(), Boolean.valueOf("true"));
            }
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
        List<MoveCalcResultsStruct> results = new ArrayList<>();
        int i = 1;
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
            //Guard clause for opposite colour piece being hit (piece being opposite colour can be assumed)
            if (status != SqrStat.EMPTY) {
                if (piece.getType() == PieceType.KING) {
                    unpinnablePieceHit = true;
                    results.add(new MoveCalcResultsStruct((King)piece, squareName, attackType, true));
                }
                else {
                    pinnablePieceHit = true;
                    pinnablePiece = piece;
                    results.add(new MoveCalcResultsStruct(null, squareName, attackType, true));
                }
                continue;
            }
            //If code reaches this point, it means the square is empty
            results.add(new MoveCalcResultsStruct(null, squareName, attackType, true));
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
        Piece pieceAtSquare = board.get(squareName).containedPiece;
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
                ", isBoard=" + (board != null) +
                '}';
    }
}