package ru.tictactoe.domain.service;

import ru.tictactoe.web.model.SignUpRequest;
import java.util.UUID;

public interface AuthService {

    /**
     * Регистрация нового пользователя
     * @param request логин и пароль
     * @return true если регистрация успешна
     * @throws RuntimeException если пользователь с таким логином уже существует
     */
    boolean register(SignUpRequest request);

    /**
     * Авторизация пользователя по Basic Auth заголовку
     * @param authHeader заголовок Authorization (формат: "Basic base64(login:password)")
     * @return UUID пользователя
     * @throws RuntimeException если логин или пароль неверны
     */
    UUID authorize(String authHeader);
}