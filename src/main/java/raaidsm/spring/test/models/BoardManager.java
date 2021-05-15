package raaidsm.spring.test.models;

import raaidsm.spring.test.models.pieces.*;
import raaidsm.spring.test.models.utils.AttackType;
import raaidsm.spring.test.models.utils.TurnManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

//Contains methods for manipulating the board
public class BoardManager {
    //region Field Variables
    //region Constants
    private final int boardLength = 8;
    private final char[] letters = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H' };
    //endregion
    //region Board Tracking
    public HashMap<String, Square> board;
    private List<Piece> whitePieces;
    private List<Piece> blackPieces;
    private HashMap<String, List<Piece>> pieceListsByColour;
    private Piece[] kings = new Piece[2];
    private TurnManager turnManager;
    //endregion
    //region Check
    private boolean isCheck;
    private List<Piece> checkingPieces;
    private List<AttackType> checkTypes;
    private Piece checkedKing;
    //endregion
    //endregion

    public BoardManager() {
        //region Initialize Board With Pieces
        this.board = new HashMap<>(Map.ofEntries(
                //Add all the initial pieces starting from the top left

                //region Black first rank
                entry("A8", new Square(new Rook("rook", "black", "A8"))),
                entry("B8", new Square(new Knight("knight", "black", "B8"))),
                entry("C8", new Square(new Bishop("bishop", "black", "C8"))),
                entry("D8", new Square(new Queen("queen", "black", "D8"))),
                entry("E8", new Square(new King("king", "black", "E8"))),
                entry("F8", new Square(new Bishop("bishop", "black", "F8"))),
                entry("G8", new Square(new Knight("knight", "black", "G8"))),
                entry("H8", new Square(new Rook("rook", "black", "H8"))),
                //endregion
                //region Black second rank
                entry("A7", new Square(new Pawn("pawn", "black", "A7"))),
                entry("B7", new Square(new Pawn("pawn", "black", "B7"))),
                entry("C7", new Square(new Pawn("pawn", "black", "C7"))),
                entry("D7", new Square(new Pawn("pawn", "black", "D7"))),
                entry("E7", new Square(new Pawn("pawn", "black", "E7"))),
                entry("F7", new Square(new Pawn("pawn", "black", "F7"))),
                entry("G7", new Square(new Pawn("pawn", "black", "G7"))),
                entry("H7", new Square(new Pawn("pawn", "black", "H7"))),
                //endregion
                //region White second rank
                entry("A2", new Square(new Pawn("pawn", "white", "A2"))),
                entry("B2", new Square(new Pawn("pawn", "white", "B2"))),
                entry("C2", new Square(new Pawn("pawn", "white", "C2"))),
                entry("D2", new Square(new Pawn("pawn", "white", "D2"))),
                entry("E2", new Square(new Pawn("pawn", "white", "E2"))),
                entry("F2", new Square(new Pawn("pawn", "white", "F2"))),
                entry("G2", new Square(new Pawn("pawn", "white", "G2"))),
                entry("H2", new Square(new Pawn("pawn", "white", "H2"))),
                //endregion
                //region White first rank
                entry("A1", new Square(new Rook("rook", "white", "A1"))),
                entry("B1", new Square(new Knight("knight", "white", "B1"))),
                entry("C1", new Square(new Bishop("bishop", "white", "C1"))),
                entry("D1", new Square(new Queen("queen", "white", "D1"))),
                entry("E1", new Square(new King("king", "white", "E1"))),
                entry("F1", new Square(new Bishop("bishop", "white", "F1"))),
                entry("G1", new Square(new Knight("knight", "white", "G1"))),
                entry("H1", new Square(new Rook("rook", "white", "H1")))
                //endregion
        ));
        //endregion
        //region Initialize Coloured Piece Lists
        this.whitePieces = new ArrayList<>();
        this.blackPieces = new ArrayList<>();
        this.pieceListsByColour = new HashMap<>(Map.ofEntries(
                entry("white", whitePieces),
                entry("black", blackPieces)
        ));
        //endregion
        //region Set Board Field
        //Set board field for each piece and add it to the designated coloured list of pieces
        board.forEach((coordinate, square) -> {
            Piece piece = square.containedPiece;
            String pieceColour = piece.getColour();
            piece.setBoard(board);
            pieceListsByColour.get(pieceColour).add(piece);
        });
        //endregion
        //region Add Empty Squares
        //Add the empty squares with no pieces
        for (int y = boardLength - 2; 0 < y; y--) {
            for (int x = 0; x < boardLength; x++) {
                board.put(letters[x] + String.valueOf(y), new Square(null));
            }
        }
        //endregion
        //region Initialize Other Fields
        this.kings[0] = board.get("E1").containedPiece;
        this.kings[1] = board.get("E8").containedPiece;
        this.turnManager = new TurnManager();
        this.isCheck = false;
        this.checkingPieces = new ArrayList<>();
        this.checkTypes = new ArrayList<>();
        this.checkedKing = null;
        //endregion
    }

    public boolean isCheck() {
        return isCheck;
    }
    public void setCheck(boolean check) {
        isCheck = check;
    }

    public void makeMove(String firstSquare, String secondSquare) {
        //Take piece-to-move off of first square
        Piece pieceToMove = board.get(firstSquare).containedPiece;
        board.put(firstSquare, null);
        //Record and remove piece-to-move-to, if exists
        Piece pieceToMoveTo = board.get(secondSquare).containedPiece;
        if (pieceToMoveTo != null) {
            pieceToMoveTo.setLocation(null);
            board.get(secondSquare).containedPiece = null;
            String pieceColour = pieceToMoveTo.getColour();
            pieceListsByColour.get(pieceColour).remove(pieceToMoveTo);
            //TODO: Record the captured piece
        }
        //Move piece-to-move to second square
        board.get(secondSquare).containedPiece = pieceToMove;
        pieceToMove.setLocation(secondSquare);
        //Move has been made, now calculate all legal moves
        calculateAllLegalMoves();
    }
    private void calculateAllLegalMoves() {
        /* OVERVIEW:
        0) If double check, only calculate moves for king
        1) Calculate moves for each piece:
            Determine if piece delivers check (and set isCheck)
            Determine if piece pins another piece (and set isPinned for pinned piece)
        Reduce moves for each piece according to board-state stipulations:
            2) For each piece, reduce if piece is pinned
            3) For each piece, reduce if check and piece is of same colour as checked king
        */
        List<Piece> pieces = pieceListsByColour.get(turnManager.getColour().toString());
        //0)
        if (1 < checkingPieces.size()) {
            pieces.forEach(piece -> {
                if (piece.getName().equals("king")) piece.calculateMoves();
                else piece.clearAllMoves();
            });
            return;
        }
        //1)
        pieces.forEach(piece -> checkedKing = piece.calculateMoves().kingChecked);
        //2)
        pieces.forEach(piece -> {
            if (!piece.getName().equals("king")) piece.reduceMovesDueToPin();
        });
        //3)
        pieces.forEach(piece -> {
            if (!piece.getName().equals("king")) piece.reduceMovesDueToCheck();
        });
    }
}