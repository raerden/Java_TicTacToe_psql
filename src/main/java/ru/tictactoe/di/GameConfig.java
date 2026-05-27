package ru.tictactoe.di;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tictactoe.datasource.mapper.GameDataMapper;
import ru.tictactoe.datasource.repository.GameRepository;
import ru.tictactoe.datasource.repository.UserRepository;
import ru.tictactoe.domain.service.GameService;
import ru.tictactoe.domain.service.GameServiceImpl;
import ru.tictactoe.web.mapper.WebMapper;

@Configuration
public class GameConfig {

    @Bean
    public GameDataMapper gameDataMapper() {
        // Маппер для преобразования между domain и datasource слоями
        return new GameDataMapper();
    }

    @Bean
    public GameService gameService(
            GameRepository gameRepository,
            GameDataMapper gameDataMapper
    ) {
        // Сервис зависит от репозитория и маппера данных
        return new GameServiceImpl(gameRepository, gameDataMapper);
    }

    @Bean
    public WebMapper webMapper() {
        // Маппер для преобразования между domain и web слоями
        return new WebMapper();
    }
}