package models;

import java.util.ArrayList;
import java.util.List;

public class Wallet {
    private List<Operation> operations; // Список операций
    private double balance; // Баланс кошелька

    public Wallet() {
        this.operations = new ArrayList<>();
        this.balance = 0.0; // Изначальный баланс
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void addOperation(Operation operation) {
        this.operations.add(operation);
        if (operation.getType() == OperationType.INCOME) {
            this.balance += operation.getAmount();
        } else if (operation.getType() == OperationType.EXPENSE) {
            this.balance -= operation.getAmount();
        }
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
