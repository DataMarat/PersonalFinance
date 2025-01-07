package models;

import java.util.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import utils.CategoryManager;

public class Wallet {
    private List<Operation> operations; // Список операций
    private double balance; // Баланс кошелька
    private CategoryManager categoryManager;

    public Wallet(CategoryManager categoryManager) {
        if (categoryManager == null) {
            throw new IllegalArgumentException("CategoryManager cannot be null");
        }
        this.categoryManager = categoryManager;
        this.operations = new ArrayList<>();
        this.balance = 0.0; // Изначальный баланс
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void addOperation(Operation operation) {
        if (this.categoryManager == null) {
            throw new IllegalStateException("CategoryManager is not initialized in Wallet.");
        }

        this.operations.add(operation);

        // Обновляем баланс
        if (operation.getType() == OperationType.INCOME) {
            this.balance += operation.getAmount();
        } else if (operation.getType() == OperationType.EXPENSE) {
            this.balance -= operation.getAmount();
        }

        // Проверка превышения лимитов
        if (operation.getType() == OperationType.EXPENSE) {
            String category = operation.getCategory();
            double used = operations.stream()
                    .filter(op -> op.getType() == OperationType.EXPENSE && op.getCategory().equalsIgnoreCase(category))
                    .mapToDouble(Operation::getAmount)
                    .sum();

            double limit = getCategoryLimit(category);
            if (limit > 0 && used > limit) {
                System.out.println("Warning: You have exceeded the budget limit for category: " + category);
            }
        }
    }


    private double getCategoryLimit(String category) {
        return categoryManager.getAllCategories().stream()
                .filter(c -> c.getName().equalsIgnoreCase(category))
                .findFirst()
                .map(Category::getLimit)
                .orElse(0.0);
    }

    public List<Operation> filterOperations(String category, Date fromDate, Date toDate) {
        return operations.stream()
                .filter(op -> (category == null || op.getCategory().equalsIgnoreCase(category)) &&
                        (fromDate == null || !op.getDate().before(fromDate)) &&
                        (toDate == null || !op.getDate().after(toDate)))
                .collect(Collectors.toList());
    }

    // Подсчёт общих доходов и расходов
    public double[] calculateTotals(Date fromDate, Date toDate) {
        double income = 0;
        double expense = 0;

        for (Operation op : operations) {
            if ((fromDate == null || !op.getDate().before(fromDate)) &&
                    (toDate == null || !op.getDate().after(toDate))) {
                if (op.getType() == OperationType.INCOME) {
                    income += op.getAmount();
                } else {
                    expense += op.getAmount();
                }
            }
        }
        return new double[]{income, expense};
    }

    // Распределение расходов по категориям
    public Map<String, Double> calculateCategoryExpenses(Date fromDate, Date toDate) {
        Map<String, Double> categoryExpenses = new HashMap<>();

        for (Operation op : operations) {
            if (op.getType() == OperationType.EXPENSE &&
                    (fromDate == null || !op.getDate().before(fromDate)) &&
                    (toDate == null || !op.getDate().after(toDate))) {
                categoryExpenses.put(op.getCategory(),
                        categoryExpenses.getOrDefault(op.getCategory(), 0.0) + op.getAmount());
            }
        }
        return categoryExpenses;
    }
    public void checkBudgetBalance() {
        double income = operations.stream()
                .filter(op -> op.getType() == OperationType.INCOME)
                .mapToDouble(Operation::getAmount)
                .sum();

        double expense = operations.stream()
                .filter(op -> op.getType() == OperationType.EXPENSE)
                .mapToDouble(Operation::getAmount)
                .sum();

        if (expense > income) {
            System.out.println("Warning: Your expenses exceed your income!");
        }
    }
    public void setCategoryManager(CategoryManager categoryManager) {
        this.categoryManager = categoryManager;
    }
}
