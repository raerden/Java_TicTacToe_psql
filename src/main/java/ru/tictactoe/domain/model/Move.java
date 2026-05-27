package ru.tictactoe.domain.model;

public class Move {
    private int row;
    private int col;
    private ZeroCross zeroCross;

    public Move(int row, int col, ZeroCross value) {
        this.row = row;
        this.col = col;
        this.zeroCross = value;

    }

    public int getRow() { return row;}

    public int getCol() { return col;}
    public int getValue() { return zeroCross.getValue();}
}
