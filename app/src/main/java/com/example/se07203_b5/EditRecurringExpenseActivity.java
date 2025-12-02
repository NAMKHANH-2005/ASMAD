package com.example.se07203_b5;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class EditRecurringExpenseActivity extends AppCompatActivity {

    // Khai báo các biến giao diện, đã sửa lại tên biến
    private EditText etName, etAmount, etDay;
    private RadioGroup rgType;
    private RadioButton rbMonthly, rbYearly;
    // ===> SỬA 1: DÙNG TÊN "btnDelete" CHO NHẤT QUÁN <===
    private Button btnUpdate, btnCancel, btnDelete, btnPickTime;

    private RecurringExpense currentExpense;
    private DatabaseHelper dbHelper;

    private final int[] selectedHour = new int[1];
    private final int[] selectedMinute = new int[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recurring_expense);

        etName = findViewById(R.id.etEditRecurringName);
        etAmount = findViewById(R.id.etEditRecurringAmount);
        etDay = findViewById(R.id.etEditRecurringDay);
        rgType = findViewById(R.id.rgEditRecurringType);
        rbMonthly = findViewById(R.id.rbEditMonthly);
        rbYearly = findViewById(R.id.rbEditYearly);
        btnUpdate = findViewById(R.id.btnUpdateRecurring);
        btnCancel = findViewById(R.id.btnCancelEdit);
        btnPickTime = findViewById(R.id.btnEditPickTime);

        // ===> SỬA 2: ÁNH XẠ VÀO BIẾN "btnDelete" <===
        btnDelete = findViewById(R.id.btnDeleteRecurring);

        dbHelper = new DatabaseHelper(this);

        currentExpense = (RecurringExpense) getIntent().getSerializableExtra("expense");
        if (currentExpense != null) {
            etName.setText(currentExpense.getName());
            etAmount.setText(String.valueOf(currentExpense.getAmount()));
            etDay.setText(String.valueOf(currentExpense.getDayOfMonth()));
            selectedHour[0] = currentExpense.getHour();
            selectedMinute[0] = currentExpense.getMinute();
            btnPickTime.setText(String.format(Locale.US, "Giờ thông báo: %02d:%02d", selectedHour[0], selectedMinute[0]));

            if ("monthly".equalsIgnoreCase(currentExpense.getType())) {
                rbMonthly.setChecked(true);
            } else {
                rbYearly.setChecked(true);
            }
        }

        btnPickTime.setOnClickListener(v -> showTimePicker());
        btnUpdate.setOnClickListener(v -> updateExpense());
        btnCancel.setOnClickListener(v -> finish());

        // ===> SỬA 3: GÁN SỰ KIỆN CHO BIẾN "btnDelete" <===
        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }
    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedHour[0] = hourOfDay;
                    selectedMinute[0] = minute;
                    btnPickTime.setText(String.format(Locale.US, "Giờ thông báo: %02d:%02d", hourOfDay, minute));
                },
                selectedHour[0], selectedMinute[0], true
        );
        timePickerDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        if (currentExpense == null) return;
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa vĩnh viễn khoản chi '" + currentExpense.getName() + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteExpense())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteExpense() {
        if (currentExpense == null) return;
        AlarmScheduler.cancelAlarm(this, currentExpense);
        if (dbHelper.removeRecurringExpenseById(currentExpense.getId())) {
            Toast.makeText(this, "Đã xóa thành công!", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra(RecurringActivity.EXTRA_DELETED_EXPENSE_ID, currentExpense.getId());
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateExpense() {
        String name = etName.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String dayStr = etDay.getText().toString().trim();
        if (name.isEmpty() || amountStr.isEmpty() || dayStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            int day = Integer.parseInt(dayStr);
            String type = rbMonthly.isChecked() ? "monthly" : "yearly";
            if (day < 1 || day > 31) {
                Toast.makeText(this, "Ngày không hợp lệ (1-31)", Toast.LENGTH_SHORT).show();
                return;
            }

            currentExpense.setName(name);
            currentExpense.setAmount(amount);
            currentExpense.setDayOfMonth(day);
            currentExpense.setType(type);
            currentExpense.setHour(selectedHour[0]);
            currentExpense.setMinute(selectedMinute[0]);

            int rowsAffected = dbHelper.updateRecurringExpense(currentExpense);

            if (rowsAffected > 0) {
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updated_expense", currentExpense);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại hoặc không có gì thay đổi", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền hoặc ngày không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
}