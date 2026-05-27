package ru.tictactoe.datasource.mapper;

import ru.tictactoe.domain.model.Board;
import ru.tictactoe.domain.model.Game;
import ru.tictactoe.datasource.model.BoardData;
import ru.tictactoe.datasource.model.GameData;

public class GameDataMapper {

    public GameData toData(Game game) {
        if (game == null) return null;

        BoardData boardData = new BoardData(game.getBoard().getMatrix());

        return new GameData(
                game.getId(),
                boardData,
                game.getCurrentPlayer(),
                game.getWinner(),
                game.isGameOver()
        );
    }

    public Game toDomain(GameData gameData) {
        if (gameData == null) return null;

        Board board = new Board(gameData.getBoard().getMatrix());

        return new Game(
                gameData.getId(),
                board,
                gameData.getCurrentPlayer(),
                gameData.getWinner(),
                gameData.isGameOver()
        );
    }
}