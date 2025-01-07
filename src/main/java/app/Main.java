package app;

import auth.AuthenticationService;
import models.*;
import storage.DataStorage;
import utils.Constants;
import utils.Menu;

import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<User> users = DataStorage.loadData();
        if (users == null) {
            users = new ArrayList<>();
        }

        AuthenticationService authService = new AuthenticationService(users);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            Menu menu = new Menu(
                    authService.getCurrentUser() != null,
                    authService.getCurrentUser() != null && authService.getCurrentUser().getWallet() != null
            );

            if (authService.getCurrentUser() != null) {
                menu.displayHint();
            } else {
                menu.displayMenu();
            }

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "r": // Регистрация нового пользователя
                    handleRegistration(users, authService, scanner);
                    break;

                case "l": // Вход в систему
                    handleLogin(authService, scanner);
                    break;

                case "o": // Выход из текущего аккаунта
                    handleLogout(authService);
                    break;

                case "c": // Создать кошелёк
                    handleCreateWallet(authService, scanner);
                    break;

                case "w": // Перейти в кошелёк
                    handleEnterWallet(authService, scanner);
                    break;

                case "a": // Добавить новую категорию
                    handleAddCategory(authService, scanner);
                    break;

                case "v": // Просмотреть список категорий
                    handleViewCategories(authService);
                    break;

                case "i": // Установить лимит для категории
                    handleSetCategoryLimit(authService, scanner);
                    break;

                case "+": // Добавить доход
                case "-": // Добавить расход
                    handleAddOperation(authService, scanner, choice.equals("+") ? OperationType.INCOME : OperationType.EXPENSE);
                    break;

                case "f": // Фильтровать операции по критериям
                    handleFilterOperations(authService, scanner);
                    break;

                case "s": // Показать статистику
                    handleShowStatistics(authService, scanner);
                    break;

                case "t": // Перевести средства другому пользователю
                    handleTransfer(authService, users, scanner);
                    break;

                case "m": // Показать полное меню
                    handleShowFullMenu(authService, menu);
                    break;

                case "q": // Завершить программу
                    handleQuit(users, scanner);
                    return;

                default: // Некорректная команда
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void handleRegistration(List<User> users, AuthenticationService authService, Scanner scanner) {
        System.out.print("Enter login: ");
        String login = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (users.stream().anyMatch(u -> u.getLogin().equalsIgnoreCase(login))) {
            System.out.println("Registration failed. Login already exists.");
        } else {
            User newUser = new User(login, password);
            users.add(newUser);
            DataStorage.saveData(users);
            authService.login(login, password);
            System.out.println("Registration successful! You are now logged in as: " + login + ", your ID: " + newUser.getUuid());
        }
    }

    private static void handleLogin(AuthenticationService authService, Scanner scanner) {
        if (authService.getCurrentUser() != null) {
            System.out.println("Logging out current user: " + authService.getCurrentUser().getLogin());
            authService.logout();
        }

        System.out.print("Enter login: ");
        String login = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (authService.login(login, password)) {
            User loggedInUser = authService.getCurrentUser();
            System.out.println("Login successful! You are now logged in as: " + login + ", your ID: " + loggedInUser.getUuid());
        } else {
            System.out.println("Invalid login or password.");
        }
    }


    private static void handleLogout(AuthenticationService authService) {
        if (authService.getCurrentUser() != null) {
            System.out.println("Goodbye, " + authService.getCurrentUser().getLogin() + "!");
            authService.logout();
        } else {
            System.out.println("No user is currently logged in.");
        }
    }

    private static void handleCreateWallet(AuthenticationService authService, Scanner scanner) {
        User currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getWallet() == null) {
                currentUser.createWallet();
                System.out.println("Wallet created successfully!");
                handleEnterWallet(authService, scanner);
            } else {
                System.out.println("You already have a wallet.");
            }
        } else {
            System.out.println("You must be logged in to create a wallet.");
        }
    }

    private static void handleEnterWallet(AuthenticationService authService, Scanner scanner) {
        User currentUser = authService.getCurrentUser();
        if (currentUser != null && currentUser.getWallet() != null) {
            Wallet wallet = currentUser.getWallet();
            System.out.println("\nWelcome to your Wallet!");
            System.out.println("Balance: " + wallet.getBalance());
            List<Operation> operations = wallet.getOperations();
            if (operations.isEmpty()) {
                System.out.println("No operations found.");
            } else {
                operations.forEach(op -> {
                    String prefix = op.getType() == OperationType.INCOME ? "+" : "-";
                    System.out.println(prefix + " " + op.getAmount() + " " + op.getCategory());
                });
            }
            System.out.println("Returning to main menu...");
        } else {
            System.out.println("You must create a wallet first.");
        }
    }

    private static void handleAddCategory(AuthenticationService authService, Scanner scanner) {
        User currentUser = authService.getCurrentUser();
        if (currentUser != null && currentUser.getWallet() != null) {
            System.out.print("Enter the name of the new category: ");
            String categoryName = scanner.nextLine();
            currentUser.getWallet().addCategory(new Category(categoryName));
            System.out.println("Category added successfully: " + categoryName);
        } else {
            System.out.println("You must be logged in and have a wallet to add categories.");
        }
    }

    private static void handleViewCategories(AuthenticationService authService) {
        User currentUser = authService.getCurrentUser();
        if (currentUser != null && currentUser.getWallet() != null) {
            List<Category> categories = currentUser.getWallet().getCategories();
            if (categories.isEmpty()) {
                System.out.println("No categories available.");
            } else {
                System.out.println("Available categories:");
                categories.forEach(category -> System.out.println("- " + category));
            }
        } else {
            System.out.println("You must be logged in and have a wallet to view categories.");
        }
    }


    private static void handleSetCategoryLimit(AuthenticationService authService, Scanner scanner) {
        User currentUser = authService.getCurrentUser();
        if (currentUser != null && currentUser.getWallet() != null) {
            System.out.println("Available categories:");
            List<Category> categories = currentUser.getWallet().getCategories();
            for (Category category : categories) {
                System.out.println("- " + category.getName());
            }

            System.out.print("Enter category name: ");
            String categoryName = scanner.nextLine();
            Category selectedCategory = categories.stream()
                    .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                    .findFirst()
                    .orElse(null);

            if (selectedCategory != null) {
                System.out.print("Enter new limit for " + categoryName + ": ");
                double newLimit = Double.parseDouble(scanner.nextLine());
                selectedCategory.setLimit(newLimit);
                System.out.println("Limit updated successfully for " + categoryName);
            } else {
                System.out.println("Category not found.");
            }
        } else {
            System.out.println("You must be logged in and have a wallet to set limits.");
        }
    }

    private static void handleAddOperation(AuthenticationService authService, Scanner scanner, OperationType type) {
        User currentUser = authService.getCurrentUser();
        if (currentUser != null && currentUser.getWallet() != null) {
            Wallet wallet = currentUser.getWallet();
            System.out.println("Available categories:");
            wallet.getCategories().forEach(c -> System.out.println("- " + c.getName()));
            System.out.print("Enter category: ");
            String categoryName = scanner.nextLine();
            System.out.print("Enter amount: ");
            double amount = Double.parseDouble(scanner.nextLine());
            wallet.addOperation(new Operation(type, amount, categoryName));
            System.out.println((type == OperationType.INCOME ? "Income" : "Expense") + " added successfully.");
        } else {
            System.out.println("You must be logged in and have a wallet to perform this operation.");
        }
    }

    private static void handleFilterOperations(AuthenticationService authService, Scanner scanner) {
        User currentUser = authService.getCurrentUser();
        if (currentUser != null && currentUser.getWallet() != null) {
            Wallet wallet = currentUser.getWallet();
            System.out.print("Enter category (or leave blank): ");
            String category = scanner.nextLine().trim();
            System.out.print("Enter start date (yyyy-MM-dd) or leave blank: ");
            String startDateInput = scanner.nextLine();
            System.out.print("Enter end date (yyyy-MM-dd) or leave blank: ");
            String endDateInput = scanner.nextLine();
            Date fromDate = null, toDate = null;
            try {
                if (!startDateInput.isEmpty()) {
                    fromDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateInput);
                }
                if (!endDateInput.isEmpty()) {
                    toDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateInput);
                }
            } catch (Exception e) {
                System.out.println("Invalid date format.");
            }
            List<Operation> filtered = wallet.filterOperations(category.isEmpty() ? null : category, fromDate, toDate);
            if (filtered.isEmpty()) {
                System.out.println("No operations found.");
            } else {
                System.out.println("Filtered operations:");
                filtered.forEach(op -> System.out.println(op));
            }
        } else {
            System.out.println("You must be logged in and have a wallet to filter operations.");
        }
    }

    private static void handleShowStatistics(AuthenticationService authService, Scanner scanner) {
        User currentUser = authService.getCurrentUser();
        if (currentUser != null && currentUser.getWallet() != null) {
            Wallet wallet = currentUser.getWallet();
            System.out.print("Enter start date (yyyy-MM-dd) or leave blank: ");
            String startDateInput = scanner.nextLine();
            System.out.print("Enter end date (yyyy-MM-dd) or leave blank: ");
            String endDateInput = scanner.nextLine();
            Date fromDate = null, toDate = null;
            try {
                if (!startDateInput.isEmpty()) {
                    fromDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateInput);
                }
                if (!endDateInput.isEmpty()) {
                    toDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateInput);
                }
            } catch (Exception e) {
                System.out.println("Invalid date format.");
            }
            double[] totals = wallet.calculateTotals(fromDate, toDate);
            System.out.println("Total Income: " + totals[0]);
            System.out.println("Total Expense: " + totals[1]);
            Map<String, Double> expenses = wallet.calculateCategoryExpenses(fromDate, toDate);
            System.out.println("Category-wise expenses:");
            expenses.forEach((k, v) -> System.out.println("- " + k + ": " + v));
        } else {
            System.out.println("You must be logged in and have a wallet to view statistics.");
        }
    }

    private static void handleTransfer(AuthenticationService authService, List<User> users, Scanner scanner) {
        User sender = authService.getCurrentUser();
        if (sender != null && sender.hasWallet()) {
            System.out.print("Enter recipient's UUID: ");
            String recipientUUID = scanner.nextLine().trim();
            User recipient = users.stream().filter(u -> u.getUuid().equals(recipientUUID)).findFirst().orElse(null);
            if (recipient == null || !recipient.hasWallet()) {
                System.out.println("Recipient not found or does not have a wallet.");
                return;
            }
            System.out.print("Enter transfer amount: ");
            double amount = Double.parseDouble(scanner.nextLine());
            if (sender.getWallet().transferAmount(amount)) {
                recipient.getWallet().receiveAmount(amount, "Transfer from " + sender.getLogin());
                sender.getWallet().addOperation(new Operation(OperationType.EXPENSE, amount, "Transfer to " + recipient.getLogin()));
                System.out.println("Transfer completed successfully.");
                DataStorage.saveData(users);
            } else {
                System.out.println("Insufficient funds.");
            }
        } else {
            System.out.println("You must be logged in and have a wallet to perform transfers.");
        }
    }

    private static void handleShowFullMenu(AuthenticationService authService, Menu menu) {
        if (authService.getCurrentUser() != null) {
            menu.displayMenu();
        } else {
            System.out.println("You must be logged in to view the full menu.");
        }
    }

    private static void handleQuit(List<User> users, Scanner scanner) {
        System.out.println("Exiting the application. Goodbye!");
        DataStorage.saveData(users);
        scanner.close();
    }
}
