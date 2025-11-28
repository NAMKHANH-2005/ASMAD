package com.example.se07203_b5;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SE07203Expense";
    private static final int DATABASE_VERSION = 2;

    //    Table User
    private static final String TABLE_USER = "users";
    private static final String TABLE_USER_COLUMN_ID = "id";
    private static final String TABLE_USER_COLUMN_USERNAME = "username";
    private static final String TABLE_USER_COLUMN_PASSWORD = "password";
    private static final String TABLE_USER_COLUMN_EMAIL = "email";


    // Table Product
    private static final String TABLE_PRODUCT = "products";
    private static final String TABLE_PRODUCT_COLUMN_ID = "id";
    private static final String TABLE_PRODUCT_COLUMN_NAME = "name";
    private static final String TABLE_PRODUCT_COLUMN_PRICE = "price";
    private static final String TABLE_PRODUCT_COLUMN_QUANTITY = "quantity";
    private static final String TABLE_PRODUCT_COLUMN_USER_ID = "user_id";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USER + "("
                + TABLE_USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TABLE_USER_COLUMN_USERNAME + " TEXT, "
                + TABLE_USER_COLUMN_EMAIL + " TEXT, "
                + TABLE_USER_COLUMN_PASSWORD + " TEXT);";
        db.execSQL(CREATE_TABLE_USERS);

        String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCT + "("
                + TABLE_PRODUCT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TABLE_PRODUCT_COLUMN_NAME + " TEXT, "
                + TABLE_PRODUCT_COLUMN_PRICE + " INTEGER, "
                + TABLE_PRODUCT_COLUMN_QUANTITY + " INTEGER, "
                + TABLE_PRODUCT_COLUMN_USER_ID + " INTEGER);";
        db.execSQL(CREATE_TABLE_PRODUCTS);
    }

    public long addProduct(Item product, long UserId) {
        SQLiteDatabase db = this.getWritableDatabase(); // Khai báo kết nối database với quyền ghi
        ContentValues values = new ContentValues(); // Khai báo giá trị cần lưu vào database
        values.put(TABLE_PRODUCT_COLUMN_NAME, product.getName());
        values.put(TABLE_PRODUCT_COLUMN_QUANTITY, product.getQuantity());
        values.put(TABLE_PRODUCT_COLUMN_PRICE, product.getUnitPrice());
        values.put(TABLE_PRODUCT_COLUMN_USER_ID, UserId);
        long id = db.insert(TABLE_PRODUCT, null, values); // Thêm dữ liệu vào bảng (return id)
        db.close();
        return id;
    }

    public ArrayList<Item> getProducts(long UserId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCT, // Truy vấn dữ liệu trong bảng TABLE_USER
                // Các cột cần truy vấn (cột nào cần lấy ra) (thứ tự index 0-1-2-3)
                new String[]{TABLE_PRODUCT_COLUMN_ID, TABLE_PRODUCT_COLUMN_NAME, TABLE_PRODUCT_COLUMN_QUANTITY, TABLE_PRODUCT_COLUMN_PRICE},
                // Điều kiện lấy (WHERE)
                TABLE_PRODUCT_COLUMN_USER_ID + "=?",
                // Giá trị của điều kiện lấy (được fill lần lượt vào dấu ?)
                new String[]{String.valueOf(UserId)},
                // Nhóm theo (groupBy), Having, OrderBy
                null, null, null);
        ArrayList<Item> items = new ArrayList<Item>();
        if(cursor.moveToFirst()) {
            do {
                Item item = new Item(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3)
                );
                items.add(item); // Thêm vào danh sách
            } while (cursor.moveToNext());
        }
        return items;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        onCreate(db);
    }

    public long addUser(User user){
        SQLiteDatabase db = this.getWritableDatabase(); // Khai báo kết nối database với quyền ghi
        ContentValues values = new ContentValues(); // Khai báo giá trị cần lưu vào database
        values.put(TABLE_USER_COLUMN_USERNAME, user.getUsername());
        values.put(TABLE_USER_COLUMN_PASSWORD, user.getPassword());
        values.put(TABLE_USER_COLUMN_EMAIL, user.getEmail());
        long id = db.insert(TABLE_USER, null, values); // Thêm dữ liệu vào bảng (return id)
        db.close();
        return id;
    }

    public User getUserByUsernameAndPassword(String username, String password)
    {
        SQLiteDatabase db = this.getReadableDatabase(); // Khai báo kết nối database với quyền đọc
        Cursor cursor = db.query(TABLE_USER, // Truy vấn dữ liệu trong bảng TABLE_USER
                // Các cột cần truy vấn (cột nào cần lấy ra) (thứ tự index 0-1-2-3)
                new String[]{TABLE_USER_COLUMN_ID, TABLE_USER_COLUMN_USERNAME, TABLE_USER_COLUMN_EMAIL, TABLE_USER_COLUMN_PASSWORD},
                // Điều kiện lấy (WHERE)
                TABLE_USER_COLUMN_USERNAME + "=? AND " + TABLE_USER_COLUMN_PASSWORD +"=?",
                // Giá trị của điều kiện lấy (được fill lần lượt vào dấu ?)
                new String[]{username, password},
                // Nhóm theo (groupBy), Having, OrderBy
                null, null, null);
        User user = null;
        if(cursor.moveToFirst()){
            user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            );
        }
        cursor.close();
        db.close();
        return user;
    }
    public boolean removeProductById(long id) {
        SQLiteDatabase db = this.getWritableDatabase(); // Khai báo kết nối database với quyền ghi
        int result = db.delete(TABLE_PRODUCT,
                TABLE_PRODUCT_COLUMN_ID + "= ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return result > 0;
    }
}
