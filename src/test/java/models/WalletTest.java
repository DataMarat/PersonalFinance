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

        wallet.addOperation(new Operation(OperationType.INCOME, 5000, "Salary"));
        assertEquals(5000, wallet.getBalance());

        wallet.addOperation(new Operation(OperationType.EXPENSE, 2000, "Food"));
        assertEquals(3000, wallet.getBalance());
    }

    @Test
    void testAddCategory() {
        Wallet wallet = new Wallet();
        wallet.addCategory(new Category("Food", 10000));
        wallet.addCategory(new Category("Transport", 5000));

        List<Category> categories = wallet.getCategories();
        assertEquals(2, categories.size());
        assertEquals("Food", categories.get(0).getName());
        assertEquals(10000, categories.get(0).getLimit());
        assertEquals("Transport", categories.get(1).getName());
        assertEquals(5000, categories.get(1).getLimit());
    }

    @Test
    void testRecalculateBalance() {
        Wallet wallet = new Wallet();

        wallet.addOperation(new Operation(OperationType.INCOME, 5000, "Salary"));
        wallet.addOperation(new Operation(OperationType.EXPENSE, 2000, "Food"));

        wallet.setBalance(0); // Сбрасываем баланс для проверки пересчёта
        wallet.recalculateBalance();

        assertEquals(3000, wallet.getBalance());
    }

    @Test
    void testFilterOperations() {
        Wallet wallet = new Wallet();

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
        Wallet wallet = new Wallet();

        wallet.addOperation(new Operation(OperationType.INCOME, 5000, "Salary"));
        wallet.addOperation(new Operation(OperationType.EXPENSE, 2000, "Food"));

        double[] totals = wallet.calculateTotals(null, null);
        assertEquals(5000, totals[0]); // Доходы
        assertEquals(2000, totals[1]); // Расходы
    }

    @Test
    void testCalculateCategoryExpenses() {
        Wallet wallet = new Wallet();

        wallet.addOperation(new Operation(OperationType.EXPENSE, 2000, "Food"));
        wallet.addOperation(new Operation(OperationType.EXPENSE, 3000, "Transport"));

        Map<String, Double> expenses = wallet.calculateCategoryExpenses(null, null);
        assertEquals(2000, expenses.get("Food"));
        assertEquals(3000, expenses.get("Transport"));
    }

    @Test
    void testTransferAndReceiveAmount() {
        Wallet senderWallet = new Wallet();
        Wallet recipientWallet = new Wallet();

        senderWallet.setBalance(5000);
        boolean transferSuccess = senderWallet.transferAmount(2000);
        assertTrue(transferSuccess);
        assertEquals(3000, senderWallet.getBalance());

        recipientWallet.receiveAmount(2000, "Transfer");
        assertEquals(2000, recipientWallet.getBalance());

        assertEquals(1, recipientWallet.getOperations().size());
        assertEquals("Transfer", recipientWallet.getOperations().get(0).getCategory());
    }
}
