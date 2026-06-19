package ru.tictactoe.domain.model;

public enum GameStatus {
    WAITING_PLAYERS (0, "Wait players for start game"),
    IN_PROGRESS (1, "Игра активна"),
    DRAW (2, "Игра завершена ничьей"),
    WINNER (3, "Игра завершена победой игрока");

    private final int code;
    private final String description;

    // Конструктор приватный (по умолчанию)
    GameStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
