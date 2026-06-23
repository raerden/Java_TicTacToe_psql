package ru.tictactoe.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.tictactoe.domain.model.Game;
import ru.tictactoe.domain.model.User;
import ru.tictactoe.domain.service.GameService;
import ru.tictactoe.domain.service.UserService;
import ru.tictactoe.web.mapper.WebMapper;
import ru.tictactoe.web.model.GameDto;
import ru.tictactoe.web.model.UserDto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController // Аннотация говорит Spring, что этот класс будет обрабатывать HTTP запросы и возвращать данные (не HTML страницы)
public class Controller {
    private final GameService gameService;
    private final UserService userService;
    private final WebMapper webMapper;

    public Controller(GameService gameService, UserService userService,  WebMapper webMapper) {
        this.gameService = gameService;
        this.userService = userService;
        this.webMapper = webMapper;
    }

    @GetMapping("/")    //  связывает метод с корневым URL (http://localhost:8080/)
    public String hello() {
        return "Крестики-нолики запущены!";
    }

    // Создание игры с выбором символа /game?symbol=X
    @GetMapping("/game")
    public GameDto createGame(
            HttpServletRequest request,
            @RequestParam(defaultValue = "X") char symbol,
            @RequestParam(defaultValue = "false") boolean singlePlayer
    ) {
        symbol = Character.toUpperCase(symbol);
        if (symbol != 'X' && symbol != 'O') symbol = 'X';

        UUID userId = (UUID) request.getAttribute("userId");
        Game game = gameService.createGame(userId, symbol, singlePlayer);
        return webMapper.toDTO(game);
    }

    //Вернуть состояние игры если такая сохранена
    @GetMapping("/game/{id}")
    public GameDto getGame(@PathVariable("id") UUID gameId) {
        Game game = gameService.getGame(gameId);
        return webMapper.toDTO(game);
    }

    // endpoint для получения информации о пользователе по UUID.
    @GetMapping("/user/{id}")
    public UserDto getUser(@PathVariable("id") UUID userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Пользователь с ID " + userId + " не найден"
                ));

        return webMapper.userToDto(user);
    }

    //Добавь endpoint для получения доступных текущих игр. Статус: WAITING_PLAYERS
    @GetMapping("/game/available")
    public List<GameDto> getAvailableGames() {
        List<Game> games = gameService.getAvailableGames();

        return games.stream()
                .map(webMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Получить все игры текущего пользователя (любой статус)
    @GetMapping("/game/my")
    public List<GameDto> getMyGames(HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        List<Game> games = gameService.getGamesByPlayerId(userId);

        return games.stream()
                .map(webMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/game/join/{id}")
    public GameDto joinGame(
            @PathVariable("id") UUID gameId,
            HttpServletRequest request
    ) {
        // Получаем ID текущего пользователя из AuthFilter
        UUID player2Id = (UUID) request.getAttribute("userId");

        // Присоединяемся к игре
        Game updatedGame = gameService.joinGame(gameId, player2Id);

        return webMapper.toDTO(updatedGame);
    }

    @PostMapping("/game/{id}")
    public GameDto makeMoveRequest(
            @PathVariable("id") UUID gameId,
            @RequestBody GameDto gameDto,
            HttpServletRequest request
    ) {
        // 1. Получаем ID текущего пользователя
        UUID playerId = (UUID) request.getAttribute("userId");

        // 2. Проверяем, что ID в пути и в теле совпадают
        if (gameDto.getId() == null || !gameId.equals(gameDto.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID mismatch");
        }

        // 3. Конвертируем DTO в доменную модель
        Game proposedGame = webMapper.toDomain(gameDto);

        // 4. Делаем ход
        Game updatedGame = gameService.makeMove(gameId, proposedGame, playerId);

        // 5. Возвращаем обновлённое состояние
        return webMapper.toDTO(updatedGame);
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