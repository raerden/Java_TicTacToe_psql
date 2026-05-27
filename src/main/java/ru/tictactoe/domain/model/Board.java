package ru.tictactoe.domain.model;

public class Board {
    private int[][] matrix;

    public Board() {
    }

    public Board(int[][] matrix) {
        this.matrix = matrix;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(int[][] matrix) {
        this.matrix = matrix;
    }
}
