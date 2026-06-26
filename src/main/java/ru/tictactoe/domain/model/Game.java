package ru.tictactoe.domain.model;

import java.util.UUID;

public class Game {
    private UUID id;
    private UUID player1Id;
    private UUID player2Id;      // может быть null, если игра с компьютером или ожидание
    private ZeroCross player1Symbol;   // 'X'
    private ZeroCross player2Symbol;   // 'O'
    private Board board;
    private UUID currentPlayer;   // UUID игрока, который сейчас ходит
    private UUID winner;          // null — нет победителя
    private GameStatus gameStatus;

    public Game() {
    }

    public Game(UUID id, Board board, UUID player1Id, UUID player2Id,
                UUID currentPlayer, ZeroCross player1Symbol, ZeroCross player2Symbol,
                UUID winner, GameStatus gameStatus) {
        this.id = id;
        this.board = board;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.currentPlayer = currentPlayer;
        this.player1Symbol = player1Symbol;
        this.player2Symbol = player2Symbol;
        this.winner = winner;
        this.gameStatus = gameStatus;
    }

    // Геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Board getBoard() { return board; }
    public void setBoard(Board board) { this.board = board; }

    public UUID getCurrentPlayer() { return currentPlayer; }
    public void setCurrentPlayer(UUID currentPlayer) { this.currentPlayer = currentPlayer; }

    public UUID getWinner() { return winner; }
    public void setWinner(UUID winner) { this.winner = winner; }

    public GameStatus getGameStatus() { return gameStatus; }
    public void setGameStatus(GameStatus gameStatus) { this.gameStatus = gameStatus; }

    public UUID getPlayer1Id() { return player1Id; }
    public void setPlayer1Id(UUID player1Id) { this.player1Id = player1Id; }

    public UUID getPlayer2Id() { return player2Id; }
    public void setPlayer2Id(UUID player2Id) { this.player2Id = player2Id; }

    public ZeroCross getPlayer1Symbol() { return player1Symbol; }
    public void setPlayer1Symbol(ZeroCross player1Symbol) { this.player1Symbol = player1Symbol; }

    public ZeroCross getPlayer2Symbol() { return player2Symbol; }
    public void setPlayer2Symbol(ZeroCross player2Symbol) { this.player2Symbol = player2Symbol; }

    // Метод для хода
    public void setMove(Move move) {
        int[][] matrix = this.board.getMatrix();

        if (matrix[move.getRow()][move.getCol()] != 0) {
            throw new IllegalStateException("Клетка уже занята");
        }

        matrix[move.getRow()][move.getCol()] = move.getValue();
    }

    public boolean isGameOver() {
        return gameStatus == GameStatus.DRAW || gameStatus == GameStatus.WINNER;
    }
}