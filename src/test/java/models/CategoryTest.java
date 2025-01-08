package models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void testCategoryInitialization() {
        Category category = new Category("Food");
        assertEquals("Food", category.getName());
        assertEquals(0.0, category.getLimit());
    }

    @Test
    void testSetLimit() {
        Category category = new Category("Transport");
        category.setLimit(5000);
        assertEquals(5000, category.getLimit());
    }

    @Test
    void testToStringNoLimit() {
        Category category = new Category("Entertainment");
        String expected = "Entertainment (No Limit)";
        assertEquals(expected, category.toString());
    }

    @Test
    void testToStringWithLimit() {
        Category category = new Category("Rent");
        category.setLimit(10000);
        String expected = "Rent (Limit: 10000.0)";
        assertEquals(expected, category.toString());
    }
}
