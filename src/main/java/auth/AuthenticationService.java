package auth;

import models.User;
import java.util.List;

public class AuthenticationService {
    private List<User> users;
    private User currentUser;

    // Передаём список пользователей через конструктор
    public AuthenticationService(List<User> users) {
        this.users = users;
        this.currentUser = null;
    }

    public boolean login(String login, String password) {
        for (User user : users) {
            if (user.getLogin().equalsIgnoreCase(login) && user.getPassword().equals(password)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
