package com.example.se07203_b5;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RecurringActivity extends AppCompatActivity {

    private ListView lvRecurringExpenses;
    private Button btnAddRecurring, btnBackFromRecurring;
    private ArrayList<RecurringExpense> recurringExpenseList;
    private ArrayAdapter<RecurringExpense> adapter;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurring);

        // Ánh xạ View
        lvRecurringExpenses = findViewById(R.id.lvRecurringExpenses);
        btnAddRecurring = findViewById(R.id.btnAddRecurring);
        btnBackFromRecurring = findViewById(R.id.btnBackFromRecurring);

        // Khởi tạo
        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
        recurringExpenseList = new ArrayList<>();

        // Cài đặt Adapter cho ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recurringExpenseList);
        lvRecurringExpenses.setAdapter(adapter);

        // Tải dữ liệu
        loadRecurringExpenses();

        // Cài đặt sự kiện
        btnAddRecurring.setOnClickListener(v -> showAddRecurringDialog());
        btnBackFromRecurring.setOnClickListener(v -> finish());

        // Sự kiện nhấn giữ để xóa
        lvRecurringExpenses.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteDialog(position);
            return true; // Đánh dấu sự kiện đã được xử lý
        });
    }

    private void loadRecurringExpenses() {
        long userId = sharedPreferences.getLong("user_id", -1);
        if (userId != -1) {
            ArrayList<RecurringExpense> expensesFromDb = dbHelper.getRecurringExpenses(userId);
            recurringExpenseList.clear();
            recurringExpenseList.addAll(expensesFromDb);
            adapter.notifyDataSetChanged();
        }
    }

    private void showDeleteDialog(int position) {
        RecurringExpense expenseToDelete = recurringExpenseList.get(position);
        new AlertDialog.Builder(this)
                .setTitle("Xác Nhận Xóa")
                .setMessage("Bạn có chắc chắn muốn xóa khoản chi định kì '" + expenseToDelete.getName() + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean result = dbHelper.removeRecurringExpenseById(expenseToDelete.getId());
                    if (result) {
                        Toast.makeText(RecurringActivity.this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                        loadRecurringExpenses(); // Tải lại danh sách
                    } else {
                        Toast.makeText(RecurringActivity.this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showAddRecurringDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm Chi Tiêu Định Kì Mới");

        // Sử dụng layout inflater để tạo view từ XML
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_recurring, null);
        builder.setView(dialogView);

        final EditText etName = dialogView.findViewById(R.id.etRecurringName);
        final EditText etAmount = dialogView.findViewById(R.id.etRecurringAmount);
        final EditText etDay = dialogView.findViewById(R.id.etRecurringDay);
        final RadioGroup rgType = dialogView.findViewById(R.id.rgRecurringType);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = etName.getText().toString();
            String amountStr = etAmount.getText().toString();
            String dayStr = etDay.getText().toString();
            int selectedTypeId = rgType.getCheckedRadioButtonId();

            if (name.isEmpty() || amountStr.isEmpty() || dayStr.isEmpty() || selectedTypeId == -1) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                int day = Integer.parseInt(dayStr);
                RadioButton selectedRadioButton = dialogView.findViewById(selectedTypeId);
                String type = selectedRadioButton.getText().toString().equalsIgnoreCase("Hàng tháng") ? "monthly" : "yearly";

                if (day < 1 || day > 31) {
                    Toast.makeText(this, "Ngày trong tháng không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                long userId = sharedPreferences.getLong("user_id", -1);
                RecurringExpense newExpense = new RecurringExpense(0, name, amount, type, day); // ID tạm là 0
                long newId = dbHelper.addRecurringExpense(newExpense, userId);

                if (newId != -1) {
                    Toast.makeText(this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
                    loadRecurringExpenses(); // Tải lại danh sách
                } else {
                    Toast.makeText(this, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
                }

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số tiền hoặc ngày không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
