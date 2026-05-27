package ru.tictactoe.domain.exception;

public class ValidateGameException extends RuntimeException {
    public ValidateGameException(String message) {
        super(message);
    }
}
