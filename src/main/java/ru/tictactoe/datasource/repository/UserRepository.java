package ru.tictactoe.datasource.repository;

import org.springframework.data.repository.CrudRepository;
import ru.tictactoe.datasource.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<UserEntity, UUID> {
    Optional<UserEntity> findByLogin(String login); //Спринг сгенерирует запрос в БД "SELECT * FROM users WHERE login = "
    boolean existsByLogin(String login); // Генерирует SELECT COUNT(*) FROM users WHERE login = ? (и возвращает true если > 0)
}

/*
Правила именования:
Шаблон	Генерируемый SQL
findBy{FieldName}	WHERE field_name = ?
findBy{FieldName}Containing	WHERE field_name LIKE %?%
findBy{FieldName}IgnoreCase	WHERE UPPER(field_name) = UPPER(?)
countBy{FieldName}	SELECT COUNT(*) ...
deleteBy{FieldName}	DELETE ... WHERE field_name = ?

 Это называется Convention over Configuration — соглашение вместо конфигурации.
 Если называешь методы правильно, Spring делает всю работу сам

примеры:
Optional<UserEntity> findByLoginIgnoreCase(String login);

*/