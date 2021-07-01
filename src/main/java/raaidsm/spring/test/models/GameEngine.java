package raaidsm.spring.test.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raaidsm.spring.test.exceptions.InvalidGameException;
import raaidsm.spring.test.exceptions.InvalidLegalMoveException;
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
        clearSquareAttacksAndPins();

        //Change piece properties according to which piece it is
        Piece pieceToMove = boardManager.getSquare(firstSquare).getContainedPiece();
        Square squareToMoveTo = boardManager.getSquare(secondSquare);
        Pawn shadowedPawn = squareToMoveTo.getShadowedPawn();
        pieceToMove = applyMoveSideEffects(pieceToMove, secondSquare);

        //Take piece-to-move off of first square
        boardManager.getSquare(firstSquare).setContainedPiece(null);

        //Record and remove piece-to-move-to, if exists
        Piece pieceToMoveTo = squareToMoveTo.getContainedPiece();
        if (pieceToMoveTo != null) {
            boardManager.removePieceFromBoard(pieceToMoveTo);
            //TODO: Record the captured piece
        }
        else if (shadowedPawn != null) {
            boardManager.removePieceFromBoard(shadowedPawn);
        }

        //Move piece-to-move to second square
        pieceToMove.setLocation(secondSquare);
        boardManager.getSquare(secondSquare).setContainedPiece(pieceToMove);

        //Move has been made, now calculate all legal moves
        return calculateAllLegalMoves();
    }

    //Methods called immediately upon a new move being made
    private void clearSquareAttacksAndPins() {
        List<Square> allSquares = boardManager.getAllSquares();
        for (Square square : allSquares) {
            square.clearAllAttacks();
            if (square.getContainedPiece() != null) {
                square.getContainedPiece().clearAllPins();
            }
        }
    }
    private Piece applyMoveSideEffects(Piece pieceMoving, String squareNameToMoveTo) {
        //OVERVIEW: Applies side effects that certain moves have, and return the promoted piece if one is created
        logger.trace("changePiecePropertiesUponMove() runs");

        //Global
        if (turnManager.isTurnToRemoveEnPassant()) boardManager.removeShadowPawn(turnManager.getCurrentTurnColour());

        //Based on piece
        //If piece that just made a move is a pawn, take away its initial move
        if (pieceMoving.getType() == PieceType.PAWN) {
            assert pieceMoving instanceof Pawn;
            Pawn pawn = (Pawn)pieceMoving;
            pawn.removeInitialPawnMove();

            Direction dirOfMovement = Square.getTwoSquareDistAndDir(pawn.getLocation(), squareNameToMoveTo);
            //Checking for Initial Pawn Move and Promotion
            if (dirOfMovement != null && dirOfMovement.getMagnitude() == 2
                    && (dirOfMovement == Direction.UP || dirOfMovement == Direction.DOWN)) {
                boardManager.setShadowPawn(pawn, dirOfMovement);
                turnManager.setEnPassant();
            }
            else if (boardManager.isFirstOrLastRow(squareNameToMoveTo)) {
                //Then promote the Pawn
                //TODO: For now Pawns only promote to Queens
                return boardManager.promote(pawn, PieceType.QUEEN).piece;
            }

            return pawn;
        }
        //If piece that just made a move is a king, remove its castling rights
        else if (pieceMoving.getType() == PieceType.KING) {
            assert pieceMoving instanceof King;
            King king = (King)pieceMoving;
            king.removeCastlingRights();

            Direction dirOfMovement = Square.getTwoSquareDistAndDir(king.getLocation(), squareNameToMoveTo);
            //If movement is 2 squares horizontally (thus meaning a castling move was made)
            if (dirOfMovement != null && dirOfMovement.getMagnitude() == 2
                    && (dirOfMovement == Direction.LEFT || dirOfMovement == Direction.RIGHT)) {
                performCastling(king, dirOfMovement);
            }

            return king;
        }
        //If piece that just made a move is a rook, remove its castling rights
        else if (pieceMoving.getType() == PieceType.ROOK) {
            assert pieceMoving instanceof Rook;
            Rook rook = (Rook)pieceMoving;
            rook.removeCastlingRights();

            return rook;
        }
        else return pieceMoving;
    }
    private void performCastling(King king, Direction dirOfMovement) {
        //OVERVIEW: Castle (meaning move the Rook to the other side of the King)

        //For iterating through squares until Rook is found
        Point kingLocation = new Point(king.getLocation());
        int magnitude = 0;
        dirOfMovement.setMagnitude(++magnitude);
        String squareNameToMoveRook = kingLocation.findRelativeByXAndY(dirOfMovement.x, dirOfMovement.y);

        //Find Rook to castle with and move it
        boolean rookMoved = false;
        while (!rookMoved) {
            dirOfMovement.setMagnitude(++magnitude);
            String currentSquareName = kingLocation.findRelativeByXAndY(dirOfMovement.x, dirOfMovement.y);
            //The end of the board cannot be hit before a Rook is found, as this was determined to be a legal move
            if (currentSquareName == null) {
                throw new InvalidLegalMoveException("End of the board cannot be hit before a Rook is found");
            }

            Square currentSquare = boardManager.getSquare(currentSquareName);
            Piece currentPiece = currentSquare.getContainedPiece();
            //Guard clause for square being empty
            if (currentPiece == null) continue;

            if (currentPiece.getType() == PieceType.ROOK) {
                Rook rook = (Rook)currentPiece;
                //Rook must be able to castle, as this was determined to be a legal move
                if (!rook.isCanCastle()) {
                    throw new InvalidLegalMoveException("Found Rook cannot be unable to castle");
                }

                Square squareToMoveRook = boardManager.getSquare(squareNameToMoveRook);
                //Rook must not be blocked by a piece from moving to where it has to to castle
                if (squareToMoveRook.getContainedPiece() != null) {
                    throw new InvalidLegalMoveException("Location to move Rook cannot be occupied");
                }
                //Move Rook
                rook.setLocation(squareNameToMoveRook);
                squareToMoveRook.setContainedPiece(rook);
                currentSquare.setContainedPiece(null);

                rookMoved = true;
            }
            else throw new InvalidLegalMoveException("Can't have a piece between the King and Rook");
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