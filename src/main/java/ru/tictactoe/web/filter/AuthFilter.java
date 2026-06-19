package ru.tictactoe.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import ru.tictactoe.domain.service.AuthService;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

public class AuthFilter extends GenericFilterBean {

    private final AuthService authService;

    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request; // Информация о запросе
        HttpServletResponse httpResponse = (HttpServletResponse) response; // для формирования ответа

        String path = httpRequest.getRequestURI();

        // Пропускаем эндпоинты регистрации и логина
        if (path.equals("/auth/register") || path.equals("/auth/login")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            sendUnauthorized(httpResponse, "Authorization header is missing or invalid");
            return;
        }

        try {
            UUID userId = authService.authorize(authHeader);

            // Создаем обьект аутентификации
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userId.toString(),  // principal — userId
                    null,               // credentials — пароль не храним
                    Collections.emptyList()  // authorities — пустой список ролей
            );

            // Сохраняем его в SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Добавляем userId в атрибуты запроса
            httpRequest.setAttribute("userId", userId);

            // Передаем запрос дальше в логику домена
            chain.doFilter(request, response);

        } catch (RuntimeException e) {
            sendUnauthorized(httpResponse, e.getMessage());
        }
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
        response.getWriter().flush();
    }
}