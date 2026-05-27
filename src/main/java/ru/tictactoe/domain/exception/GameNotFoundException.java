package ru.tictactoe.domain.exception;

import java.util.UUID;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(UUID id) {
        super("Игра с ID " + id + " не найдена");
    }
}