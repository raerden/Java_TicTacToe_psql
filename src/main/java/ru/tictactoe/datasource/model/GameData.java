package ru.tictactoe.datasource.model;

import jakarta.persistence.*;
import ru.tictactoe.datasource.converter.BoardDataConverter;

import java.util.UUID;

@Entity  // ← говорит JPA: этот класс нужно сохранять в БД
@Table(name = "games")  // ← имя таблицы в БД
public class GameData {
    @Id  // ← говорит: это первичный ключ
    @GeneratedValue(strategy = GenerationType.UUID)  // ← UUID генерируется автоматически
    private UUID id;// айди игры

    @Convert(converter = BoardDataConverter.class)  // ← добавляем конвертер
    @Column(name = "board", length = 9, nullable = false)
    private BoardData board;

    @Column(name = "current_player", nullable = false)
    private int currentPlayer;

    @Column(name = "winner", nullable = false)
    private int winner;

    @Column(name = "game_over", nullable = false)
    private boolean gameOver;

    public GameData() {
    }

    public GameData(UUID id, BoardData board, int currentPlayer, int winner, boolean gameOver) {
        this.id = id;
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.winner = winner;
        this.gameOver = gameOver;
    }

    // Геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public BoardData getBoard() { return board; }
    public void setBoard(BoardData board) { this.board = board; }

    public int getCurrentPlayer() { return currentPlayer; }
    public void setCurrentPlayer(int currentPlayer) { this.currentPlayer = currentPlayer; }

    public int getWinner() { return winner; }
    public void setWinner(int winner) { this.winner = winner; }

    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
}