package models;

import java.util.UUID;

public class User {
    private String uuid;
    private String login;
    private String password;

    public User(String login, String password) {
        this.uuid = UUID.randomUUID().toString();
        this.login = login;
        this.password = password;
    }

    public String getUuid() {
        return uuid;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
