package raaidsm.spring.test.exceptions;

public class InvalidLegalMoveException extends RuntimeException {
    public InvalidLegalMoveException(String errorMessage) {
        super(errorMessage);
    }
}