package raaidsm.spring.test.exceptions;

public class PieceNotOnBoardException extends RuntimeException {
    public PieceNotOnBoardException(String errorMessage) {
        super(errorMessage);
    }
}
