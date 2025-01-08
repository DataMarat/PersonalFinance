package models;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class OperationTest {

    @Test
    void testOperationCreationWithCurrentDate() {
        Operation operation = new Operation(OperationType.INCOME, 1000, "Salary");
        assertEquals(OperationType.INCOME, operation.getType());
        assertEquals(1000, operation.getAmount());
        assertEquals("Salary", operation.getCategory());
        assertNotNull(operation.getDate());
    }

    @Test
    void testOperationCreationWithSpecificDate() {
        Date specificDate = new Date(1672531200000L); // 01 Jan 2023 00:00:00 GMT
        Operation operation = new Operation(OperationType.EXPENSE, 500, "Food", specificDate);

        assertEquals(OperationType.EXPENSE, operation.getType());
        assertEquals(500, operation.getAmount());
        assertEquals("Food", operation.getCategory());
        assertEquals(specificDate, operation.getDate());
    }

    @Test
    void testToString() {
        Operation operation = new Operation(OperationType.INCOME, 2000, "Bonus");
        String expected = operation.getDate() + " - INCOME: 2000.0 (Category: Bonus)";
        assertEquals(expected, operation.toString());
    }
}
