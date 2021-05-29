package raaidsm.spring.test.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raaidsm.spring.test.models.exceptions.InvalidGameException;
import raaidsm.spring.test.models.managers.CheckManager;
import raaidsm.spring.test.models.pieces.*;
import raaidsm.spring.test.models.utils.*;
import raaidsm.spring.test.models.managers.TurnManager;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.piece_properties.PieceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class GameEngine {
    //region Field Variables
    private final Logger logger = LoggerFactory.getLogger(GameEngine.class);
    //region Constants
    private final int boardLength = 8;
    private final char[] letters = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H' };
    //endregion
    //region Board Tracking
    private final HashMap<String, Square> board;
    private final HashMap<Colour, List<Piece>> pieceListsByColour;
    //endregion
    //region Turn and Check Managers
    private final TurnManager turnManager;
    private final CheckManager checkManager;
    //endregion
    //endregion

    public GameEngine() {
        //region Initialize Board With Pieces
        this.board = new HashMap<>(Map.ofEntries(
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
        ));
        //endregion
        //region Initialize Coloured Piece Lists
        this.pieceListsByColour = new HashMap<>(Map.ofEntries(
                entry(Colour.WHITE, new ArrayList<>()), entry(Colour.BLACK, new ArrayList<>())
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
        for (int y = boardLength - 2; 2 < y; y--) {
            for (int x = 0; x < boardLength; x++) {
                board.put(letters[x] + String.valueOf(y), new Square(null));
            }
        }
        //endregion
        //region Initialize Other Fields
        this.turnManager = new TurnManager();
        King whiteKing = (King)board.get("E1").containedPiece;
        King blackKing = (King)board.get("E8").containedPiece;
        this.checkManager = new CheckManager(whiteKing, blackKing);
        //endregion
        //region Initialize Initial Moves
        GameStatus gameStatus = calculateAllLegalMoves();
        if (gameStatus != GameStatus.LIVE) throw new InvalidGameException();
        //endregion
    }

    public List<String> getLegalMovesForAPiece(String squareNameOfPiece) {
        List<AttackingPieceStruct> legalMoves = board.get(squareNameOfPiece).containedPiece.legalMoves;
        List<String> legalMoveSquareNames = new ArrayList<>();
        for (AttackingPieceStruct legalMove : legalMoves) {
            legalMoveSquareNames.add(legalMove.attackedSquareName);
        }
        return legalMoveSquareNames;
    }
    public GameStatus makeMove(String firstSquare, String secondSquare) {
        logger.trace("makeMove() runs");

        //Clear all previous attacks
        clearAllAttacksOnSquares();

        //Take piece-to-move off of first square
        Piece pieceToMove = board.get(firstSquare).containedPiece;
        board.get(firstSquare).containedPiece = null;

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
        pieceToMove.setLocation(secondSquare);
        board.get(secondSquare).containedPiece = pieceToMove;

        //Change piece properties according to which piece it is
        changePiecePropertiesUponMove(pieceToMove);

        //Move has been made, now calculate all legal moves
        return calculateAllLegalMoves();
    }

    private void clearAllAttacksOnSquares() {
        Colour currentTurnColour = turnManager.getColour();
        assert currentTurnColour != null;
        board.forEach((squareName, square) -> square.clearAttacksByColour(currentTurnColour));
    }
    private void changePiecePropertiesUponMove(Piece piece) {
        logger.trace("changePiecePropertiesUponMove() runs");
        //If piece that just made a move is a pawn, take away its initial move
        if (piece.getType() == PieceType.PAWN) {
            assert piece instanceof Pawn;
            Pawn pawn = (Pawn)piece;
            pawn.removeInitialPawnMove();
        }
        //If piece that just made a move is a king, remove its castling rights
        if (piece.getType() == PieceType.KING) {
            assert piece instanceof King;
            King king = (King)piece;
            king.removeCastlingRights();
        }
        //If piece that just made a move is a rook, remove its castling rights
        if (piece.getType() == PieceType.ROOK) {
            assert piece instanceof Rook;
            Rook rook = (Rook)piece;
            rook.removeCastlingRights();
        }
    }
    private GameStatus calculateAllLegalMoves() {
        /* OVERVIEW:
        0) If double check, only calculate moves for king
        1) Calculate moves for each piece:
            Determine if piece delivers check (and set isCheck)
            Determine if piece pins another piece (and set isPinned for pinned piece)
        Reduce moves for each piece according to board-state stipulations:
            2) For each piece, reduce if piece is pinned
            3) For each piece, reduce if check and piece is of same colour as checked king
        */
        logger.trace("calculateAllLegalMoves() runs");
        List<Piece> pieces = pieceListsByColour.get(turnManager.getColour());
        boolean legalMovesFound;

        //region Multi-Check
        if (1 < checkManager.getCheckingPieces().size()) {
            legalMovesFound = caseMultiCheck(pieces);
            turnManager.switchC();
            if (legalMovesFound) return GameStatus.LIVE;
            else return GameStatus.CHECKMATE;
        }
        //endregion
        //region Calculate All Possible Moves
        legalMovesFound = calculateAllPossibleMoves(pieces);
        //endregion
        //region Reduce Moves Due to Pin
        if (!legalMovesFound) {
            if (checkManager.isCheck()) return GameStatus.CHECKMATE;
            else return GameStatus.STALEMATE;
        }
        legalMovesFound = reduceMovesDueToPin(pieces);
        //endregion
        //region Reduce Moves Due to Check
        if (!legalMovesFound) {
            if (checkManager.isCheck()) return GameStatus.CHECKMATE;
            else return GameStatus.STALEMATE;
        }
        if (checkManager.isCheck()) legalMovesFound = reduceMovesDueToCheck(pieces);
        //endregion
        //region Set tracking variables for next turn
        checkManager.clearChecks();
        turnManager.switchC();
        //endregion
        if (legalMovesFound) return GameStatus.LIVE;
        else return GameStatus.CHECKMATE;
    }
    private boolean caseMultiCheck(List<Piece> pieces) {
        /* OVERVIEW:
        Returns: wasLegalMoveFound */
        boolean legalMovesFound = false;
        for (Piece piece : pieces) {
            if (piece.getType() == PieceType.KING) {
                MoveCalcResultsStruct results = piece.calculateMoves();
                if (results.hasMoves) legalMovesFound = true;
            }
            else piece.clearAllMoves();
        }
        return legalMovesFound;
    }
    private boolean calculateAllPossibleMoves(List<Piece> pieces) {
        /* OVERVIEW:
        Returns: wasLegalMoveFound */
        logger.trace("calculateAllPossibleMoves() runs");
        boolean legalMovesFound = false;
        for (Piece piece : pieces) {
            piece.clearAllMoves();
            MoveCalcResultsStruct results = piece.calculateMoves();
            if (results.hasMoves) legalMovesFound = true;
            if (results.checkedKing != null) {
                checkManager.setCheck(piece, results.attackType, results.checkedKing, results.squareName);
            }
        }
        return legalMovesFound;
    }
    private boolean reduceMovesDueToPin(List<Piece> pieces) {
        /* OVERVIEW:
        Returns: wasLegalMoveFound */
        boolean legalMovesFound = false;
        for (Piece piece : pieces) {
            if (piece.getType() != PieceType.KING) {
                MoveCalcResultsStruct results = piece.reduceMovesDueToPin();
                if (results.hasMoves) legalMovesFound = true;
            }
        }
        return legalMovesFound;
    }
    private boolean reduceMovesDueToCheck(List<Piece> pieces) {
        /* OVERVIEW:
        Returns: wasLegalMoveFound */
        boolean legalMovesFound = false;
        for (Piece piece : pieces) {
            if (piece.getType() != PieceType.KING) {
                MoveCalcResultsStruct results = piece.reduceMovesDueToCheck();
                if (results.hasMoves) legalMovesFound = true;
            }
        }
        return legalMovesFound;
    }
}