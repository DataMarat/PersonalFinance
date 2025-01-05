package models;

import utils.CategoryManager;

public class User {
    private String uuid;
    private String login;
    private String password;
    private CategoryManager categoryManager;

    public User(String login, String password) {
        this.uuid = java.util.UUID.randomUUID().toString();
        this.login = login;
        this.password = password;
        this.categoryManager = new CategoryManager();
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

    public CategoryManager getCategoryManager() {
        return categoryManager;
    }
}