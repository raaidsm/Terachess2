package raaidsm.spring.test.models.exceptions;

public class DrawException extends RuntimeException {
    public DrawException(String errorMessage) {
        super(errorMessage);
    }
}