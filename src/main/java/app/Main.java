package app;

import auth.AuthenticationService;
import models.User;
import models.Category;
import models.Wallet;
import models.Operation;
import models.OperationType;
import storage.DataStorage;
import utils.Menu;
import utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Загрузка данных из файла либо старт с нуля
        List<User> users = DataStorage.loadData();
        if (users != null) {
            users.forEach(User::recalculateBalance); // Пересчёт балансов
        } else {
            users = new ArrayList<>();
        }

        AuthenticationService authService = new AuthenticationService(users);
        Scanner scanner = new Scanner(System.in);

        String login;
        String password;

        while (true) {
            Menu menu = new Menu(
                    authService.getCurrentUser() != null,
                    authService.getCurrentUser() != null && authService.getCurrentUser().getWallet() != null
            );
            menu.displayMenu();

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "r": // Регистрация
                    System.out.print("Enter login: ");
                    login = scanner.nextLine();
                    System.out.print("Enter password: ");
                    password = scanner.nextLine();

                    // Проверяем уникальность логина
                    boolean loginExists = false;
                    for (User user : users) {
                        if (user.getLogin().equalsIgnoreCase(login)) {
                            loginExists = true;
                            break;
                        }
                    }

                    if (loginExists) {
                        System.out.println("Registration failed. Login already exists.");
                    } else {
                        // Создаём нового пользователя и добавляем в список
                        User newUser = new User(login, password);
                        users.add(newUser);

                        // Сохраняем данные после регистрации
                        DataStorage.saveData(users);

                        // Автоматический вход в систему
                        authService.login(login, password);

                        System.out.println("Registration successful! You are now logged in as: " + login + ", your ID: " + newUser.getUuid());
                    }
                    break;


                case "l": // Вход или смена пользователя
                    if (authService.getCurrentUser() != null) {
                        System.out.println("Logging out current user: " + authService.getCurrentUser().getLogin());
                        authService.logout();
                    }

                    System.out.print("Enter login: ");
                    login = scanner.nextLine();
                    System.out.print("Enter password: ");
                    password = scanner.nextLine();

                    if (authService.login(login, password)) {
                        User loggedInUser = authService.getCurrentUser();
                        System.out.println("Login successful! You are now logged in as: " + login + ", your ID: " + loggedInUser.getUuid());
                    } else {
                        System.out.println("Invalid login or password.");
                    }
                    break;

                case "o": // Выход из системы
                    if (authService.getCurrentUser() != null) {
                        System.out.println("Goodbye, " + authService.getCurrentUser().getLogin() + "!");
                        authService.logout();
                    } else {
                        System.out.println("No user is currently logged in.");
                    }
                    break;

                case "c": // Создать кошелек
                    if (authService.getCurrentUser() != null) {
                        User currentUser = authService.getCurrentUser();
                        if (currentUser.getWallet() == null) {
                            currentUser.createWallet();
                            System.out.println("Wallet created successfully! You are now in your wallet.");
                            enterWallet(scanner, currentUser.getWallet());
                        } else {
                            System.out.println("You already have a wallet.");
                        }
                    } else {
                        System.out.println("You must be logged in to create a wallet.");
                    }
                    break;

                case "w": // Перейти в кошелек
                    if (authService.getCurrentUser() != null) {
                        Wallet wallet = authService.getCurrentUser().getWallet();
                        if (wallet != null) {
                            enterWallet(scanner, wallet);
                        } else {
                            System.out.println("You don't have a wallet. Use 'c' to create one.");
                        }
                    } else {
                        System.out.println("You must be logged in to enter your wallet.");
                    }
                    break;

                case "a": // Добавить категорию
                    if (authService.getCurrentUser() != null) {
                        System.out.print("Enter the name of the new category: ");
                        String categoryName = scanner.nextLine().trim();
                        if (authService.getCurrentUser().getCategoryManager().addCategory(categoryName)) {
                            System.out.println(Constants.ADD_CATEGORY_COMMAND + " successfully: " + categoryName);
                        } else {
                            System.out.println("Category already exists: " + categoryName);
                        }
                    } else {
                        System.out.println("You must be logged in to add categories.");
                    }
                    break;

                case "v": // Просмотр всех категорий
                    if (authService.getCurrentUser() != null) {
                        List<Category> categories = authService.getCurrentUser().getCategoryManager().getAllCategories();
                        if (categories.isEmpty()) {
                            System.out.println("No categories found. Use '" + Constants.ADD_CATEGORY_COMMAND + "' command to add them.");
                        } else {
                            System.out.println("All Categories:");
                            for (Category category : categories) {
                                System.out.println("- " + category);
                            }
                        }
                    } else {
                        System.out.println("You must be logged in to view categories.");
                    }
                    break;

                case "s": // Установить лимит для категории
                    if (authService.getCurrentUser() != null) {
                        System.out.print("Enter the name of the category: ");
                        String categoryName = scanner.nextLine().trim();
                        Category category = authService.getCurrentUser().getCategoryManager().getAllCategories().stream()
                                .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                                .findFirst()
                                .orElse(null);

                        if (category == null) {
                            System.out.println("Category not found. Use '" + Constants.ADD_CATEGORY_COMMAND + "' command to add it.");
                            break;
                        }

                        System.out.print("Enter the new limit for the category: ");
                        double limit = scanner.nextDouble();
                        scanner.nextLine();
                        category.setLimit(limit);
                        System.out.println("Limit set successfully for category " + categoryName + ": " + limit);
                    } else {
                        System.out.println("You must be logged in to set category limits.");
                    }
                    break;

                case "+": // Приход
                    processOperation(scanner, authService, OperationType.INCOME);
                    break;

                case "-": // Расход
                    processOperation(scanner, authService, OperationType.EXPENSE);
                    break;

                case "q": // Завершить работу и сохранить данные в файл
                    System.out.println("Exiting the application. Goodbye!");
                    DataStorage.saveData(users);
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void processOperation(Scanner scanner, AuthenticationService authService, OperationType type) {
        if (authService.getCurrentUser() != null) {
            Wallet wallet = authService.getCurrentUser().getWallet();
            if (wallet != null) {
                List<Category> categories = authService.getCurrentUser().getCategoryManager().getAllCategories();
                if (categories.isEmpty()) {
                    System.out.println("No categories found. Use '" + Constants.ADD_CATEGORY_COMMAND + "' command to add them.");
                    return;
                }

                System.out.println("Available categories:");
                for (Category category : categories) {
                    System.out.println("- " + category.getName());
                }

                Category category = null;
                while (category == null) {
                    // Выводим доступные категории
//                    System.out.println("Available categories:");
                    categories = authService.getCurrentUser().getCategoryManager().getAllCategories();
//                    for (Category c : categories) {
//                        System.out.println("- " + c.getName());
//                    }

                    // Просим пользователя выбрать категорию или добавить новую
                    System.out.print("Enter category or enter 'a' to add new: ");
                    String input = scanner.nextLine().trim();

                    if (input.equalsIgnoreCase("a")) {
                        System.out.println("Redirecting to Add Category...");
                        System.out.print("Enter the name of the new category: ");
                        String newCategory = scanner.nextLine().trim();

                        if (authService.getCurrentUser().getCategoryManager().addCategory(newCategory)) {
                            System.out.println("Category added successfully: " + newCategory);
                        } else {
                            System.out.println("Category already exists: " + newCategory);
                        }
                        continue; // После добавления возвращаемся к выбору категории
                    }

                    // Проверяем существование категории
                    category = categories.stream()
                            .filter(c -> c.getName().equalsIgnoreCase(input))
                            .findFirst()
                            .orElse(null);

                    if (category == null) {
                        System.out.println("Category not found. Please try again.");
                    }
                }

                System.out.println("Category selected: " + category.getName());


                System.out.print(type == OperationType.INCOME ? "Enter income amount: " : "Enter expense amount: ");
                double amount = scanner.nextDouble();
                scanner.nextLine();

                if (type == OperationType.EXPENSE && category.getLimit() > 0 && amount > category.getLimit()) {
                    System.out.println("Warning: This expense exceeds the limit for the category " + category.getName() + ".");
                }

                wallet.addOperation(new Operation(type, amount, category.getName()));
                System.out.println((type == OperationType.INCOME ? "Income" : "Expense") + " added successfully.");
            } else {
                System.out.println("You must create a wallet first.");
            }
        } else {
            System.out.println("You must be logged in to perform this operation.");
        }
    }

    private static void enterWallet(Scanner scanner, Wallet wallet) {
        System.out.println("\nWelcome to your Wallet!");
        System.out.println("Balance: " + wallet.getBalance());
        System.out.println("Recent Operations:");
        List<Operation> operations = wallet.getOperations();
        if (operations.isEmpty()) {
            System.out.println("No operations found.");
        } else {
            Collections.reverse(operations);
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            for (Operation operation : operations) {
                String prefix = operation.getType() == OperationType.INCOME ? "+" : "-";
                System.out.println(prefix + " " + operation.getAmount() + " " + operation.getCategory() + " (" + dateFormat.format(operation.getDate()) + ")");
            }
            Collections.reverse(operations);
        }
        System.out.println("Returning to main menu...");
    }
}
