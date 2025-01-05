package app;

import auth.AuthenticationService;
import models.User;
import utils.Menu;

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
