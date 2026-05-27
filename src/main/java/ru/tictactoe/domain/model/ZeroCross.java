package ru.tictactoe.domain.model;

public enum ZeroCross {
    EMPTY(0),
    CROSS(1),
    ZERO(2);

    private int value;

    ZeroCross(int value) {
        this.value = value;
    }

    public int getValue() {return value;}
}
