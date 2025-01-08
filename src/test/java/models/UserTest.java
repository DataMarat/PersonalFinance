package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserInitialization() {
        User user = new User("ivan", "password123");
        assertNotNull(user.getUuid());
        assertEquals("ivan", user.getLogin());
        assertEquals("password123", user.getPassword());
        assertNull(user.getWallet());
    }

    @Test
    void testCreateWallet() {
        User user = new User("ivan", "password123");
        assertNull(user.getWallet());
        user.createWallet();
        assertNotNull(user.getWallet());
        assertEquals(5, user.getWallet().getCategories().size()); // Проверяем базовые категории
    }

    @Test
    void testRecalculateBalance() {
        User user = new User("ivan", "password123");
        user.createWallet();

        Wallet wallet = user.getWallet();
        wallet.addOperation(new Operation(OperationType.INCOME, 5000, "Salary"));
        wallet.addOperation(new Operation(OperationType.EXPENSE, 2000, "Food"));

        user.recalculateBalance();
        assertEquals(3000, wallet.getBalance());
    }

    @Test
    void testHasWallet() {
        User user = new User("ivan", "password123");
        assertFalse(user.hasWallet());
        user.createWallet();
        assertTrue(user.hasWallet());
    }
}
