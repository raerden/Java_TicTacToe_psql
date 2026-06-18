package ru.tictactoe.domain.service;

import org.springframework.stereotype.Service;
import ru.tictactoe.domain.model.User;
import ru.tictactoe.web.model.SignUpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    public AuthServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean register(SignUpRequest request) {
        String login = request.getLogin();
        String password = request.getPassword();

        // Проверяем, не занят ли логин
        if (userService.existsByLogin(login)) {
            throw new RuntimeException("User with login '" + login + "' already exists");
        }

        // Сохраняем пользователя
        User user = userService.register(login, password);
        return user != null;
    }

    @Override
    public UUID authorize(String authHeader) {
        // 1. Проверяем, что заголовок не пустой и начинается с "Basic "
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        // 2. Извлекаем base64-строку (после "Basic ")
        String base64Credentials = authHeader.substring(6).trim();

        // 3. Декодируем base64 → "login:password"
        byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(decodedBytes, StandardCharsets.UTF_8);

        // 4. Разделяем логин и пароль по ":"
        String[] parts = credentials.split(":", 2);
        if (parts.length != 2) {
            throw new RuntimeException("Invalid credentials format");
        }

        String login = parts[0];
        String password = parts[1];

        // 5. Ищем пользователя в БД
        Optional<User> userOpt = userService.findByLogin(login);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found: " + login);
        }

        User user = userOpt.get();

        // 6. Проверяем пароль (пока без хеширования, простое сравнение)
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // 7. Возвращаем UUID пользователя
        return user.getId();
    }
}