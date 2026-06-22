package ru.tictactoe.domain.service;

import ru.tictactoe.datasource.mapper.GameDataMapper;
import ru.tictactoe.datasource.model.GameData;
import ru.tictactoe.datasource.repository.GameRepository;
import ru.tictactoe.domain.exception.ValidateGameException;
import ru.tictactoe.domain.model.*;
import ru.tictactoe.domain.exception.GameNotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final GameDataMapper gameDataMapper;  // для конвертации
    private static final int PLAYER = 1;    // крестик (максимизирующий игрок)
    private static final int COMPUTER = 2;  // нолик (минимизирующий игрок)
    private static final int EMPTY = 0;

    public GameServiceImpl(GameRepository gameRepository, GameDataMapper gameDataMapper) {
        this.gameRepository = gameRepository;
        this.gameDataMapper = gameDataMapper;
    }

    public Game createGame(UUID player1Id, char player1Symbol) {
        // Новая игра: пустая доска, ход игрока (1), нет победителя, игра не окончена
        Game game = new Game(
                UUID.randomUUID(),
                new Board(new int[3][3]),
                player1Id,
                null,  //
                player1Id, // currentPlayer первый игрок начинает
                player1Symbol,
                player1Symbol == 'X' ? 'O' : 'X',
                null,  // winner — пока нет
                GameStatus.WAITING_PLAYERS
        );

        GameData gameData = gameDataMapper.toData(game);
        gameRepository.save(gameData);

        return game;
    }

    public Game getGame(UUID id) {
        GameData gameData = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));

        return gameDataMapper.toDomain(gameData);
    }

    @Override
    public List<Game> getAvailableGames() {
        // Находим все игры со статусом WAITING_PLAYERS
        List<GameData> gamesData = gameRepository.findAllByGameStatus(GameStatus.WAITING_PLAYERS);

        // Преобразуем в доменные объекты
        return gamesData.stream()
                .map(gameDataMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Game> getGamesByPlayerId(UUID playerId) {
        // Находим все игры, где пользователь player1 или player2
        List<GameData> gamesData = gameRepository.findAllByPlayer1IdOrPlayer2Id(playerId, playerId);

        // Преобразуем в доменные объекты
        return gamesData.stream()
                .map(gameDataMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Game joinGame(UUID gameId, UUID player2Id) {
        // 1. Загружаем игру
        GameData gameData = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        // 2. Проверяем, что игра ждёт игроков
        if (gameData.getGameStatus() != GameStatus.WAITING_PLAYERS) {
            throw new IllegalStateException("Игра уже начата или завершена");
        }

        // 3. Проверяем, что второй игрок не равен первому
        if (gameData.getPlayer1Id().equals(player2Id)) {
            throw new IllegalStateException("Вы не можете присоединиться к своей собственной игре");
        }

        // 4. Обновляем игру
        gameData.setPlayer2Id(player2Id);
        gameData.setGameStatus(GameStatus.IN_PROGRESS);
        // currentPlayer остаётся player1Id (первый игрок начинает)

        // 5. Сохраняем
        GameData updatedGameData = gameRepository.save(gameData);

        // 6. Возвращаем доменную модель
        return gameDataMapper.toDomain(updatedGameData);
    }


    @Override
    public Game makeMove(UUID gameId, Game gameFromClient, UUID playerId) {
        // 1. Загружаем актуальную игру из БД
        GameData gameData = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        Game savedGame = gameDataMapper.toDomain(gameData);

        // 2. Проверяем, что игра активна
        if (savedGame.getGameStatus() != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Игра не активна");
        }

        // 3. Проверяем, что ходит правильный игрок
        if (!savedGame.getCurrentPlayer().equals(playerId)) {
            throw new IllegalStateException("Сейчас не ваш ход");
        }

        // 4. Валидируем состояние доски (через существующий метод)
        if (!validateGameState(gameId, gameFromClient)) {
            throw new IllegalStateException("Невалидное состояние игры");
        }

        // 5. Применяем ход игрока
        // Находим, какая клетка изменилась
        int[][] savedMatrix = savedGame.getBoard().getMatrix();
        int[][] proposedMatrix = gameFromClient.getBoard().getMatrix();

        int row = -1, col = -1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (savedMatrix[i][j] != proposedMatrix[i][j]) {
                    row = i;
                    col = j;
                    break;
                }
            }
            if (row != -1) break;
        }

        // Применяем ход к сохранённой игре
        Move move = new Move(row, col,
                playerId.equals(savedGame.getPlayer1Id()) ?
                        (savedGame.getPlayer1Symbol() == 'X' ? ZeroCross.CROSS : ZeroCross.ZERO) :
                        (savedGame.getPlayer2Symbol() == 'X' ? ZeroCross.CROSS : ZeroCross.ZERO)
        );
        savedGame.setMove(move);

        // 6. Проверяем победу
        int winnerValue = checkWinner(savedGame.getBoard().getMatrix());
        if (winnerValue != 0) {
            // Кто победил?
            UUID winnerId = null;
            if (winnerValue == 1) { // X
                winnerId = savedGame.getPlayer1Symbol() == 'X' ?
                        savedGame.getPlayer1Id() : savedGame.getPlayer2Id();
            } else { // O
                winnerId = savedGame.getPlayer1Symbol() == 'O' ?
                        savedGame.getPlayer1Id() : savedGame.getPlayer2Id();
            }

            savedGame.setWinner(winnerId);
            savedGame.setGameStatus(GameStatus.WINNER);

            // Сохраняем и возвращаем
            gameRepository.save(gameDataMapper.toData(savedGame));
            return savedGame;
        }

        // 7. Проверяем ничью
        if (isBoardFull(savedGame.getBoard().getMatrix())) {
            savedGame.setGameStatus(GameStatus.DRAW);
            savedGame.setWinner(null);

            gameRepository.save(gameDataMapper.toData(savedGame));
            return savedGame;
        }

        // 8. Передаём ход другому игроку
        UUID nextPlayer = savedGame.getCurrentPlayer().equals(savedGame.getPlayer1Id()) ?
                savedGame.getPlayer2Id() : savedGame.getPlayer1Id();
        savedGame.setCurrentPlayer(nextPlayer);

        // 9. Сохраняем
        gameRepository.save(gameDataMapper.toData(savedGame));

        return savedGame;
    }

    @Override
    public boolean validateGameState(UUID gameId, Game proposedGame) {
        //Загружаем сохраненную иргу
        Game savedGame = getGame(gameId);

        //Проверить что игра не завершена
        if(savedGame.isGameOver()) {
            throw new ValidateGameException("Игра " + gameId + " завершена!");
        }

        // Сравниваем доски игры
        int[][] savedMatrix = savedGame.getBoard().getMatrix();
        int[][] proposedMatrix = proposedGame.getBoard().getMatrix();

        int changes = 0;
        int changedRow = -1;
        int changedCol = -1;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (savedMatrix[i][j] != proposedMatrix[i][j]) {
                    changes++;
                    changedRow = i;
                    changedCol = j;
                }
            }
        }

        if (changes == 0) {
            throw new ValidateGameException("Игрок не сделал ход!");
        }

        // Должна измениться ровно одна клетка (ход игрока)
        if (changes != 1) {
            throw new ValidateGameException("Изменены более одной клетки!");
        }

        // Измененная клетка была не пустой
        if (savedMatrix[changedRow][changedCol] != 0) {
            throw new ValidateGameException("Данная клетка занята!");
        }

        // Определяем, какой символ должен поставить игрок
        int expectedSymbol;
        UUID currentPlayerId = savedGame.getCurrentPlayer();

        if (currentPlayerId.equals(savedGame.getPlayer1Id())) {
            // Ходит первый игрок — его символ
            expectedSymbol = savedGame.getPlayer1Symbol() == 'X' ? 1 : 2;
        } else if (currentPlayerId.equals(savedGame.getPlayer2Id())) {
            // Ходит второй игрок — его символ
            expectedSymbol = savedGame.getPlayer2Symbol() == 'X' ? 1 : 2;
        } else {
            throw new ValidateGameException("Неизвестный игрок");
        }

        // Проверяем, что игрок поставил правильный символ
        int placedSymbol = proposedMatrix[changedRow][changedCol];
        if (placedSymbol != expectedSymbol) {
            throw new ValidateGameException(
                    "Игрок должен поставить " +
                            (expectedSymbol == 1 ? "X (1)" : "O (2)") +
                            ", а поставил " + placedSymbol
            );
        }

        return true;
    }

    /**
     * Основной метод, который делает ход за компьютер(2)
     */
    @Override
    public Game makeComputerMove(Game game) {
        if (game.isGameOver()) {
            throw new IllegalStateException("Game is already over");
        }
/*
        // Проверяем победу игрока приславшего поле со своим ходом
        int winner = checkWinner(game.getBoard().getMatrix());
        if (winner != EMPTY) {
            game.setGameOver(true);
            game.setWinner(winner);
            gameRepository.save(gameDataMapper.toData(game));
            return game;
        }

        // Получаем текущего игрока, кто прислал ход, и меняем на противоположного.
        int currentPlayer = game.getCurrentPlayer() == PLAYER ? COMPUTER : PLAYER;


        // Находим лучший ход для компа с помощью минимакса
        Move bestMove = findBestMove(game, currentPlayer);

        if (bestMove != null) {
            game.setMove(bestMove);
        }

        // Проверяем победу
        winner = checkWinner(game.getBoard().getMatrix());
        if (winner != EMPTY) {
            game.setGameOver(true);
            game.setWinner(winner);
            gameRepository.save(gameDataMapper.toData(game));
            return game;
        }

        // Проверяем ничью (все клетки заполнены)
        if (isBoardFull(game.getBoard().getMatrix())) {
            game.setGameOver(true);
            game.setWinner(0); // ничья
            gameRepository.save(gameDataMapper.toData(game));
            return game;
        }

        // Если игра не закончена, меняем игрока. Кто сделал текущий ход
        game.setCurrentPlayer(currentPlayer);
*/
        // Сохраняем новое состояние игры
        gameRepository.save(gameDataMapper.toData(game));

        return game;
    }

    private Move findBestMove(Game game, int player) {
        int[][] board = game.getBoard().getMatrix();
        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = player;

                    if (checkWinner(board) == player) {
                        board[i][j] = EMPTY;  // отменяем ход перед возвратом
                        return new Move(i, j, player == PLAYER ? ZeroCross.CROSS : ZeroCross.ZERO);
                    }

                    // Передаем в минимакс противоположного игрока
                    int nextPlayer = (player == PLAYER) ? COMPUTER : PLAYER;
                    int score = minimax(board, 0, nextPlayer);

                    board[i][j] = EMPTY;

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new Move(i, j,
                                player ==  PLAYER ? ZeroCross.CROSS : ZeroCross.ZERO);
                    }
                }
            }
        }

        return bestMove;
    }

    private int minimax(int[][] board, int depth, int player) {
        // Проверяем терминальные состояния
        int winner = checkWinner(board);

        if (winner == COMPUTER) return 10 - depth;  // победа компьютера
        if (winner == PLAYER) return -10 + depth;   // победа игрока
        if (isBoardFull(board)) return 0;  // ничья

        if (player == PLAYER) {
            // Ход игрока - он МАКСИМИЗИРУЕТ свою выгоду
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = PLAYER;
                        int score = minimax(board, depth + 1, COMPUTER);
                        board[i][j] = EMPTY;
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            // Ход компьютера - он МИНИМИЗИРУЕТ выгоду игрока
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = COMPUTER;
                        int score = minimax(board, depth + 1, PLAYER);
                        board[i][j] = EMPTY;
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }

    private int checkWinner(int[][] board) {
        // Проверка строк
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != EMPTY && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return board[i][0];
            }
        }

        // Проверка столбцов
        for (int j = 0; j < 3; j++) {
            if (board[0][j] != EMPTY && board[0][j] == board[1][j] && board[1][j] == board[2][j]) {
                return board[0][j];
            }
        }

        // Проверка диагоналей
        if (board[0][0] != EMPTY && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return board[0][0];
        }
        if (board[0][2] != EMPTY && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return board[0][2];
        }

        return EMPTY; // нет победителя
    }

    private boolean isBoardFull(int[][] board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }
}