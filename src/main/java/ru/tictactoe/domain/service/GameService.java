package ru.tictactoe.domain.service;

import ru.tictactoe.domain.model.Game;

import java.util.UUID;

public interface GameService {
    Game createGame();
    Game getGame(UUID id);
    Game makeComputerMove(Game game);
    boolean validateGameState(UUID gameId, Game proposedGame);
}
