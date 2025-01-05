package utils;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private final List<String> menuItems;

    // Конструктор для построения меню в зависимости от состояния пользователя
    public Menu(boolean isLoggedIn) {
        this.menuItems = buildMenu(isLoggedIn);
    }

    // Метод для формирования меню
    private List<String> buildMenu(boolean isLoggedIn) {
        List<String> menu = new ArrayList<>();
        menu.add("r - Register User");
        if (isLoggedIn) {
            menu.add("l - Login different user");
            menu.add("o - Logout");
            menu.add("a - Add Category");
            menu.add("v - View Categories");
            menu.add("s - Set Category Limit");
        } else {
            menu.add("l - Login");
        }
        menu.add("q - Quit Program");
        return menu;
    }

    // Метод для отображения меню
    public void displayMenu() {
        System.out.println("\n=======================");
        System.out.println("       MAIN MENU");
        System.out.println("=======================");
        for (String item : menuItems) {
            System.out.println(item);
        }
        System.out.println("=======================");
    }
}