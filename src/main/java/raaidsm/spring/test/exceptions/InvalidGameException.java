package raaidsm.spring.test.exceptions;

public class InvalidGameException extends RuntimeException {
    public InvalidGameException() {
        super("This game is not valid and can therefore not start");
    }
}