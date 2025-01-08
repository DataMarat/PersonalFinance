package models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OperationTypeTest {

    @Test
    void testOperationTypeValues() {
        OperationType[] values = OperationType.values();
        assertEquals(2, values.length);
        assertEquals(OperationType.INCOME, values[0]);
        assertEquals(OperationType.EXPENSE, values[1]);
    }

    @Test
    void testOperationTypeValueOf() {
        assertEquals(OperationType.INCOME, OperationType.valueOf("INCOME"));
        assertEquals(OperationType.EXPENSE, OperationType.valueOf("EXPENSE"));
    }
}
