package com.example.se07203_b5;

public class RecurringExpense {
    private int id;
    private String name;
    private double amount;
    private String type; // "monthly" hoặc "yearly"
    private int dayOfMonth; // Ngày 1-31

    public RecurringExpense(int id, String name, double amount, String type, int dayOfMonth) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.type = type;
        this.dayOfMonth = dayOfMonth;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public int getDayOfMonth() { return dayOfMonth; }

    @Override
    public String toString() {
        return String.format("Tên: %s - %,.0f VNĐ (%s)",
                name, amount, type.equals("monthly") ? "Hàng tháng" : "Hàng năm");
    }
}
