package com.example.se07203_b5;import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4; // Giả sử phiên bản hiện tại là 4
    private static final String DATABASE_NAME = "SE07203Expense";

    // ... (Các hằng số bảng USER, PRODUCT, RECURRING giữ nguyên) ...
    private static final String TABLE_USER = "users";
    private static final String TABLE_USER_COLUMN_ID = "id";
    private static final String TABLE_USER_COLUMN_USERNAME = "username";
    private static final String TABLE_USER_COLUMN_PASSWORD = "password";
    private static final String TABLE_USER_COLUMN_FULLNAME = "fullname";
    private static final String TABLE_USER_COLUMN_SPENDING_LIMIT = "spending_limit";

    private static final String TABLE_PRODUCT = "products";
    private static final String TABLE_PRODUCT_COLUMN_ID = "id";
    private static final String TABLE_PRODUCT_COLUMN_NAME = "name";
    private static final String TABLE_PRODUCT_COLUMN_PRICE = "price";
    private static final String TABLE_PRODUCT_COLUMN_QUANTITY = "quantity";
    private static final String TABLE_PRODUCT_COLUMN_USER_ID = "user_id";

    private static final String TABLE_RECURRING = "recurring_expenses";
    private static final String TABLE_RECURRING_COLUMN_ID = "id";
    private static final String TABLE_RECURRING_COLUMN_NAME = "name";
    private static final String TABLE_RECURRING_COLUMN_AMOUNT = "amount";
    private static final String TABLE_RECURRING_COLUMN_TYPE = "type";
    private static final String TABLE_RECURRING_COLUMN_DAY_OF_MONTH = "day_of_month";
    private static final String TABLE_RECURRING_COLUMN_USER_ID = "user_id";

    // Câu lệnh tạo bảng
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USER + "("
            + TABLE_USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TABLE_USER_COLUMN_USERNAME + " TEXT, "
            + TABLE_USER_COLUMN_FULLNAME + " TEXT, "
            + TABLE_USER_COLUMN_SPENDING_LIMIT + " REAL DEFAULT 0, "
            + TABLE_USER_COLUMN_PASSWORD + " TEXT);";

    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCT + "("
            + TABLE_PRODUCT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TABLE_PRODUCT_COLUMN_NAME + " TEXT, "
            + TABLE_PRODUCT_COLUMN_PRICE + " INTEGER, "
            + TABLE_PRODUCT_COLUMN_QUANTITY + " INTEGER, "
            + TABLE_PRODUCT_COLUMN_USER_ID + " INTEGER);";

    private static final String CREATE_TABLE_RECURRING = "CREATE TABLE " + TABLE_RECURRING + "("
            + TABLE_RECURRING_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TABLE_RECURRING_COLUMN_NAME + " TEXT, "
            + TABLE_RECURRING_COLUMN_AMOUNT + " REAL, "
            + TABLE_RECURRING_COLUMN_TYPE + " TEXT, "
            + TABLE_RECURRING_COLUMN_DAY_OF_MONTH + " INTEGER, "
            + TABLE_RECURRING_COLUMN_USER_ID + " INTEGER);";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Chỉ tạo bảng khi database được tạo lần đầu tiên
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_PRODUCTS);
        db.execSQL(CREATE_TABLE_RECURRING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // ===> SỬA LẠI HOÀN TOÀN LOGIC NÂNG CẤP Ở ĐÂY <===
        // Logic này sẽ kiểm tra và nâng cấp từng phiên bản một mà không xóa dữ liệu

        // Nâng cấp từ v1 lên v2 (Ví dụ: Thêm bảng products)
        if (oldVersion < 2) {
            // Giả sử v2 thêm bảng products, nếu chưa có thì tạo
            db.execSQL(CREATE_TABLE_PRODUCTS);
        }

        // Nâng cấp từ v2 lên v3 (Ví dụ: Thêm cột spending_limit)
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + TABLE_USER_COLUMN_SPENDING_LIMIT + " REAL DEFAULT 0;");
        }

        // Nâng cấp từ v3 lên v4 (Thêm bảng recurring_expenses)
        if (oldVersion < 4) {
            db.execSQL(CREATE_TABLE_RECURRING);
        }
    }


    // ... (Tất cả các hàm cũ của bạn như addUser, getProducts, ... giữ nguyên ở đây)
    public long addUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TABLE_USER_COLUMN_USERNAME, user.getUsername());
        values.put(TABLE_USER_COLUMN_PASSWORD, user.getPassword());
        values.put(TABLE_USER_COLUMN_FULLNAME, user.getFullname());
        long id = db.insert(TABLE_USER, null, values);
        db.close();
        return id;
    }
    public User getUserByUsernameAndPassword(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER,
                new String[]{TABLE_USER_COLUMN_ID, TABLE_USER_COLUMN_USERNAME, TABLE_USER_COLUMN_FULLNAME, TABLE_USER_COLUMN_PASSWORD},
                TABLE_USER_COLUMN_USERNAME + "=? AND " + TABLE_USER_COLUMN_PASSWORD +"=?",
                new String[]{username, password},
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
    public void updateUserSpendingLimit(long userId, double limit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TABLE_USER_COLUMN_SPENDING_LIMIT, limit);
        db.update(TABLE_USER, values, TABLE_USER_COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
    }
    public double getUserSpendingLimit(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER,
                new String[]{TABLE_USER_COLUMN_SPENDING_LIMIT},
                TABLE_USER_COLUMN_ID + " = ?",
                new String[]{String.valueOf(userId)}, null, null, null);

        double limit = 0;
        if (cursor != null && cursor.moveToFirst()) {
            limit = cursor.getDouble(0);
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return limit;
    }
    public long addProduct(Item product, long UserId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TABLE_PRODUCT_COLUMN_NAME, product.getName());
        values.put(TABLE_PRODUCT_COLUMN_QUANTITY, product.getQuantity());
        values.put(TABLE_PRODUCT_COLUMN_PRICE, product.getUnitPrice());
        values.put(TABLE_PRODUCT_COLUMN_USER_ID, UserId);
        long id = db.insert(TABLE_PRODUCT, null, values);
        db.close();
        return id;
    }
    public ArrayList<Item> getProducts(long UserId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCT,
                new String[]{TABLE_PRODUCT_COLUMN_ID, TABLE_PRODUCT_COLUMN_NAME, TABLE_PRODUCT_COLUMN_QUANTITY, TABLE_PRODUCT_COLUMN_PRICE},
                TABLE_PRODUCT_COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(UserId)},
                null, null, null);
        ArrayList<Item> items = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do {
                Item item = new Item(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3)
                );
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }
    public boolean removeProductById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_PRODUCT,
                TABLE_PRODUCT_COLUMN_ID + "= ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return result > 0;
    }

    // ===> CÁC HÀM MỚI ĐỂ QUẢN LÝ CHI TIÊU ĐỊNH KÌ <===
    public long addRecurringExpense(RecurringExpense expense, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TABLE_RECURRING_COLUMN_NAME, expense.getName());
        values.put(TABLE_RECURRING_COLUMN_AMOUNT, expense.getAmount());
        values.put(TABLE_RECURRING_COLUMN_TYPE, expense.getType());
        values.put(TABLE_RECURRING_COLUMN_DAY_OF_MONTH, expense.getDayOfMonth());
        values.put(TABLE_RECURRING_COLUMN_USER_ID, userId);
        long id = db.insert(TABLE_RECURRING, null, values);
        db.close();
        return id;
    }

    public ArrayList<RecurringExpense> getRecurringExpenses(long userId) {
        ArrayList<RecurringExpense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECURRING,
                new String[]{TABLE_RECURRING_COLUMN_ID, TABLE_RECURRING_COLUMN_NAME, TABLE_RECURRING_COLUMN_AMOUNT, TABLE_RECURRING_COLUMN_TYPE, TABLE_RECURRING_COLUMN_DAY_OF_MONTH},
                TABLE_RECURRING_COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                RecurringExpense expense = new RecurringExpense(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getString(3),
                        cursor.getInt(4)
                );
                expenses.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return expenses;
    }

    public boolean removeRecurringExpenseById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_RECURRING, TABLE_RECURRING_COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }
}
