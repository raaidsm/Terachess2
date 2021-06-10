package raaidsm.spring.test.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raaidsm.spring.test.models.exceptions.InvalidGameException;
import raaidsm.spring.test.models.managers.BoardManager;
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
    private final Logger logger = LoggerFactory.getLogger(GameEngine.class);
    private final BoardManager boardManager;
    private final TurnManager turnManager;
    private final CheckManager checkManager;

    public GameEngine() {
        boardManager = new BoardManager();
        turnManager = new TurnManager();
        checkManager = boardManager.createCheckManager();

        GameStatus gameStatus = calculateAllLegalMoves();
        if (gameStatus != GameStatus.LIVE) throw new InvalidGameException();
    }

    public List<String> getLegalMovesForAPiece(String squareNameOfPiece) {
        List<AttackingPieceStruct> legalMoves = boardManager
                .getSquare(squareNameOfPiece).getContainedPiece().legalMoves;
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
        Piece pieceToMove = boardManager.getSquare(firstSquare).getContainedPiece();
        boardManager.getSquare(firstSquare).setContainedPiece(null);

        //Record and remove piece-to-move-to, if exists
        Piece pieceToMoveTo = boardManager.getSquare(secondSquare).getContainedPiece();
        if (pieceToMoveTo != null) {
            pieceToMoveTo.setLocation(null);
            boardManager.getSquare(secondSquare).setContainedPiece(null);
            Colour pieceColour = pieceToMoveTo.getColour();
            boardManager.getPieceList(pieceColour).remove(pieceToMoveTo);
            //TODO: Record the captured piece
        }

        //Move piece-to-move to second square
        pieceToMove.setLocation(secondSquare);
        boardManager.getSquare(secondSquare).setContainedPiece(pieceToMove);

        //Change piece properties according to which piece it is
        changePiecePropertiesUponMove(pieceToMove);

        //Move has been made, now calculate all legal moves
        return calculateAllLegalMoves();
    }

    private void clearAllAttacksOnSquares() {
        Colour currentTurnColour = turnManager.getColour();
        assert currentTurnColour != null;
        List<Square> allSquares = boardManager.getAllSquaresFromBoard();
        for (Square allSquare : allSquares) {
            allSquare.clearAttacksByColour(currentTurnColour);
        }
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
        List<Piece> pieces = boardManager.getPieceList(turnManager.getColour());
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