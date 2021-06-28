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
                entry("A8", new Square("A8", new Rook(PieceType.ROOK, Colour.BLACK, "A8"))),
                entry("B8", new Square("B8", new Knight(PieceType.KNIGHT, Colour.BLACK, "B8"))),
                entry("C8", new Square("C8", new Bishop(PieceType.BISHOP, Colour.BLACK, "C8"))),
                entry("D8", new Square("D8", new Queen(PieceType.QUEEN, Colour.BLACK, "D8"))),
                entry("E8", new Square("E8", new King(PieceType.KING, Colour.BLACK, "E8"))),
                entry("F8", new Square("F8", new Bishop(PieceType.BISHOP, Colour.BLACK, "F8"))),
                entry("G8", new Square("G8", new Knight(PieceType.KNIGHT, Colour.BLACK, "G8"))),
                entry("H8", new Square("H8", new Rook(PieceType.ROOK, Colour.BLACK, "H8"))),
                //endregion
                //region Black second rank
                entry("A7", new Square("A7", new Pawn(PieceType.PAWN, Colour.BLACK, "A7"))),
                entry("B7", new Square("B7", new Pawn(PieceType.PAWN, Colour.BLACK, "B7"))),
                entry("C7", new Square("C7", new Pawn(PieceType.PAWN, Colour.BLACK, "C7"))),
                entry("D7", new Square("D7", new Pawn(PieceType.PAWN, Colour.BLACK, "D7"))),
                entry("E7", new Square("E7", new Pawn(PieceType.PAWN, Colour.BLACK, "E7"))),
                entry("F7", new Square("F7", new Pawn(PieceType.PAWN, Colour.BLACK, "F7"))),
                entry("G7", new Square("G7", new Pawn(PieceType.PAWN, Colour.BLACK, "G7"))),
                entry("H7", new Square("H7", new Pawn(PieceType.PAWN, Colour.BLACK, "H7"))),
                //endregion
                //region White second rank
                entry("A2", new Square("A2", new Pawn(PieceType.PAWN, Colour.WHITE, "A2"))),
                entry("B2", new Square("B2", new Pawn(PieceType.PAWN, Colour.WHITE, "B2"))),
                entry("C2", new Square("C2", new Pawn(PieceType.PAWN, Colour.WHITE, "C2"))),
                entry("D2", new Square("D2", new Pawn(PieceType.PAWN, Colour.WHITE, "D2"))),
                entry("E2", new Square("E2", new Pawn(PieceType.PAWN, Colour.WHITE, "E2"))),
                entry("F2", new Square("F2", new Pawn(PieceType.PAWN, Colour.WHITE, "F2"))),
                entry("G2", new Square("G2", new Pawn(PieceType.PAWN, Colour.WHITE, "G2"))),
                entry("H2", new Square("H2", new Pawn(PieceType.PAWN, Colour.WHITE, "H2"))),
                //endregion
                //region White first rank
                entry("A1", new Square("A1", new Rook(PieceType.ROOK, Colour.WHITE, "A1"))),
                entry("B1", new Square("B1", new Knight(PieceType.KNIGHT, Colour.WHITE, "B1"))),
                entry("C1", new Square("C1", new Bishop(PieceType.BISHOP, Colour.WHITE, "C1"))),
                entry("D1", new Square("D1", new Queen(PieceType.QUEEN, Colour.WHITE, "D1"))),
                entry("E1", new Square("E1", new King(PieceType.KING, Colour.WHITE, "E1"))),
                entry("F1", new Square("F1", new Bishop(PieceType.BISHOP, Colour.WHITE, "F1"))),
                entry("G1", new Square("G1", new Knight(PieceType.KNIGHT, Colour.WHITE, "G1"))),
                entry("H1", new Square("H1", new Rook(PieceType.ROOK, Colour.WHITE, "H1")))
                //endregion
                //endregion
        ));

        addBoardToPieces();
        addEmptySquares();
    }

    public Square getSquare(String squareName) {
        return board.get(squareName);
    }
    public List<Square> getAllSquares() {
        List<Square> allSquares = new ArrayList<>();
        board.forEach((coordinate, square) -> allSquares.add(square));
        return allSquares;
    }
    public List<Piece> getPieceListByColour(Colour pieceColour) {
        return new ArrayList<>(pieceListsByColour.get(pieceColour));
    }
    public King popKingFromPieceListByColour(List<Piece> pieceList) {
        //Get King
        King king = null;
        for (Piece piece : pieceList) {
            if (piece.getType() == PieceType.KING) {
                king = (King)piece;
            }
        }
        assert (king != null);

        //Remove King from list before returning it
        pieceList.remove(king);
        return king;
    }
    public void removePieceFromBoard(Piece pieceToRemove) {
        //Get and remove piece's location
        String pieceLocation = pieceToRemove.getLocation();
        pieceToRemove.setLocation(null);

        //Remove from board
        board.get(pieceLocation).setContainedPiece(null);

        //Remove from piece list
        pieceListsByColour.get(pieceToRemove.getColour()).remove(pieceToRemove);
    }

    private void addBoardToPieces() {
        board.forEach((coordinate, square) -> {
            Piece piece = square.getContainedPiece();
            if (piece != null) {
                piece.setBoardManager(this);
                Colour pieceColour = piece.getColour();
                pieceListsByColour.get(pieceColour).add(piece);
            }
        });
    }
    private void addEmptySquares() {
        String squareName;
        for (int y = boardLength - 2; 2 < y; y--) {
            for (int x = 0; x < boardLength; x++) {
                squareName = letters[x] + String.valueOf(y);
                board.put(squareName, new Square(squareName, null));
            }
        }
    }
}