package com.example.se07203_b5;

import androidx.annotation.NonNull;
import java.io.Serializable;
import java.util.Locale;

public class Item implements Serializable {

    // --- Khai báo biến ---
    private int id;
    private String name;
    private int quantity;
    private double unitPrice;
    private String date; // ===> BIẾN QUAN TRỌNG BỊ THIẾU <===

    // ===> SỬA LỖI: TẠO HÀM KHỞI TẠO VỚI ĐỦ 5 THAM SỐ <===
    public Item(int id, String name, int quantity, double unitPrice, String date) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.date = date; // Gán giá trị cho biến date
    }

    // --- Giữ lại hàm khởi tạo 4 tham số để tương thích (nếu cần) ---
    public Item(int id, String name, int quantity, double unitPrice) {
        this(id, name, quantity, unitPrice, ""); // Gọi hàm khởi tạo chính với date rỗng
    }


    // --- Các hàm Getter và Setter ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // --- Hàm toString để hiển thị trên ListView ---
    @NonNull
    @Override
    public String toString() {
        // Định dạng hiển thị đẹp mắt, có thể bao gồm cả ngày nếu muốn
        return String.format(Locale.US, "%s (SL: %d) - %,.0f VNĐ", this.name, this.quantity, this.unitPrice * this.quantity);
    }
}