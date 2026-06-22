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

        // Измененная клетка была пустой
        if (savedMatrix[changedRow][changedCol] != 0) {
            throw new ValidateGameException("Данная клетка занята!");
        }

        // Игрок поставил крестик (1)
        if (proposedMatrix[changedRow][changedCol] != 1) {
            throw new ValidateGameException("Игрок прислал не 1 (крестик)!");
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