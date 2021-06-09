package raaidsm.spring.test.models.managers;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.Square;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.piece_properties.PieceType;
import raaidsm.spring.test.models.pieces.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class BoardManager {
    private final HashMap<String, Square> board;
    private final int boardLength = 8;
    private final char[] letters = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H' };
    private final HashMap<Colour, List<Piece>> pieceListsByColour;

    public BoardManager() {
        pieceListsByColour = new HashMap<>(Map.ofEntries(
                entry(Colour.WHITE, new ArrayList<>()), entry(Colour.BLACK, new ArrayList<>())
        ));
        //Initialize board with pieces
        board = new HashMap<>(Map.ofEntries(
                //region Piece Initialization
                //Add all the initial pieces starting from the top left

                //region Black first rank
                entry("A8", new Square(new Rook(PieceType.ROOK, Colour.BLACK, "A8"))),
                entry("B8", new Square(new Knight(PieceType.KNIGHT, Colour.BLACK, "B8"))),
                entry("C8", new Square(new Bishop(PieceType.BISHOP, Colour.BLACK, "C8"))),
                entry("D8", new Square(new Queen(PieceType.QUEEN, Colour.BLACK, "D8"))),
                entry("E8", new Square(new King(PieceType.KING, Colour.BLACK, "E8"))),
                entry("F8", new Square(new Bishop(PieceType.BISHOP, Colour.BLACK, "F8"))),
                entry("G8", new Square(new Knight(PieceType.KNIGHT, Colour.BLACK, "G8"))),
                entry("H8", new Square(new Rook(PieceType.ROOK, Colour.BLACK, "H8"))),
                //endregion
                //region Black second rank
                entry("A7", new Square(new Pawn(PieceType.PAWN, Colour.BLACK, "A7"))),
                entry("B7", new Square(new Pawn(PieceType.PAWN, Colour.BLACK, "B7"))),
                entry("C7", new Square(new Pawn(PieceType.PAWN, Colour.BLACK, "C7"))),
                entry("D7", new Square(new Pawn(PieceType.PAWN, Colour.BLACK, "D7"))),
                entry("E7", new Square(new Pawn(PieceType.PAWN, Colour.BLACK, "E7"))),
                entry("F7", new Square(new Pawn(PieceType.PAWN, Colour.BLACK, "F7"))),
                entry("G7", new Square(new Pawn(PieceType.PAWN, Colour.BLACK, "G7"))),
                entry("H7", new Square(new Pawn(PieceType.PAWN, Colour.BLACK, "H7"))),
                //endregion
                //region White second rank
                entry("A2", new Square(new Pawn(PieceType.PAWN, Colour.WHITE, "A2"))),
                entry("B2", new Square(new Pawn(PieceType.PAWN, Colour.WHITE, "B2"))),
                entry("C2", new Square(new Pawn(PieceType.PAWN, Colour.WHITE, "C2"))),
                entry("D2", new Square(new Pawn(PieceType.PAWN, Colour.WHITE, "D2"))),
                entry("E2", new Square(new Pawn(PieceType.PAWN, Colour.WHITE, "E2"))),
                entry("F2", new Square(new Pawn(PieceType.PAWN, Colour.WHITE, "F2"))),
                entry("G2", new Square(new Pawn(PieceType.PAWN, Colour.WHITE, "G2"))),
                entry("H2", new Square(new Pawn(PieceType.PAWN, Colour.WHITE, "H2"))),
                //endregion
                //region White first rank
                entry("A1", new Square(new Rook(PieceType.ROOK, Colour.WHITE, "A1"))),
                entry("B1", new Square(new Knight(PieceType.KNIGHT, Colour.WHITE, "B1"))),
                entry("C1", new Square(new Bishop(PieceType.BISHOP, Colour.WHITE, "C1"))),
                entry("D1", new Square(new Queen(PieceType.QUEEN, Colour.WHITE, "D1"))),
                entry("E1", new Square(new King(PieceType.KING, Colour.WHITE, "E1"))),
                entry("F1", new Square(new Bishop(PieceType.BISHOP, Colour.WHITE, "F1"))),
                entry("G1", new Square(new Knight(PieceType.KNIGHT, Colour.WHITE, "G1"))),
                entry("H1", new Square(new Rook(PieceType.ROOK, Colour.WHITE, "H1")))
                //endregion
                //endregion
        ));

        addBoardToPieces();
        addEmptySquares();
    }

    //region Methods for inserting private information from this class into another class
    public CheckManager createCheckManager() {
        King whiteKing = (King)board.get("E1").containedPiece;
        King blackKing = (King)board.get("E8").containedPiece;
        return new CheckManager(whiteKing, blackKing);
    }
    public void integrateBoardIntoPiece(Piece piece) {
        piece.setBoard(board);
    }
    //endregion

    //region Getters and Setters
    public Square getSquare(String squareName) {
        return board.get(squareName);
    }
    public List<Piece> getPieceList(Colour pieceColour) {
        return pieceListsByColour.get(pieceColour);
    }
    public List<Square> getAllSquaresFromBoard() {
        List<Square> allSquares = new ArrayList<>();
        board.forEach((coordinate, square) -> {
            allSquares.add(square);
        });
        return allSquares;
    }
    //endregion

    private void addBoardToPieces() {
        board.forEach((coordinate, square) -> {
            Piece piece = square.containedPiece;
            Colour pieceColour = piece.getColour();
            piece.setBoard(board);
            pieceListsByColour.get(pieceColour).add(piece);
        });
    }
    private void addEmptySquares() {
        for (int y = boardLength - 2; 2 < y; y--) {
            for (int x = 0; x < boardLength; x++) {
                board.put(letters[x] + String.valueOf(y), new Square(null));
            }
        }
    }
}