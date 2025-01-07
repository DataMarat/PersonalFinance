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
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import utils.CategoryManager;


public class Main {
    public static void main(String[] args) {
        // Загрузка данных из файла либо старт с нуля
        List<User> users = DataStorage.loadData();
        if (users != null) {
            users.forEach(user -> {
                // Убедимся, что CategoryManager восстановлен
                if (user.getCategoryManager() == null) {
                    user.setCategoryManager(new CategoryManager());
                }
                // Передаём CategoryManager в Wallet, если он есть
                if (user.getWallet() != null) {
                    user.getWallet().setCategoryManager(user.getCategoryManager());
                    user.recalculateBalance();
                }
            });
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

            if (authService.getCurrentUser() != null) {
                menu.displayHint();
            } else {
                menu.displayMenu();
            }

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
                            // Отладочная проверка CategoryManager перед созданием кошелька
                            if (currentUser.getCategoryManager() == null) {
                                System.out.println("Error: CategoryManager is not initialized for the current user.");
                                break;
                            }

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

                case "v": // Просмотр всех категорий (лимиты, остатки по лимитам)
                    if (authService.getCurrentUser() != null) {
                        List<Category> categories = authService.getCurrentUser().getCategoryManager().getAllCategories();
                        if (categories.isEmpty()) {
                            System.out.println("No categories found. Use '" + Constants.ADD_CATEGORY_COMMAND + "' command to add them.");
                        } else {
                            System.out.println("All Categories:");
                            for (Category category : categories) {
                                double limit = category.getLimit();
                                double used = authService.getCurrentUser().getWallet().getOperations().stream()
                                        .filter(op -> op.getType() == OperationType.EXPENSE && op.getCategory().equalsIgnoreCase(category.getName()))
                                        .mapToDouble(Operation::getAmount)
                                        .sum();
                                double remaining = limit > 0 ? (limit - used) : Double.POSITIVE_INFINITY;

                                String limitInfo = limit > 0 ? "Limit: " + limit + ", Remaining: " + (remaining == Double.POSITIVE_INFINITY ? "∞" : remaining) : "No Limit";
                                System.out.println("- " + category.getName() + " (" + limitInfo + ")");
                            }
                        }
                    } else {
                        System.out.println("You must be logged in to view categories.");
                    }
                    break;

                case "i": // Установить лимит для категории
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
                case "f": // Фильтрация операций
                    if (authService.getCurrentUser() != null && authService.getCurrentUser().getWallet() != null) {
                        Wallet wallet = authService.getCurrentUser().getWallet();

                        System.out.print("Enter category (or leave blank to skip): ");
                        String category = scanner.nextLine().trim();
                        if (category.isEmpty()) category = null;

                        System.out.print("Enter start date (dd.MM.yyyy) or leave blank: ");
                        String fromDateInput = scanner.nextLine().trim();
                        Date fromDate = null;
                        if (!fromDateInput.isEmpty()) {
                            try {
                                fromDate = new SimpleDateFormat("dd.MM.yyyy").parse(fromDateInput);
                            } catch (Exception e) {
                                System.out.println("Invalid date format. Skipping start date.");
                            }
                        }

                        System.out.print("Enter end date (dd.MM.yyyy) or leave blank: ");
                        String toDateInput = scanner.nextLine().trim();
                        Date toDate = null;
                        if (!toDateInput.isEmpty()) {
                            try {
                                toDate = new SimpleDateFormat("dd.MM.yyyy").parse(toDateInput);
                            } catch (Exception e) {
                                System.out.println("Invalid date format. Skipping end date.");
                            }
                        }

                        List<Operation> filteredOperations = wallet.filterOperations(category, fromDate, toDate);
                        if (filteredOperations.isEmpty()) {
                            System.out.println("No operations found for the given filters.");
                        } else {
                            System.out.println("Filtered operations:");
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                            for (Operation op : filteredOperations) {
                                String prefix = op.getType() == OperationType.INCOME ? "+" : "-";
                                System.out.println(prefix + " " + op.getAmount() + " " + op.getCategory() +
                                        " (" + dateFormat.format(op.getDate()) + ")");
                            }
                        }
                    } else {
                        System.out.println("You must be logged in and have a wallet to filter operations.");
                    }
                    break;
                case "s": // Показать статистику
                    if (authService.getCurrentUser() != null && authService.getCurrentUser().getWallet() != null) {
                        Wallet wallet = authService.getCurrentUser().getWallet();

                        System.out.print("Enter start date (dd.MM.yyyy) or leave blank: ");
                        String fromDateInput = scanner.nextLine().trim();
                        Date fromDate = null;
                        if (!fromDateInput.isEmpty()) {
                            try {
                                fromDate = new SimpleDateFormat("dd.MM.yyyy").parse(fromDateInput);
                            } catch (Exception e) {
                                System.out.println("Invalid date format. Skipping start date.");
                            }
                        }

                        System.out.print("Enter end date (dd.MM.yyyy) or leave blank: ");
                        String toDateInput = scanner.nextLine().trim();
                        Date toDate = null;
                        if (!toDateInput.isEmpty()) {
                            try {
                                toDate = new SimpleDateFormat("dd.MM.yyyy").parse(toDateInput);
                            } catch (Exception e) {
                                System.out.println("Invalid date format. Skipping end date.");
                            }
                        }

                        // Подсчёт общих доходов и расходов
                        double[] totals = wallet.calculateTotals(fromDate, toDate);
                        System.out.println("Total Income: " + totals[0]);
                        System.out.println("Total Expense: " + totals[1]);

                        // Распределение расходов по категориям
                        Map<String, Double> categoryExpenses = wallet.calculateCategoryExpenses(fromDate, toDate);
                        if (((Map<?, ?>) categoryExpenses).isEmpty()) {
                            System.out.println("No expenses found for the given filters.");
                        } else {
                            System.out.println("Expenses by category:");
                            for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
                                System.out.println("- " + entry.getKey() + ": " + entry.getValue());
                            }
                        }
                    } else {
                        System.out.println("You must be logged in and have a wallet to view statistics.");
                    }
                    break;

                case "+": // Операция прихода
                    processOperation(scanner, authService, OperationType.INCOME);
                    break;

                case "-": // Добавление расхода
                    if (authService.getCurrentUser() != null && authService.getCurrentUser().getWallet() != null) {
                        Wallet wallet = authService.getCurrentUser().getWallet();

                        System.out.print("Enter category: ");
                        String category = scanner.nextLine().trim();

                        // Проверяем, существует ли категория
                        boolean categoryExists = authService.getCurrentUser().getCategoryManager().getAllCategories().stream()
                                .anyMatch(c -> c.getName().equalsIgnoreCase(category));

                        if (!categoryExists) {
                            System.out.println("Category not found. Use 'a' to add a new category.");
                            break;
                        }

                        System.out.print("Enter expense amount: ");
                        double amount;
                        try {
                            amount = Double.parseDouble(scanner.nextLine().trim());
                            if (amount <= 0) {
                                System.out.println("Amount must be positive.");
                                break;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid amount. Please enter a numeric value.");
                            break;
                        }

                        // Создаём операцию расхода
                        Operation newOperation = new Operation(OperationType.EXPENSE, amount, category);

                        // Добавляем операцию и проверяем баланс
                        wallet.addOperation(newOperation);
                        wallet.checkBudgetBalance(); // Проверка превышения расходов

                        // Сохраняем изменения
                        DataStorage.saveData(users);

                        System.out.println("Expense added successfully.");
                    } else {
                        System.out.println("You must be logged in and have a wallet to add expenses.");
                    }
                    break;


                case "m": // Показать полное меню
                    if (authService.getCurrentUser() != null) {
                        menu.displayMenu();
                    } else {
                        System.out.println("You must be logged in to view full menu.");
                    }
                    break;

                case "t": // Перевод средств между кошельками
                    if (authService.getCurrentUser() != null) {
                        User sender = authService.getCurrentUser();
                        if (!sender.hasWallet()) {
                            System.out.println("You must have a wallet to perform a transfer.");
                            break;
                        }

                        System.out.print("Enter recipient's UUID: ");
                        String recipientUUID = scanner.nextLine().trim();

                        // Поиск получателя
                        User recipient = users.stream()
                                .filter(u -> u.getUuid().equalsIgnoreCase(recipientUUID))
                                .findFirst()
                                .orElse(null);

                        if (recipient == null) {
                            System.out.println("Recipient not found.");
                            break;
                        }

                        if (!recipient.hasWallet()) {
                            System.out.println("Recipient does not have a wallet.");
                            break;
                        }

                        System.out.print("Enter amount to transfer: ");
                        double amount;
                        try {
                            amount = Double.parseDouble(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid amount. Please try again.");
                            break;
                        }

                        if (amount <= 0) {
                            System.out.println("Amount must be positive.");
                            break;
                        }

                        // Выполнение перевода
                        if (sender.getWallet().transferAmount(amount)) {
                            recipient.getWallet().receiveAmount(amount, "Transfer from " + sender.getLogin());
                            sender.getWallet().addOperation(new Operation(OperationType.EXPENSE, amount, "Transfer to " + recipient.getLogin()));
                            System.out.println("Transfer successful!");
                            DataStorage.saveData(users); // Сохранение данных
                        } else {
                            System.out.println("Insufficient funds for the transfer.");
                        }
                    } else {
                        System.out.println("You must be logged in to perform a transfer.");
                    }
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
                    categories = authService.getCurrentUser().getCategoryManager().getAllCategories();

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


//                System.out.print(type == OperationType.INCOME ? "Enter income amount: " : "Enter expense amount: ");
                System.out.print("Enter amount: ");
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
