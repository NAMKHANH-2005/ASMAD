package com.example.se07203_b5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter; // SỬA: Đổi lại thành ArrayAdapter
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // --- 1. KHAI BÁO BIẾN (Giữ nguyên ID cũ, thêm nút mới) ---
    Button btnCreate, btnLogout, btnSetLimit, btnExpenseOverview, btnRecurringExpenses; // Thêm nút btnSetLimit
    ListView lvListItem;
    TextView tvListTitle, tvReport;
    ArrayAdapter<Item> adapter; // SỬA: Sử dụng lại ArrayAdapter<Item> như yêu cầu
    DatabaseHelper dbHelper;
    SharedPreferences sharedPreferences;

    double spendingLimit = 0; // Biến để lưu giới hạn chi tiêu
    private static final int REQUEST_CODE_EDIT_ADD = 101; // Mã yêu cầu chung

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- 2. KIỂM TRA ĐĂNG NHẬP (LÀM ĐẦU TIÊN) ---
        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("isLogin", false)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // SỬA: Phải gọi finish() để đóng MainActivity
            return;
        }

        setContentView(R.layout.activity_main);

        // --- 3. ÁNH XẠ VIEW (Giữ nguyên ID cũ, thêm nút mới) ---
        btnCreate = findViewById(R.id.btnCreate);
        btnLogout = findViewById(R.id.btnLogout);
        lvListItem = findViewById(R.id.lvItem);
        tvListTitle = findViewById(R.id.tvListTitle);
        tvReport = findViewById(R.id.tvReport);
        btnSetLimit = findViewById(R.id.btnSetLimit); // Ánh xạ nút mới
        btnExpenseOverview = findViewById(R.id.btnExpenseOverview);
        btnRecurringExpenses = findViewById(R.id.btnRecurringExpenses);
        // --- 4. KHỞI TẠO CÁC ĐỐI TƯỢNG ---
        dbHelper = new DatabaseHelper(this);
        // SỬA: Khởi tạo lại ArrayAdapter như code gốc của bạn
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, AppData.ListItem);
        lvListItem.setAdapter(adapter);

        // --- 5. TẢI DỮ LIỆU TỪ DATABASE VÀ HIỂN THỊ ---
        loadDataAndRefreshUI();

        // --- 6. CÀI ĐẶT SỰ KIỆN CLICK ---
        btnCreate.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateNewTaskActivity.class);
            startActivityForResult(intent, REQUEST_CODE_EDIT_ADD);
        });

        btnLogout.setOnClickListener(v -> logout());

        // Gắn sự kiện cho nút Đặt Giới Hạn
        btnSetLimit.setOnClickListener(v -> showSetLimitDialog());
        btnExpenseOverview.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReportActivity.class);
            startActivity(intent);
        });
        btnRecurringExpenses.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecurringActivity.class);
            startActivity(intent);
        });

        lvListItem.setOnItemClickListener((parent, view, position, id) -> {
            showOptionsDialog(position);
        });
    }

    /**
     * Tải lại toàn bộ dữ liệu từ DB (danh sách và giới hạn) rồi cập nhật giao diện.
     */
    private void loadDataAndRefreshUI() {
        long userId = sharedPreferences.getLong("user_id", -1);
        if (userId != -1) {
            // Lấy danh sách sản phẩm
            ArrayList<Item> itemsFromDb = dbHelper.getProducts(userId);
            // Lấy giới hạn chi tiêu
            spendingLimit = dbHelper.getUserSpendingLimit(userId);

            // SỬA (RẤT QUAN TRỌNG): Cập nhật danh sách tĩnh đúng cách
            AppData.ListItem.clear(); // Xóa sạch dữ liệu cũ
            AppData.ListItem.addAll(itemsFromDb); // Thêm tất cả dữ liệu mới vào
        }
        updateSpendingReport(); // Gọi hàm cập nhật giao diện
    }

    /**
     * Cập nhật TextView báo cáo và màu sắc cảnh báo.
     * Sử dụng tvReport mà bạn đã khai báo.
     */
    private void updateSpendingReport() {
        double totalPrice = 0;
        for (Item item : AppData.ListItem) {
            totalPrice += item.getUnitPrice() * item.getQuantity();
        }

        // Định dạng chuỗi báo cáo
        String report = String.format("Tổng chi: %,.0f / Giới hạn: %,.0f", totalPrice, spendingLimit);
        tvReport.setText(report); // Sử dụng ID cũ tvReport

        // Đặt màu cảnh báo
        if (spendingLimit > 0) {
            if (totalPrice > spendingLimit) {
                tvReport.setTextColor(Color.RED);
            } else if (totalPrice >= spendingLimit * 0.8) {
                tvReport.setTextColor(Color.rgb(255, 165, 0)); // Orange
            } else {
                tvReport.setTextColor(Color.BLACK);
            }
        } else {
            tvReport.setTextColor(Color.BLACK);
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * Hiển thị Dialog cho phép người dùng đặt/sửa giới hạn chi tiêu.
     */
    private void showSetLimitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đặt Giới Hạn Chi Tiêu");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Nhập số tiền giới hạn");
        if (spendingLimit > 0) {
            input.setText(String.valueOf(spendingLimit));
        }
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String limitStr = input.getText().toString();
            if (!limitStr.isEmpty()) {
                try {
                    spendingLimit = Double.parseDouble(limitStr);
                    long userId = sharedPreferences.getLong("user_id", -1);
                    dbHelper.updateUserSpendingLimit(userId, spendingLimit);
                    Toast.makeText(this, "Đã lưu giới hạn mới!", Toast.LENGTH_SHORT).show();
                    updateSpendingReport();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Vui lòng nhập một con số hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Xử lý đăng xuất, xóa SharedPreferences và chuyển về màn hình Login.
     */
    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLogin", false);
        editor.remove("user_id");
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Hiển thị dialog lựa chọn Sửa hoặc Xóa.
     */
    private void showOptionsDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lựa chọn hành động");
        String[] options = {"Sửa", "Xóa"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) { // Sửa
                Intent intent = new Intent(MainActivity.this, CreateNewTaskActivity.class);
                intent.putExtra("position", position);
                startActivityForResult(intent, REQUEST_CODE_EDIT_ADD);
            } else { // Xóa
                Item itemToDelete = AppData.ListItem.get(position);
                if (dbHelper.removeProductById(itemToDelete.getId())) {
                    Toast.makeText(this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                    loadDataAndRefreshUI(); // Tải lại dữ liệu từ DB và làm mới
                } else {
                    Toast.makeText(this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.create().show();
    }

    /**
     * Nhận kết quả trả về từ màn hình thêm/sửa.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_ADD && resultCode == RESULT_OK) {
            loadDataAndRefreshUI();
            Toast.makeText(this, "Danh sách đã được cập nhật!", Toast.LENGTH_SHORT).show();
        }
    }
}
