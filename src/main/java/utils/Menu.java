package utils;

import static utils.Constants.*;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private final List<String> menuItems;

    // Конструктор для построения меню в зависимости от состояния пользователя и наличия кошелька
    public Menu(boolean isLoggedIn, boolean hasWallet) {
        this.menuItems = buildMenu(isLoggedIn, hasWallet);
    }

    // Метод для формирования меню
    private List<String> buildMenu(boolean isLoggedIn, boolean hasWallet) {
        List<String> menu = new ArrayList<>();
        menu.add("r - " + REGISTER_USER_COMMAND);
        if (isLoggedIn) {
            menu.add("m - " + SHOW_FULL_MENU_COMMAND);
            menu.add("l - " + LOGIN_COMMAND);
            menu.add("o - " + LOGOUT_COMMAND);
            if (hasWallet) {
                menu.add("w - " + ENTER_WALLET_COMMAND);
                menu.add("+ - " + ADD_INCOME_COMMAND);
                menu.add("- - " + ADD_EXPENSE_COMMAND);
                menu.add("f - " + FILTER_OPERATIONS_COMMAND);
                menu.add("s - " + SHOW_STATISTICS_COMMAND);
            } else {
                menu.add("c - " + CREATE_WALLET_COMMAND);
            }
            menu.add("a - " + ADD_CATEGORY_COMMAND);
            menu.add("v - " + VIEW_CATEGORIES_COMMAND);
            menu.add("i - " + SET_CATEGORY_LIMIT_COMMAND);
            menu.add("t - " + TRANSFER_MONEY_COMMAND);
        } else {
            menu.add("l - " + LOGIN_COMMAND);
        }
        menu.add("q - " + QUIT_PROGRAM_COMMAND);
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

    // Отображение краткой подсказки
    public void displayHint() {
        System.out.println("m - Show full menu, q - Quit program");
    }
}
