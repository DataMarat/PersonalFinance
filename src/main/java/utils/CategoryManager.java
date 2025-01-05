package utils;

import models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryManager {
    private final List<Category> categories;

    public CategoryManager() {
        this.categories = new ArrayList<>();
        initializeDefaultCategories();
    }

    private void initializeDefaultCategories() {
        categories.add(new Category("Food"));
        categories.add(new Category("Transport"));
        categories.add(new Category("Entertainment"));
    }

    public boolean addCategory(String name) {
        for (Category category : categories) {
            if (category.getName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        categories.add(new Category(name));
        return true;
    }

    public List<Category> getAllCategories() {
        return new ArrayList<>(categories);
    }
}