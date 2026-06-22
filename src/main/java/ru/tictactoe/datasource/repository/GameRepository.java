package ru.tictactoe.datasource.repository;

import org.springframework.data.repository.CrudRepository;
import ru.tictactoe.datasource.model.GameData;
import ru.tictactoe.domain.model.GameStatus;

import java.util.List;
import java.util.UUID;

public interface GameRepository extends CrudRepository<GameData, UUID> {
    // Найти все игры с указанным статусом
    List<GameData> findAllByGameStatus(GameStatus status);

    // Найти игры, где пользователь является player1 или player2
    List<GameData> findAllByPlayer1IdOrPlayer2Id(UUID player1Id, UUID player2Id);
}