package ru.tictactoe.domain.service;

import org.springframework.stereotype.Service;
import ru.tictactoe.datasource.entity.UserEntity;
import ru.tictactoe.datasource.mapper.UserDataMapper;
import ru.tictactoe.datasource.repository.UserRepository;
import ru.tictactoe.domain.model.User;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDataMapper userDataMapper;

    public UserServiceImpl(UserRepository userRepository, UserDataMapper userDataMapper) {
        this.userRepository = userRepository;
        this.userDataMapper = userDataMapper;
    }

    @Override
    public User register(String login, String password) {
        // Проверка, существует ли уже такой логин
        if (userRepository.existsByLogin(login)) {
            throw new RuntimeException("User with login '" + login + "' already exists");
        }

        User user = new User(UUID.randomUUID(), login, password);
        UserEntity entity = userDataMapper.toEntity(user);
        UserEntity saved = userRepository.save(entity);
        return userDataMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login)
                .map(userDataMapper::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id)
                .map(userDataMapper::toDomain);
    }

    @Override
    public boolean existsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }
}