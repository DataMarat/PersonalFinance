package models;

public class Category {
    private final String name;
    private double limit; // Лимит для категории

    public Category(String name) {
        this.name = name;
        this.limit = 0.0; // Лимит по умолчанию
    }

    public String getName() {
        return name;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return name +  (limit > 0 ? " (Limit: " + limit + ")": " (No Limit)");
    }
}