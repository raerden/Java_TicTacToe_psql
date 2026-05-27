package ru.tictactoe.datasource.mapper;

import org.springframework.stereotype.Component;
import ru.tictactoe.domain.model.User;
import ru.tictactoe.datasource.entity.UserEntity;

@Component
public class UserDataMapper {

    public UserEntity toEntity(User user) {
        if (user == null) return null;
        return new UserEntity(user.getId(), user.getLogin(), user.getPassword());
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;
        return new User(entity.getId(), entity.getLogin(), entity.getPassword());
    }
}