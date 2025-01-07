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
        assertEquals(0.0, user.getWallet().getBalance());
        assertTrue(user.getWallet().getOperations().isEmpty());
        assertTrue(user.getWallet().getCategories().isEmpty());
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

    @Test
    void testAddCategoryToWallet() {
        User user = new User("ivan", "password123");
        user.createWallet();

        Wallet wallet = user.getWallet();
        wallet.addCategory(new Category("Food", 10000));
        wallet.addCategory(new Category("Transport", 5000));

        assertEquals(2, wallet.getCategories().size());
        assertEquals("Food", wallet.getCategories().get(0).getName());
        assertEquals(10000, wallet.getCategories().get(0).getLimit());
        assertEquals("Transport", wallet.getCategories().get(1).getName());
        assertEquals(5000, wallet.getCategories().get(1).getLimit());
    }
}
