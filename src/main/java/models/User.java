package models;

import java.util.UUID;

public class User {
    private String uuid;
    private String login;
    private String password;
    private Wallet wallet; // Кошелёк пользователя

    public User(String login, String password) {
        this.uuid = UUID.randomUUID().toString();
        this.login = login;
        this.password = password;
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

    public Wallet getWallet() {
        return wallet;
    }

    public void createWallet() {
        if (this.wallet == null) {
            this.wallet = new Wallet();
            // Добавляем базовые категории
            this.wallet.addCategory(new Category("Food"));
            this.wallet.addCategory(new Category("Transport"));
            this.wallet.addCategory(new Category("Entertainment"));
            this.wallet.addCategory(new Category("Utilities"));
            this.wallet.addCategory(new Category("Salary"));
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
