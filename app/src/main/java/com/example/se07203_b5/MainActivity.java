package com.example.se07203_b5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnCreate, btnLogout;
    ListView lvListItem;
    int count = 0;
    TextView tvListTitle, tvReport; // khai báo TextView để hiển thị tiêu đề danh sách task
    ArrayAdapter<Item> adapter; // khai báo adapter - công cụ để kết nối danh sách task với ListView

    DatabaseHelper dbHelper;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("isLogin", false)){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // kết nối với layout activity_main với class MainActivity

        btnCreate = findViewById(R.id.btnCreate); // liên kết với id btnCreate trong layout
        btnLogout = findViewById(R.id.btnLogout); // liên kết với id btnShow trong layout
        lvListItem = findViewById(R.id.lvItem); // liên kết với id lvTask trong layout
        tvListTitle = findViewById(R.id.tvListTitle); // liên kết với id tvListTitle trong layout
        tvReport = findViewById(R.id.tvReport);
        dbHelper = new DatabaseHelper(this); // Khởi tạo kết nối database

        long userId = sharedPreferences.getLong("user_id", 0);

        ArrayList<Item> _items = dbHelper.getProducts(userId);
        AppData.ListItem.clear();
        AppData.ListItem = _items;


        // Khởi tạo adapter với "this" là context - chính là lớp MainActivity
        // và android.R.layout.simple_list_item_1 là layout sẵn có trong Android (chỉ 1 dòng text)
        // và ListItem là danh sách các task (thuộc kiểu ArrayList<Item> đã được khai báo ở trên)
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, AppData.ListItem);
        // Đặt adapter cho ListView (lvListTask) để hiển thị danh sách các task
        lvListItem.setAdapter(adapter);
        showReport();
        adapter.notifyDataSetChanged(); // thông báo cho adapter rằng dữ liệu đã thay đổi và cần cập nhật lên ListView

        btnCreate.setOnClickListener(v -> {
            // Khai báo Intent để chuyển sang CreateNewTaskActivity (di chuyển từ activity MainActivity sang activity CreateNewTaskActivity)
            Intent intent = new Intent(MainActivity.this, CreateNewTaskActivity.class);
            startActivity(intent);
        });

        lvListItem.setOnItemClickListener((parent, view, position, id) -> {
            Toast.makeText(this, "Bạn chọn item thứ " + (position + 1) + ", món đồ " + AppData.ListItem.get(position), Toast.LENGTH_LONG).show();
            showOptionsDialog(position);
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putBoolean("isLogin", false);
            sharedPreferencesEditor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void showOptionsDialog(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lựa chọn hành động!");
        String[] options = {"Sửa", "Xóa"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0){ // Hành động đầu tiên trong options - "Sửa"
                // Khai báo Intent để chuyển sang CreateNewTaskActivity (di chuyển từ activity MainActivity sang activity CreateNewTaskActivity)
                Intent intent = new Intent(MainActivity.this, CreateNewTaskActivity.class);
                // bổ sung thông tin về task cần sửa vào Intent, thông tin position là vị trí task trong danh sách
                intent.putExtra("position", position);
                // Chuyển sang activity CreateNewTaskActivity và đợi kết quả trả về
                startActivityForResult(intent, AppData.EDIT_TASK);
            }else {
                Item _item = AppData.ListItem.get(position);
                long itemId = _item.getId();

                boolean result = dbHelper.removeProductById(itemId);

                if (result) {
                    AppData.ListItem.remove(position);
                    showReport();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppData.EDIT_TASK && resultCode == RESULT_OK){
            showReport();
            adapter.notifyDataSetChanged();}
    }

    private void showReport(){
        tvReport.setText("Số đồ cần mua: " + AppData.ListItem.size() + " - Tổng tiền: " + AppData.getTotalBill());
    }
}