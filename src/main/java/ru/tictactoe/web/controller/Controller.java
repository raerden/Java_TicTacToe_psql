package ru.tictactoe.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.tictactoe.domain.model.Game;
import ru.tictactoe.domain.model.GameStatus;
import ru.tictactoe.domain.service.GameService;
import ru.tictactoe.web.mapper.WebMapper;
import ru.tictactoe.web.model.GameDto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController // Аннотация говорит Spring, что этот класс будет обрабатывать HTTP запросы и возвращать данные (не HTML страницы)
public class Controller {
    private final GameService gameService;
    private final WebMapper webMapper;

    public Controller(GameService gameService, WebMapper webMapper) {
        this.gameService = gameService;
        this.webMapper = webMapper;
    }

    @GetMapping("/")    //  связывает метод с корневым URL (http://localhost:8080/)
    public String hello() {
        return "Крестики-нолики запущены!";
    }

    @GetMapping("/game")
    public GameDto createGame(
            HttpServletRequest request,
            @RequestParam(defaultValue = "X") char symbol
    ) {
        UUID userId = (UUID) request.getAttribute("userId");
        Game game = gameService.createGame(userId, symbol);
        return webMapper.toDTO(game);
    }

    //Вернуть состояние игры если такая сохранена
    @GetMapping("/game/{id}")
    public GameDto getGame(@PathVariable("id") UUID gameId) {
        Game game = gameService.getGame(gameId);
        return webMapper.toDTO(game);
    }

    //Добавь endpoint для получения доступных текущих игр. Статус: WAITING_PLAYERS
    @GetMapping("/game/available")
    public List<GameDto> getAvailableGames() {
        List<Game> games = gameService.getAvailableGames();

        return games.stream()
                .map(webMapper::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/game/{id}")
    public GameDto makeMoveRequest(
            @PathVariable("id") UUID id,
            @RequestBody GameDto gameDto
    ) {
        // TODO: переделать под новый Game
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Метод временно отключён");
    }

/*
    @PostMapping("/game/{id}")
    public GameDto makeMoveRequest(
            @PathVariable ("id") UUID id, // Spring сам сконвертирует строку в UUID
            @RequestBody GameDto gameDto  // Spring сам распарсит JSON в объект
    ) {

        // Проверка ID
        if (gameDto.getId() == null || !id.equals(gameDto.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID mismatch");
        }

        // Конвертируем DTO в доменную модель
        Game proposedGame = webMapper.toDomain(gameDto);

        // Валидация сохраненной игры с присланным состоянием
        if (!gameService.validateGameState(id, proposedGame)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid game state");
        }

        // Делаем ход компьютера (и проверяем победу)
        Game updatedGame = gameService.makeComputerMove(proposedGame);

        // Возвращаем клиенту через мапперDTO
        return webMapper.toDTO(updatedGame);

    }
 */
}