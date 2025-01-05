package auth;

import models.User;
import java.util.*;

public class AuthenticationService {
    private Map<String, User> users;
    private User currentUser;

    public AuthenticationService() {
        this.users = new HashMap<>();
    }

    // Регистрация нового пользователя
    public boolean register(String login, String password) {
        if (users.containsKey(login)) {
            return false;
        }
        User newUser = new User(login, password);
        users.put(login, newUser);
        return true;
    }

    // Авторизация пользователя
    public boolean login(String login, String password) {
        User user = users.get(login);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    // Получить текущего авторизованного пользователя
    public User getCurrentUser() {
        return currentUser;
    }

    // Получить пользователя по логину
    public User getUserByLogin(String login) {
        return users.get(login);
    }

    // Завершить текущую сессию
    public void logout() {
        currentUser = null;
    }
}