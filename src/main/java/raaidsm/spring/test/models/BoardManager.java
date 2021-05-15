package raaidsm.spring.test.models;

import raaidsm.spring.test.models.pieces.*;
import raaidsm.spring.test.models.utils.AttackType;
import raaidsm.spring.test.models.utils.Colour;
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
    private HashMap<Colour, List<Piece>> pieceListsByColour;
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
                entry("A8", new Square(new Rook("rook", Colour.BLACK, "A8"))),
                entry("B8", new Square(new Knight("knight", Colour.BLACK, "B8"))),
                entry("C8", new Square(new Bishop("bishop", Colour.BLACK, "C8"))),
                entry("D8", new Square(new Queen("queen", Colour.BLACK, "D8"))),
                entry("E8", new Square(new King("king", Colour.BLACK, "E8"))),
                entry("F8", new Square(new Bishop("bishop", Colour.BLACK, "F8"))),
                entry("G8", new Square(new Knight("knight", Colour.BLACK, "G8"))),
                entry("H8", new Square(new Rook("rook", Colour.BLACK, "H8"))),
                //endregion
                //region Black second rank
                entry("A7", new Square(new Pawn("pawn", Colour.BLACK, "A7"))),
                entry("B7", new Square(new Pawn("pawn", Colour.BLACK, "B7"))),
                entry("C7", new Square(new Pawn("pawn", Colour.BLACK, "C7"))),
                entry("D7", new Square(new Pawn("pawn", Colour.BLACK, "D7"))),
                entry("E7", new Square(new Pawn("pawn", Colour.BLACK, "E7"))),
                entry("F7", new Square(new Pawn("pawn", Colour.BLACK, "F7"))),
                entry("G7", new Square(new Pawn("pawn", Colour.BLACK, "G7"))),
                entry("H7", new Square(new Pawn("pawn", Colour.BLACK, "H7"))),
                //endregion
                //region White second rank
                entry("A2", new Square(new Pawn("pawn", Colour.WHITE, "A2"))),
                entry("B2", new Square(new Pawn("pawn", Colour.WHITE, "B2"))),
                entry("C2", new Square(new Pawn("pawn", Colour.WHITE, "C2"))),
                entry("D2", new Square(new Pawn("pawn", Colour.WHITE, "D2"))),
                entry("E2", new Square(new Pawn("pawn", Colour.WHITE, "E2"))),
                entry("F2", new Square(new Pawn("pawn", Colour.WHITE, "F2"))),
                entry("G2", new Square(new Pawn("pawn", Colour.WHITE, "G2"))),
                entry("H2", new Square(new Pawn("pawn", Colour.WHITE, "H2"))),
                //endregion
                //region White first rank
                entry("A1", new Square(new Rook("rook", Colour.WHITE, "A1"))),
                entry("B1", new Square(new Knight("knight", Colour.WHITE, "B1"))),
                entry("C1", new Square(new Bishop("bishop", Colour.WHITE, "C1"))),
                entry("D1", new Square(new Queen("queen", Colour.WHITE, "D1"))),
                entry("E1", new Square(new King("king", Colour.WHITE, "E1"))),
                entry("F1", new Square(new Bishop("bishop", Colour.WHITE, "F1"))),
                entry("G1", new Square(new Knight("knight", Colour.WHITE, "G1"))),
                entry("H1", new Square(new Rook("rook", Colour.WHITE, "H1")))
                //endregion
        ));
        //endregion
        //region Initialize Coloured Piece Lists
        this.whitePieces = new ArrayList<>();
        this.blackPieces = new ArrayList<>();
        this.pieceListsByColour = new HashMap<>(Map.ofEntries(
                entry(Colour.WHITE, whitePieces),
                entry(Colour.BLACK, blackPieces)
        ));
        //endregion
        //region Set Board Field
        //Set board field for each piece and add it to the designated coloured list of pieces
        board.forEach((coordinate, square) -> {
            Piece piece = square.containedPiece;
            Colour pieceColour = piece.getColour();
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
            Colour pieceColour = pieceToMoveTo.getColour();
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
        List<Piece> pieces = pieceListsByColour.get(turnManager.getColour());
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