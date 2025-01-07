package models;

import java.util.Date;

public class Operation {
    private final OperationType type;
    private final double amount;
    private final String category;
    private final Date date;

    public Operation(OperationType type, double amount, String category) {
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.date = new Date();
    }

    public Operation(OperationType type, double amount, String category, Date date) {
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public OperationType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return date + " - " + type + ": " + amount + " (Category: " + category + ")";
    }
}

