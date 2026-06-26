package ru.tictactoe.web.model;

import ru.tictactoe.domain.model.GameStatus;
import ru.tictactoe.domain.model.ZeroCross;

import java.util.UUID;

public class GameDto {
    private UUID id;
    private BoardDto board;
    private UUID player1Id;
    private UUID player2Id;
    private ZeroCross player1Symbol;
    private ZeroCross player2Symbol;
    private UUID currentPlayer;
    private UUID winner;
    private GameStatus gameStatus;

    public GameDto() {
    }

    public GameDto(UUID id, BoardDto board, UUID player1Id, UUID player2Id,
                   ZeroCross player1Symbol, ZeroCross player2Symbol,
                   UUID currentPlayer, UUID winner, GameStatus gameStatus) {
        this.id = id;
        this.board = board;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.player1Symbol = player1Symbol;
        this.player2Symbol = player2Symbol;
        this.currentPlayer = currentPlayer;
        this.winner = winner;
        this.gameStatus = gameStatus;
    }

    // Геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public BoardDto getBoard() { return board; }
    public void setBoard(BoardDto board) { this.board = board; }

    public UUID getPlayer1Id() { return player1Id; }
    public void setPlayer1Id(UUID player1Id) { this.player1Id = player1Id; }

    public UUID getPlayer2Id() { return player2Id; }
    public void setPlayer2Id(UUID player2Id) { this.player2Id = player2Id; }

    public ZeroCross getPlayer1Symbol() { return player1Symbol; }
    public void setPlayer1Symbol(ZeroCross player1Symbol) { this.player1Symbol = player1Symbol; }

    public ZeroCross getPlayer2Symbol() { return player2Symbol; }
    public void setPlayer2Symbol(ZeroCross player2Symbol) { this.player2Symbol = player2Symbol; }

    public UUID getCurrentPlayer() { return currentPlayer; }
    public void setCurrentPlayer(UUID currentPlayer) { this.currentPlayer = currentPlayer; }

    public UUID getWinner() { return winner; }
    public void setWinner(UUID winner) { this.winner = winner; }

    public GameStatus getGameStatus() { return gameStatus; }
    public void setGameStatus(GameStatus gameStatus) { this.gameStatus = gameStatus; }
}