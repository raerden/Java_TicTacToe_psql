package ru.tictactoe.datasource.repository;

import org.springframework.data.repository.CrudRepository;
import ru.tictactoe.datasource.model.GameData;
import java.util.UUID;

public interface GameRepository extends CrudRepository<GameData, UUID> {
    // Никакой реализации! Spring сделает всё сам.
}