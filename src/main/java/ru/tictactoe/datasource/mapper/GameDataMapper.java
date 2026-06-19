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
                game.getPlayer1Id(),
                game.getPlayer2Id(),
                game.getCurrentPlayer(),
                game.getPlayer1Symbol(),
                game.getPlayer2Symbol(),
                game.getWinner(),
                game.getGameStatus()
        );
    }

    public Game toDomain(GameData gameData) {
        if (gameData == null) return null;

        Board board = new Board(gameData.getBoard().getMatrix());

        return new Game(
                gameData.getId(),
                board,
                gameData.getPlayer1Id(),
                gameData.getPlayer2Id(),
                gameData.getCurrentPlayer(),
                gameData.getPlayer1Symbol(),
                gameData.getPlayer2Symbol(),
                gameData.getWinner(),
                gameData.getGameStatus()
        );
    }
}