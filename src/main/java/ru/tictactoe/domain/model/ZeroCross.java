package ru.tictactoe.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ZeroCross {
    CROSS(1, 'X'),
    ZERO(2, 'O');

    private final int value;
    private final char symbol;

    ZeroCross(int value, char symbol) {
        this.value = value;
        this.symbol = symbol;
    }

    public int getValue() {
        return value;
    }

    @JsonValue
    public char getSymbol() {
        return symbol;
    }

    public static ZeroCross invert(ZeroCross symbol) {
        return symbol == CROSS ? ZERO : CROSS;
    }

    // Метод для получения enum по значению
    public static ZeroCross fromValue(int value) {
        if (value == 1 || value == 2) {
            return value == 1 ? CROSS : ZERO;
        }
        return null;
    }

    // Метод для получения enum по символу
    @JsonCreator
    public static ZeroCross fromSymbol(char symbol) {
        if (symbol == 'X' || symbol == 'O') {
            return symbol == 'X' ? CROSS : ZERO;
        }
        return null;
    }

}