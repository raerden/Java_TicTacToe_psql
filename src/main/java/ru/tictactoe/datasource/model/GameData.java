package ru.tictactoe.datasource.model;

import jakarta.persistence.*;
import ru.tictactoe.datasource.converter.BoardDataConverter;
import ru.tictactoe.domain.model.GameStatus;

import java.util.UUID;

@Entity
@Table(name = "games")
public class GameData {
    @Id
    private UUID id;

    @Convert(converter = BoardDataConverter.class)
    @Column(name = "board", length = 9, nullable = false)
    private BoardData board;

    @Column(name = "player1_id", nullable = false)
    private UUID player1Id;

    @Column(name = "player2_id", nullable = true)
    private UUID player2Id;

    @Column(name = "current_player", nullable = false)
    private UUID currentPlayer;

    @Column(name = "player1_symbol", nullable = false)
    private char player1Symbol;

    @Column(name = "player2_symbol", nullable = false)
    private char player2Symbol;

    @Column(name = "winner", nullable = true)
    private UUID winner;

    @Enumerated(EnumType.STRING)  // сохраняем как строку: "WAITING_PLAYERS", "IN_PROGRESS" и т.д.
    @Column(name = "game_status", nullable = false)
    private GameStatus gameStatus;

    public GameData() {
    }

    public GameData(UUID id, BoardData board, UUID player1Id, UUID player2Id,
                    UUID currentPlayer, char player1Symbol, char player2Symbol,
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

    //ГЕТТЕРЫ И СЕТТЕРЫ
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public BoardData getBoard() { return board; }
    public void setBoard(BoardData board) { this.board = board; }

    public UUID getPlayer1Id() { return player1Id; }
    public void setPlayer1Id(UUID player1Id) { this.player1Id = player1Id; }

    public UUID getPlayer2Id() { return player2Id; }
    public void setPlayer2Id(UUID player2Id) { this.player2Id = player2Id; }

    public UUID getCurrentPlayer() { return currentPlayer; }
    public void setCurrentPlayer(UUID currentPlayer) { this.currentPlayer = currentPlayer; }

    public char getPlayer1Symbol() { return player1Symbol; }
    public void setPlayer1Symbol(char player1Symbol) { this.player1Symbol = player1Symbol; }

    public char getPlayer2Symbol() { return player2Symbol; }
    public void setPlayer2Symbol(char player2Symbol) { this.player2Symbol = player2Symbol; }

    public UUID getWinner() { return winner; }
    public void setWinner(UUID winner) { this.winner = winner; }

    public GameStatus getGameStatus() { return gameStatus; }
    public void setGameStatus(GameStatus gameStatus) { this.gameStatus = gameStatus; }
}