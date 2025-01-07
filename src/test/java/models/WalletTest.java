package models;

import org.junit.jupiter.api.Test;
import utils.CategoryManager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    @Test
    void testWalletInitialization() {
        CategoryManager categoryManager = new CategoryManager();
        Wallet wallet = new Wallet(categoryManager);

        assertNotNull(wallet.getOperations());
        assertEquals(0.0, wallet.getBalance());
    }

    @Test
    void testAddOperationAndBalanceUpdate() {
        CategoryManager categoryManager = new CategoryManager();
        Wallet wallet = new Wallet(categoryManager);

        wallet.addOperation(new Operation(OperationType.INCOME, 5000, "Salary"));
        assertEquals(5000, wallet.getBalance());

        wallet.addOperation(new Operation(OperationType.EXPENSE, 2000, "Food"));
        assertEquals(3000, wallet.getBalance());
    }

    @Test
    void testRecalculateBalance() {
        CategoryManager categoryManager = new CategoryManager();
        Wallet wallet = new Wallet(categoryManager);

        wallet.addOperation(new Operation(OperationType.INCOME, 5000, "Salary"));
        wallet.addOperation(new Operation(OperationType.EXPENSE, 2000, "Food"));

        wallet.setBalance(0); // Сбрасываем баланс для проверки пересчёта
        wallet.recalculateBalance();

        assertEquals(3000, wallet.getBalance());
    }

    @Test
    void testFilterOperations() {
        CategoryManager categoryManager = new CategoryManager();
        Wallet wallet = new Wallet(categoryManager);

        Date date1 = new Date(System.currentTimeMillis() - 1000000); // 10 минут назад
        Date date2 = new Date(); // Сейчас

        wallet.addOperation(new Operation(OperationType.INCOME, 5000, "Salary", date1));
        wallet.addOperation(new Operation(OperationType.EXPENSE, 2000, "Food", date2));

        List<Operation> filtered = wallet.filterOperations("Food", null, null);
        assertEquals(1, filtered.size());
        assertEquals("Food", filtered.get(0).getCategory());
    }

    @Test
    void testCalculateTotals() {
        CategoryManager categoryManager = new CategoryManager();
        Wallet wallet = new Wallet(categoryManager);

        wallet.addOperation(new Operation(OperationType.INCOME, 5000, "Salary"));
        wallet.addOperation(new Operation(OperationType.EXPENSE, 2000, "Food"));

        double[] totals = wallet.calculateTotals(null, null);
        assertEquals(5000, totals[0]); // Доходы
        assertEquals(2000, totals[1]); // Расходы
    }

    @Test
    void testCalculateCategoryExpenses() {
        CategoryManager categoryManager = new CategoryManager();
        Wallet wallet = new Wallet(categoryManager);

        wallet.addOperation(new Operation(OperationType.EXPENSE, 2000, "Food"));
        wallet.addOperation(new Operation(OperationType.EXPENSE, 3000, "Transport"));

        Map<String, Double> expenses = wallet.calculateCategoryExpenses(null, null);
        assertEquals(2000, expenses.get("Food"));
        assertEquals(3000, expenses.get("Transport"));
    }
}
