package models;

import utils.CategoryManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Wallet {
    private List<Operation> operations;
    private double balance;
    private CategoryManager categoryManager;

    public Wallet(CategoryManager categoryManager) {
        this.categoryManager = categoryManager;
        this.operations = new ArrayList<>();
        this.balance = 0.0;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void addOperation(Operation operation) {
        this.operations.add(operation);
        if (operation.getType() == OperationType.INCOME) {
            this.balance += operation.getAmount();
        } else if (operation.getType() == OperationType.EXPENSE) {
            this.balance -= operation.getAmount();
        }
        checkBudgetBalance(); // Проверяем баланс после добавления операции
    }

    public void recalculateBalance() {
        this.balance = operations.stream()
                .mapToDouble(op -> op.getType() == OperationType.INCOME ? op.getAmount() : -op.getAmount())
                .sum();
    }

    public CategoryManager getCategoryManager() {
        return categoryManager;
    }

    public void setCategoryManager(CategoryManager categoryManager) {
        this.categoryManager = categoryManager;
    }

    public List<Operation> filterOperations(String category, Date fromDate, Date toDate) {
        return operations.stream()
                .filter(op -> (category == null || op.getCategory().equalsIgnoreCase(category)) &&
                        (fromDate == null || !op.getDate().before(fromDate)) &&
                        (toDate == null || !op.getDate().after(toDate)))
                .collect(Collectors.toList());
    }

    public double[] calculateTotals(Date fromDate, Date toDate) {
        double income = 0;
        double expense = 0;

        for (Operation operation : operations) {
            if ((fromDate == null || !operation.getDate().before(fromDate)) &&
                    (toDate == null || !operation.getDate().after(toDate))) {
                if (operation.getType() == OperationType.INCOME) {
                    income += operation.getAmount();
                } else if (operation.getType() == OperationType.EXPENSE) {
                    expense += operation.getAmount();
                }
            }
        }

        return new double[]{income, expense};
    }

    public Map<String, Double> calculateCategoryExpenses(Date fromDate, Date toDate) {
        Map<String, Double> categoryExpenses = new HashMap<>();

        for (Operation operation : operations) {
            if (operation.getType() == OperationType.EXPENSE &&
                    (fromDate == null || !operation.getDate().before(fromDate)) &&
                    (toDate == null || !operation.getDate().after(toDate))) {

                String category = operation.getCategory();
                double amount = operation.getAmount();
                categoryExpenses.put(category, categoryExpenses.getOrDefault(category, 0.0) + amount);
            }
        }

        return categoryExpenses;
    }

    public void checkBudgetBalance() {
        double totalIncome = operations.stream()
                .filter(op -> op.getType() == OperationType.INCOME)
                .mapToDouble(Operation::getAmount)
                .sum();

        double totalExpense = operations.stream()
                .filter(op -> op.getType() == OperationType.EXPENSE)
                .mapToDouble(Operation::getAmount)
                .sum();

        if (totalExpense > totalIncome) {
            System.out.println("Warning: Your expenses exceed your income!");
        }
    }
    public boolean transferAmount(double amount) {
        if (balance >= amount) {
            balance -= amount;
            return true; // Успешный перевод
        }
        return false; // Недостаточно средств
    }

    public void receiveAmount(double amount, String category) {
        balance += amount;
        operations.add(new Operation(OperationType.INCOME, amount, category));
    }

}
