package raaidsm.spring.test.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raaidsm.spring.test.exceptions.InvalidGameException;
import raaidsm.spring.test.models.managers.BoardManager;
import raaidsm.spring.test.models.managers.CheckManager;
import raaidsm.spring.test.models.moves_and_attacks.AttackOnSquareStruct;
import raaidsm.spring.test.models.moves_and_attacks.MoveCalcSummaryStruct;
import raaidsm.spring.test.models.pieces.*;
import raaidsm.spring.test.models.utils.*;
import raaidsm.spring.test.models.managers.TurnManager;
import raaidsm.spring.test.models.piece_properties.PieceType;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    private final Logger logger = LoggerFactory.getLogger(GameEngine.class);
    private final BoardManager boardManager;
    private final TurnManager turnManager;
    private final CheckManager checkManager;

    public GameEngine() {
        boardManager = new BoardManager();
        turnManager = new TurnManager();
        checkManager = new CheckManager(turnManager.getCurrentTurnColour());

        GameStatus gameStatus = calculateAllLegalMoves();
        if (gameStatus != GameStatus.LIVE) throw new InvalidGameException();
    }

    public List<String> getLegalMovesForAPiece(String squareNameOfPiece) {
        List<AttackOnSquareStruct> legalMoves = boardManager
                .getSquare(squareNameOfPiece).getContainedPiece().getLegalMoves();
        List<String> legalMoveSquareNames = new ArrayList<>();
        for (AttackOnSquareStruct legalMove : legalMoves) {
            legalMoveSquareNames.add(legalMove.attackedSquareName);
        }
        return legalMoveSquareNames;
    }
    public GameStatus makeMove(String firstSquare, String secondSquare) {
        logger.trace("makeMove() runs");

        //Clear all previous attacks
        clearAllAttacksOnSquaresAndPinsOnPieces();

        //Take piece-to-move off of first square
        Piece pieceToMove = boardManager.getSquare(firstSquare).getContainedPiece();
        boardManager.getSquare(firstSquare).setContainedPiece(null);

        //Record and remove piece-to-move-to, if exists
        Piece pieceToMoveTo = boardManager.getSquare(secondSquare).getContainedPiece();
        if (pieceToMoveTo != null) {
            boardManager.removePieceFromBoard(pieceToMoveTo);
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

    //Methods called immediately upon a new move being made
    private void clearAllAttacksOnSquaresAndPinsOnPieces() {
        List<Square> allSquares = boardManager.getAllSquares();
        for (Square square : allSquares) {
            square.clearAllAttacks();
            if (square.getContainedPiece() != null) {
                square.getContainedPiece().clearAllPins();
            }
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

    //Move Calculation Methods
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

        //Declare pieces to calculate all legal moves for
        List<Piece> currentPlayerPieces = boardManager.getPieceListByColour(turnManager.getCurrentTurnColour());
        King currentPlayerKing = boardManager.popKingFromPieceListByColour(currentPlayerPieces);
        List<Piece> opponentPieces = boardManager.getPieceListByColour(
                turnManager.getCurrentTurnColour().oppositeColour());
        King opponentKing = boardManager.popKingFromPieceListByColour(opponentPieces);
        boolean piecesHaveLegalMoves = false;
        boolean kingHasLegalMoves = false;

        //Special Multi-Check Case
        if (1 < checkManager.numOfChecks()) {
            piecesHaveLegalMoves = caseMultiCheck(currentPlayerPieces);
            checkManager.clearChecks();
            turnManager.switchCurrentTurnColour();
            checkManager.setCurrentTurnColour(turnManager.getCurrentTurnColour());
            if (piecesHaveLegalMoves) return GameStatus.LIVE;
            else return GameStatus.CHECKMATE;
        }

        //Calculate All Possible Moves (except for Kings)
        calculateAllPossibleMoves(opponentPieces);
        piecesHaveLegalMoves = calculateAllPossibleMoves(currentPlayerPieces);

        //Calculate All Possible Moves for Kings
        opponentKing.calculateMoves();
        if (currentPlayerKing.calculateMoves().hasMoves) kingHasLegalMoves = true;
        if (!piecesHaveLegalMoves && !kingHasLegalMoves) {
            if (checkManager.isCheck()) return GameStatus.CHECKMATE;
            else return GameStatus.STALEMATE;
        }

        //Reduce Moves Due to Pin
        piecesHaveLegalMoves = reduceMovesDueToPin(currentPlayerPieces);
        if (!piecesHaveLegalMoves && !kingHasLegalMoves) {
            if (checkManager.isCheck()) return GameStatus.CHECKMATE;
            else return GameStatus.STALEMATE;
        }

        //Reduce Moves Due to Check
        if (checkManager.isCheck()) piecesHaveLegalMoves = reduceMovesDueToCheck(currentPlayerPieces);

        //Set tracking variables for next turn
        checkManager.clearChecks();
        turnManager.switchCurrentTurnColour();
        checkManager.setCurrentTurnColour(turnManager.getCurrentTurnColour());

        //Return game status at the end of the turn
        if (piecesHaveLegalMoves || kingHasLegalMoves) return GameStatus.LIVE;
        else return GameStatus.CHECKMATE;
    }
    private boolean caseMultiCheck(List<Piece> pieces) {
        //OVERVIEW: Returns: wasLegalMoveFound
        logger.trace("caseMultiCheck() runs");

        boolean legalMovesFound = false;
        for (Piece piece : pieces) {
            if (piece.getType() == PieceType.KING) {
                MoveCalcSummaryStruct summary = piece.calculateMoves();
                if (summary.hasMoves) legalMovesFound = true;
            }
            else piece.clearAllMoves();
        }
        return legalMovesFound;
    }
    private boolean calculateAllPossibleMoves(List<Piece> pieces) {
        //OVERVIEW: Returns: wasLegalMoveFound
        logger.trace("calculateAllPossibleMoves() runs");

        boolean legalMovesFound = false;
        for (Piece piece : pieces) {
            MoveCalcSummaryStruct summary = piece.calculateMoves();
            if (summary.hasMoves) legalMovesFound = true;
            if (summary.checkedKing != null) {
                checkManager.setCheck(summary.checkedKing, piece, summary.attackType, summary.squareNamesOnPathOfCheck);
            }
        }
        return legalMovesFound;
    }
    private boolean reduceMovesDueToPin(List<Piece> pieces) {
        //OVERVIEW: Returns: wasLegalMoveFound
        logger.trace("reduceMovesDueToPin() runs");

        boolean legalMovesFound = false;
        for (Piece piece : pieces) {
            boolean hasMoves = piece.reduceMovesDueToPin();
            if (hasMoves) legalMovesFound = true;
        }
        return legalMovesFound;
    }
    private boolean reduceMovesDueToCheck(List<Piece> pieces) {
        //OVERVIEW: Returns: wasLegalMoveFound
        logger.trace("reduceMovesDueToCheck() runs");

        boolean legalMovesFound = false;
        for (Piece piece : pieces) {
            boolean hasMoves = piece.reduceMovesDueToCheck(checkManager.getSquaresFacilitatingCheck());
            if (hasMoves) legalMovesFound = true;
        }
        return legalMovesFound;
    }
}