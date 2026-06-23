package ru.tictactoe.domain.service;

import ru.tictactoe.domain.model.Game;

import java.util.List;
import java.util.UUID;

public interface GameService {
    Game createGame(UUID player1Id, char player1Symbol, boolean isSingleGame);
    Game getGame(UUID id);
    List<Game> getAvailableGames();
    List<Game> getGamesByPlayerId(UUID playerId);
    Game joinGame(UUID gameId, UUID player2Id);
    Game makeMove(UUID gameId, Game gameFromClient, UUID playerId);
    boolean validateGameState(UUID gameId, Game proposedGame);
}
