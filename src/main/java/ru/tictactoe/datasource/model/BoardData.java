package ru.tictactoe.datasource.model;

import java.util.Arrays;

public class BoardData {
    private final int[][] matrix;  // final поле

    public BoardData(int[][] matrix) {
        this.matrix = copyMatrix(matrix);
    }

    public int[][] getMatrix() {
        return copyMatrix(this.matrix);
    }

    private int[][] copyMatrix(int[][] source) {
        int[][] copy = new int[source.length][];
        for (int i = 0; i < source.length; i++) {
            copy[i] = Arrays.copyOf(source[i], source[i].length);
        }
        return copy;
    }

}