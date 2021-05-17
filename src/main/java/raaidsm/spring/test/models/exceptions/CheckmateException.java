package raaidsm.spring.test.models.exceptions;

public class CheckmateException extends RuntimeException {
    public CheckmateException(String errorMessage) {
        super(errorMessage);
    }
}