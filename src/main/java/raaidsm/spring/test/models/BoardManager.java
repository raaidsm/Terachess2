package raaidsm.spring.test.models;

import raaidsm.spring.test.models.pieces.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

//Contains methods for manipulating the board
public class BoardManager {
    public HashMap<String, Piece> board;
    private Piece[] kings = new Piece[2];
    private boolean isCheck;
    private List<Piece> checkingPieces;
    private Piece checkedKing;

    public BoardManager() {
        //Initialize board
        this.board = new HashMap<>(Map.ofEntries(
                //Add all the initial pieces starting from the top left

                //region Black first rank
                entry("A8", new Rook("rook", "black", "A8")),
                entry("B8", new Knight("knight", "black", "B8")),
                entry("C8", new Bishop("bishop", "black", "C8")),
                entry("D8", new Queen("queen", "black", "D8")),
                entry("E8", new King("king", "black", "E8")),
                entry("F8", new Bishop("bishop", "black", "F8")),
                entry("G8", new Knight("knight", "black", "G8")),
                entry("H8", new Rook("rook", "black", "H8")),
                //endregion
                //region Black second rank
                entry("A7", new Pawn("pawn", "black", "A7")),
                entry("B7", new Pawn("pawn", "black", "B7")),
                entry("C7", new Pawn("pawn", "black", "C7")),
                entry("D7", new Pawn("pawn", "black", "D7")),
                entry("E7", new Pawn("pawn", "black", "E7")),
                entry("F7", new Pawn("pawn", "black", "F7")),
                entry("G7", new Pawn("pawn", "black", "G7")),
                entry("H7", new Pawn("pawn", "black", "H7")),
                //endregion
                //region White second rank
                entry("A2", new Pawn("pawn", "white", "A2")),
                entry("B2", new Pawn("pawn", "white", "B2")),
                entry("C2", new Pawn("pawn", "white", "C2")),
                entry("D2", new Pawn("pawn", "white", "D2")),
                entry("E2", new Pawn("pawn", "white", "E2")),
                entry("F2", new Pawn("pawn", "white", "F2")),
                entry("G2", new Pawn("pawn", "white", "G2")),
                entry("H2", new Pawn("pawn", "white", "H2")),
                //endregion
                //region White first rank
                entry("A1", new Rook("rook", "white", "A1")),
                entry("B1", new Knight("knight", "white", "B1")),
                entry("C1", new Bishop("bishop", "white", "C1")),
                entry("D1", new Queen("queen", "white", "D1")),
                entry("E1", new King("king", "white", "E1")),
                entry("F1", new Bishop("bishop", "white", "F1")),
                entry("G1", new Knight("knight", "white", "G1")),
                entry("H1", new Rook("rook", "white", "H1"))
                //endregion
        ));
        //Set board field for each piece in the board
        board.forEach((coordinate, piece) -> piece.setBoard(board));
        //Initialize other fields
        this.kings[0] = board.get("E1");
        this.kings[1] = board.get("E8");
        this.isCheck = false;
        this.checkingPieces = new ArrayList<>();
        this.checkedKing = null;
    }

    public boolean isCheck() {
        return isCheck;
    }
    public void setCheck(boolean check) {
        isCheck = check;
    }

    public void makeMove(String firstSquare, String secondSquare) {
        //Take piece off first square
        Piece pieceToMove = board.get(firstSquare);
        board.put(firstSquare, null);
        //Act on piece currently at second square if exists
        Piece pieceToMoveTo = board.get(secondSquare);
        if (pieceToMoveTo != null) {
            pieceToMoveTo.setLocation(null);
            //TODO: Record the captured piece
        }
        //Move piece to second square
        board.put(secondSquare, pieceToMove);
        pieceToMove.setLocation(secondSquare);
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
        //0)
        if (1 < checkingPieces.size()) {
            board.forEach((coordinate, piece) -> {
                if (piece.getName().equals("king")) piece.calculateMoves();
                else piece.clearAllMoves();
            });
            return;
        }
        //1)
        board.forEach((coordinate, piece) -> {
            checkedKing = piece.calculateMoves();
        });
        //2)
        board.forEach((coordinate, piece) -> {
            if (!piece.getName().equals("king") && piece.isPinned()) piece.reduceMovesDueToPin();
        });
        //3)
        board.forEach((coordinate, piece) -> {
            if (!piece.getName().equals("king") && piece.getColour().equals(checkedKing.getColour())) {
                piece.reduceMovesDueToCheck();
            }
        });
    }
}