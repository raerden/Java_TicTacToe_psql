package ru.tictactoe.web.model;

public class BoardDto {
    private int[][] matrix;

    public BoardDto() {
    }

    public BoardDto(int[][] matrix) {
        this.matrix = matrix;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(int[][] matrix) {
        this.matrix = matrix;
    }
}
