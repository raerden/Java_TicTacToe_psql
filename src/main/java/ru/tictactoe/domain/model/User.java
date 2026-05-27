package ru.tictactoe.domain.model;

import java.util.UUID;

public class User {
    private UUID id;
    private String login;
    private String password;  // будет хранить зашифрованный пароль

    public User() {}

    public User(UUID id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    // Геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}