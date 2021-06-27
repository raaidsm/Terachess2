package raaidsm.spring.test.exceptions;

public class DrawException extends RuntimeException {
    public DrawException(String errorMessage) {
        super(errorMessage);
    }
}