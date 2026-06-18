package ru.tictactoe.di;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Отключаем CSRF (для REST API)
                .csrf(AbstractHttpConfigurer::disable)

                // Настраиваем авторизацию
                .authorizeHttpRequests(auth -> auth
                        // Разрешаем доступ без авторизации к регистрации и логину
                        .requestMatchers("/auth/register", "/auth/login").permitAll()
                        // Все остальные запросы требуют авторизации
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}