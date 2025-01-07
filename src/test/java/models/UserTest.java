package models;

import org.junit.jupiter.api.Test;
import utils.CategoryManager;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserInitialization() {
        User user = new User("testUser", "password123");

        assertNotNull(user.getUuid());
        assertEquals("testUser", user.getLogin());
        assertEquals("password123", user.getPassword());
        assertNotNull(user.getCategoryManager());
        assertNull(user.getWallet());
    }

    @Test
    void testCreateWallet() {
        User user = new User("testUser", "password123");
        user.createWallet();

        assertNotNull(user.getWallet());
        assertNotNull(user.getWallet().getCategoryManager());
    }

    @Test
    void testRecalculateBalance() {
        User user = new User("testUser", "password123");
        user.createWallet();
        Wallet wallet = user.getWallet();

        wallet.addOperation(new Operation(OperationType.INCOME, 5000, "Salary"));
        wallet.addOperation(new Operation(OperationType.EXPENSE, 2000, "Food"));

        user.recalculateBalance();

        assertEquals(3000, wallet.getBalance());
    }
}
