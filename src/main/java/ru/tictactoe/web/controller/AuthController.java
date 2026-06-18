package ru.tictactoe.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tictactoe.domain.service.AuthService;
import ru.tictactoe.web.model.SignUpRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Регистрация нового пользователя
     * POST /auth/register
     *
     * Request body: { "login": "user", "password": "pass" }
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignUpRequest request) {
        try {
            boolean success = authService.register(request);
            if (success) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "User registered successfully");
                response.put("login", request.getLogin());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Registration failed"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Авторизация пользователя (Basic Auth)
     * POST /auth/login
     *
     * Заголовок: Authorization: Basic base64(login:password)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestHeader("Authorization") String authHeader) {
        try {
            UUID userId = authService.authorize(authHeader);

            Map<String, String> response = new HashMap<>();
            response.put("userId", userId.toString());
            response.put("message", "Login successful");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}