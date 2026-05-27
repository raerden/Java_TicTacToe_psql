package ru.tictactoe.domain.model;

import java.util.UUID;

public class Game {
    private UUID id;
    private Board board;      // игровое поле 3x3
    private int currentPlayer; // 1 - крестики (игрок), 2 - нолики (компьютер)
    private int winner;        // 0 - нет победителя, 1 - игрок, 2 - компьютер
    private boolean gameOver;

    public Game() {
    }

    public Game(UUID id, Board board, int currentPlayer, int winner, boolean gameOver) {
        this.id = id;
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.winner = winner;
        this.gameOver = gameOver;
    }

    // Геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Board getBoard() { return board; }
    public void setBoard(Board board) { this.board = board; }

    public int getCurrentPlayer() { return currentPlayer; }
    public void setCurrentPlayer(int currentPlayer) { this.currentPlayer = currentPlayer; }

    public int getWinner() { return winner; }
    public void setWinner(int winner) { this.winner = winner; }

    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }

    public void setMove(Move move) {
        // Получаем текущую матрицу
        int[][] matrix = this.board.getMatrix();

        // Проверяем, что клетка пуста (0)
        if (matrix[move.getRow()][move.getCol()] != 0) {
            throw new IllegalStateException("Клетка уже занята");
        }

        // Ставим новый ход
        matrix[move.getRow()][move.getCol()] = move.getValue();
    }
}