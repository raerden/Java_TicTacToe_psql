package ru.tictactoe.web.mapper;

import ru.tictactoe.domain.model.Board;
import ru.tictactoe.domain.model.Game;
import ru.tictactoe.web.model.BoardDto;
import ru.tictactoe.web.model.GameDto;

public class WebMapper {
    public GameDto toDTO(Game game) {
        if (game == null) {
            return null;
        }

        return new GameDto(
                game.getId(),
                new BoardDto(game.getBoard().getMatrix()),
                game.getCurrentPlayer(),
                game.getWinner(),
                game.isGameOver());
    }

    public Game toDomain(GameDto gameDTO) {
        if (gameDTO == null) {
            return null;
        }

        return new Game(
                gameDTO.getId(),
                new Board(gameDTO.getBoard().getMatrix()),
                gameDTO.getCurrentPlayer(),
                gameDTO.getWinner(),
                gameDTO.isGameOver());
    }
}
