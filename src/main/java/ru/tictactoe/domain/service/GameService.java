package ru.tictactoe.domain.service;

import ru.tictactoe.domain.model.Game;

import java.util.List;
import java.util.UUID;

public interface GameService {
    Game createGame(UUID player1Id, char player1Symbol);
    Game getGame(UUID id);
    List<Game> getAvailableGames();
    Game makeComputerMove(Game game);
    boolean validateGameState(UUID gameId, Game proposedGame);
}
