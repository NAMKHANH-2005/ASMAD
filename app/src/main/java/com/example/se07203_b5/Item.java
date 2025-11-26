package com.example.se07203_b5;

import androidx.annotation.NonNull;

public class Item {
    private long id; // kiểu dữ liệu int, accessible private - không thể truy cập từ bên ngoài
    private String ItemName; // kiểu dữ liệu String, accessible private - không thể truy cập từ bên ngoài
    private int Quantity; // kiểu dữ liệu int, accessible private - không thể truy cập từ bên ngoài
    private int unitPrice = 0; // kiểu dữ liệu int, accessible private - không thể truy cập từ bên ngoài

    // Hàm khởi tạo, tự động được gọi ra khi object thuộc class được khởi tạo
    public Item(String itemName, int quantity) {
        // truyền vào 2 tham số itemName và quantity vào hàm khởi tạo
        // gán giá trị vào 2 biến ItemName và Quantity
        ItemName = itemName;
        Quantity = quantity;
    }
    // Hàm khởi tạo, tự động được gọi ra khi object thuộc class được khởi tạo
    public Item(String _itemName, int _quantity, int _unitPrice){
        ItemName = _itemName;
        Quantity = _quantity;
        unitPrice = _unitPrice;
    }

    public Item (long id, String name, int quantity, int unitPrice){
        this.id = id;
        this.ItemName = name;
        this.Quantity = quantity;
        this.unitPrice = unitPrice;
    }
    // Hàm toString trả về chuỗi thông tin của sản phẩm
    @NonNull
    @Override
    public String toString(){
        return ItemName + " - Số lượng: " + Quantity + " - Đơn giá: " + unitPrice;
    }
    // Hàm get giá trị tên sản phẩm, với accessible public - có thể truy cập từ bên ngoài
    public String getName(){
        return ItemName;
    }
    // Hàm get giá trị số lượng sản phẩm, với accessible public - có thể truy cập từ bên ngoài
    public int getQuantity(){
        return Quantity;
    }

    public int getUnitPrice(){
        return unitPrice;
    }
    public void setUnitPrice(int _price){
        unitPrice = _price;
    }

    public void setName(String name){
        ItemName = name;
    }

    public void setQuantity(int quantity){
        Quantity = quantity;
    }

    public long getId(){
        return id;
    }

}
