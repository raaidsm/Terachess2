package raaidsm.spring.test.models;

import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.utils.AttackingPieceStruct;
import raaidsm.spring.test.models.utils.MoveCalcResultsStruct;
import raaidsm.spring.test.models.piece_properties.PieceType;
import raaidsm.spring.test.models.utils.SquarePreviewStruct;
import raaidsm.spring.test.models.utils.SquareStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Piece implements Serializable {
    protected PieceType type;
    protected Colour colour;
    protected Point location;
    protected List<AttackingPieceStruct> legalMoves;
    protected boolean isPinned;
    protected Piece pinningPiece;
    protected List<String> promotion;
    protected HashMap<String, Square> board;

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

    public MoveCalcResultsStruct calculateMoves() {
        //OVERVIEW:
        //Return checked king (null if none) and whether piece has any legal moves
        //TODO: For now, returning default value
        return new MoveCalcResultsStruct(null, null, false);
    }
    public MoveCalcResultsStruct reduceMovesDueToPin() {
        //OVERVIEW:
        //Return whether piece has any legal moves
        //TODO: For now, returning default value
        return new MoveCalcResultsStruct(null, null, false);
    }
    public MoveCalcResultsStruct reduceMovesDueToCheck() {
        //OVERVIEW:
        //Return whether piece has any legal moves
        //TODO: For now, returning default value
        return new MoveCalcResultsStruct(null, null, false);
    }
    public void clearAllMoves() {}

    @Override
    public String toString() {
        return "Piece{" +
                "name='" + type + '\'' +
                ", colour=" + colour +
                ", location='" + location + '\'' +
                ", isBoard=" + (board != null) +
                '}';
    }
    private SquarePreviewStruct previewRelativeSquare(int x, int y) {
        String squareName = location.findRelativeByXAndY(x, y);
        if (squareName == null) return new SquarePreviewStruct(SquareStatus.NO_SQUARE, null, null);
        Piece pieceAtSquare = board.get(squareName).containedPiece;
        if (pieceAtSquare == null) return new SquarePreviewStruct(SquareStatus.EMPTY, null, null);
        if (pieceAtSquare.getType() == PieceType.KING) {
            return new SquarePreviewStruct(SquareStatus.KING, pieceAtSquare, pieceAtSquare.getColour());
        }
        else {
            return new SquarePreviewStruct(SquareStatus.NON_KING_PIECE, pieceAtSquare, pieceAtSquare.getColour());
        }
    }
}