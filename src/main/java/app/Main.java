package app;

import auth.AuthenticationService;
import models.User;
import models.Category;
import utils.Menu;
import utils.CategoryManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        AuthenticationService authService = new AuthenticationService();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            Menu menu = new Menu(authService.getCurrentUser() != null);
            menu.displayMenu();

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "r": // Регистрация
                    System.out.print("Enter login: ");
                    String login = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    if (authService.register(login, password)) {
                        authService.login(login, password);
                        User registeredUser = authService.getCurrentUser();
                        System.out.println("Registration successful! You are now logged in as: " + login + ", your ID: " + registeredUser.getUuid());
                    } else {
                        System.out.println("Registration failed. Login already exists.");
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

                case "a": // Добавить категорию
                    if (authService.getCurrentUser() != null) {
                        System.out.print("Enter the name of the new category: ");
                        String categoryName = scanner.nextLine().trim();
                        if (authService.getCurrentUser().getCategoryManager().addCategory(categoryName)) {
                            System.out.println("Category added successfully: " + categoryName);
                        } else {
                            System.out.println("Category already exists: " + categoryName);
                        }
                    } else {
                        System.out.println("You must be logged in to add categories.");
                    }
                    break;

                case "v": // Просмотр всех категорий
                    if (authService.getCurrentUser() != null) {
                        System.out.println("All Categories:");
                        for (Category category : authService.getCurrentUser().getCategoryManager().getAllCategories()) {
                            System.out.println("- " + category);
                        }
                    } else {
                        System.out.println("You must be logged in to view categories.");
                    }
                    break;

                case "s": // Установить лимит для категории
                    if (authService.getCurrentUser() != null) {
                        System.out.print("Enter the name of the category: ");
                        String categoryName = scanner.nextLine().trim();
                        CategoryManager manager = authService.getCurrentUser().getCategoryManager();
                        Category category = manager.getAllCategories().stream()
                                .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                                .findFirst()
                                .orElse(null);

                        if (category != null) {
                            System.out.print("Enter the new limit for the category: ");
                            double limit = scanner.nextDouble();
                            scanner.nextLine();
                            category.setLimit(limit);
                            System.out.println("Limit set successfully for category " + categoryName + ": " + limit);
                        } else {
                            System.out.println("Category not found: " + categoryName);
                        }
                    } else {
                        System.out.println("You must be logged in to set category limits.");
                    }
                    break;

                case "q": // Завершить работу
                    System.out.println("Exiting the application. Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
