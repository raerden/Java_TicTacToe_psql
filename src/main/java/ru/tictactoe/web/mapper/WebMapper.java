package ru.tictactoe.web.mapper;

import org.springframework.stereotype.Component;
import ru.tictactoe.domain.model.Board;
import ru.tictactoe.domain.model.Game;
import ru.tictactoe.domain.model.User;
import ru.tictactoe.web.model.BoardDto;
import ru.tictactoe.web.model.GameDto;
import ru.tictactoe.web.model.UserDto;

import java.util.Optional;

@Component
public class WebMapper {

    public GameDto toDTO(Game game) {
        if (game == null) {
            return null;
        }

        return new GameDto(
                game.getId(),
                new BoardDto(game.getBoard().getMatrix()),
                game.getPlayer1Id(),
                game.getPlayer2Id(),
                game.getPlayer1Symbol(),
                game.getPlayer2Symbol(),
                game.getCurrentPlayer(),
                game.getWinner(),
                game.getGameStatus()
        );
    }

    public Game toDomain(GameDto dto) {
        if (dto == null) {
            return null;
        }

        return new Game(
                dto.getId(),
                new Board(dto.getBoard().getMatrix()),
                dto.getPlayer1Id(),
                dto.getPlayer2Id(),
                dto.getCurrentPlayer(),
                dto.getPlayer1Symbol(),
                dto.getPlayer2Symbol(),
                dto.getWinner(),
                dto.getGameStatus()
        );
    }

    public UserDto userToDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getId(),
                user.getLogin()
        );
    }

}