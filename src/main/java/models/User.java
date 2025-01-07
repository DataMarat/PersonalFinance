package models;

import utils.CategoryManager;
import java.util.UUID;

public class User {
    private String uuid;
    private String login;
    private String password;
    private CategoryManager categoryManager;
    private Wallet wallet; // Кошелёк пользователя

    public User(String login, String password) {
        this.uuid = java.util.UUID.randomUUID().toString();
        this.login = login;
        this.password = password;
        this.categoryManager = new CategoryManager();
        this.wallet = null; // Изначально кошелёк отсутствует
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

    public void setCategoryManager(CategoryManager categoryManager) {
        this.categoryManager = categoryManager;
    }
    public Wallet getWallet() {
        return wallet;
    }

    public void createWallet() {
        if (this.wallet == null) {
            if (this.categoryManager == null) {
                throw new IllegalStateException("CategoryManager is not initialized for this user.");
            }
            this.wallet = new Wallet(this.categoryManager);
        }
    }

    // Пересчёт баланса
    public void recalculateBalance() {
        if (wallet != null) {
            double balance = wallet.getOperations().stream()
                    .mapToDouble(op -> op.getType() == OperationType.INCOME ? op.getAmount() : -op.getAmount())
                    .sum();
            wallet.setBalance(balance);
        }
    }
    // Наличие кошелька
    public boolean hasWallet() {
        return wallet != null;
    }
}