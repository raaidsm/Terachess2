package raaidsm.spring.test.models.exceptions;

public class PieceNotOnBoardException extends RuntimeException {
    public PieceNotOnBoardException(String errorMessage) {
        super(errorMessage);
    }
}
