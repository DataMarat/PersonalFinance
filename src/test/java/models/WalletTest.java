package models;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    @Test
    void testWalletInitialization() {
        Wallet wallet = new Wallet();
        assertNotNull(wallet.getOperations());
        assertNotNull(wallet.getCategories());
        assertEquals(0.0, wallet.getBalance());
    }

    @Test
    void testAddOperationAndBalanceUpdate() {
        Wallet wallet = new Wallet();

        wallet.addOperation(new Operation(OperationType.INCOME, 10000, "Salary"));
        assertEquals(10000, wallet.getBalance());

        wallet.addOperation(new Operation(OperationType.EXPENSE, 3000, "Food"));
        assertEquals(7000, wallet.getBalance());
    }

    @Test
    void testAddCategory() {
        Wallet wallet = new Wallet();
        assertEquals(0, wallet.getCategories().size());

        wallet.addCategory(new Category("Food"));
        wallet.addCategory(new Category("Transport"));

        assertEquals(2, wallet.getCategories().size());
    }

    @Test
    void testRecalculateBalance() {
        Wallet wallet = new Wallet();
        wallet.addOperation(new Operation(OperationType.INCOME, 2000, "Salary"));
        wallet.addOperation(new Operation(OperationType.EXPENSE, 500, "Food"));

        wallet.setBalance(0); // Сбрасываем баланс
        wallet.recalculateBalance();

        assertEquals(1500, wallet.getBalance());
    }

    @Test
    void testFilterOperations() {
        Wallet wallet = new Wallet();
        Date now = new Date();

        wallet.addOperation(new Operation(OperationType.INCOME, 5000, "Salary", now));
        wallet.addOperation(new Operation(OperationType.EXPENSE, 2000, "Food", now));

        List<Operation> filtered = wallet.filterOperations("Food", null, null);
        assertEquals(1, filtered.size());
        assertEquals("Food", filtered.get(0).getCategory());
    }

    @Test
    void testCalculateTotals() {
        Wallet wallet = new Wallet();

        wallet.addOperation(new Operation(OperationType.INCOME, 8000, "Salary"));
        wallet.addOperation(new Operation(OperationType.EXPENSE, 3000, "Food"));

        double[] totals = wallet.calculateTotals(null, null);
        assertEquals(8000, totals[0]); // Доходы
        assertEquals(3000, totals[1]); // Расходы
    }

    @Test
    void testCalculateCategoryExpenses() {
        Wallet wallet = new Wallet();

        wallet.addOperation(new Operation(OperationType.EXPENSE, 1000, "Food"));
        wallet.addOperation(new Operation(OperationType.EXPENSE, 2000, "Transport"));

        Map<String, Double> categoryExpenses = wallet.calculateCategoryExpenses(null, null);
        assertEquals(1000, categoryExpenses.get("Food"));
        assertEquals(2000, categoryExpenses.get("Transport"));
    }

    @Test
    void testTransferAmount() {
        Wallet wallet = new Wallet();
        wallet.setBalance(5000);

        assertTrue(wallet.transferAmount(3000));
        assertEquals(2000, wallet.getBalance());

        assertFalse(wallet.transferAmount(3000)); // Недостаточно средств
        assertEquals(2000, wallet.getBalance());
    }

    @Test
    void testReceiveAmount() {
        Wallet wallet = new Wallet();

        wallet.receiveAmount(2000, "Gift");
        assertEquals(2000, wallet.getBalance());

        List<Operation> operations = wallet.getOperations();
        assertEquals(1, operations.size());
        assertEquals("Gift", operations.get(0).getCategory());
        assertEquals(2000, operations.get(0).getAmount());
    }
}
