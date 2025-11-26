package com.example.se07203_b5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class CreateNewTaskActivity extends AppCompatActivity {

    Button btnSubmitCreate, btnBackToMain;
    TextView titlePageCreateEdit;
    Boolean isEditMode = false;
    int position = -1;

    SharedPreferences sharedPreferences;

    EditText edtItemName, edtQuantity, edtUnitPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_new_task);

        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);

        btnSubmitCreate = findViewById(R.id.btnSubmitCreate);
        btnBackToMain = findViewById(R.id.btnBackToMain);
        edtItemName = findViewById(R.id.edtItemName);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtUnitPrice = findViewById(R.id.edtUnitPrice);
        titlePageCreateEdit = findViewById(R.id.titlePageCreateEdit);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            position = intent.getIntExtra("position", -1);
            if (position > -1) {
                isEditMode = true;
                titlePageCreateEdit.setText("Sửa thông tin");
                // set các thông tin item cần sửa lên EditText
                edtItemName.setText(AppData.ListItem.get(position).getName());
                edtQuantity.setText(String.valueOf(AppData.ListItem.get(position).getQuantity()));
                edtUnitPrice.setText(String.valueOf(AppData.ListItem.get(position).getUnitPrice()));
            }else{
                isEditMode = false;
            }
        }else{
            isEditMode = false;
        }

        btnSubmitCreate.setOnClickListener(v -> {
            if (isEditMode){
                editAnItem();
            }else{
                createNewItem();
            }
        });

        btnBackToMain.setOnClickListener(v -> {
            backToMain();
        });

    }

    private void backToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void editAnItem(){
        String itemName = edtItemName.getText().toString();
        int quantity = 0, unitPrice = 0;
        try {
            quantity = Integer.parseInt(edtQuantity.getText().toString());
            unitPrice = Integer.parseInt(edtUnitPrice.getText().toString());
        }catch (NumberFormatException e){
            edtQuantity.setError("Số lượng phải lớn hơn 0");
            return;
        }
        AppData.ListItem.get(position).setName(itemName);
        AppData.ListItem.get(position).setQuantity(quantity);
        AppData.ListItem.get(position).setUnitPrice(unitPrice);
        setResult(RESULT_OK);
        finish();
    }

    private void createNewItem(){
        String itemName = edtItemName.getText().toString();
        int quantity = 0, unitPrice = 0;
        try {
            quantity = Integer.parseInt(edtQuantity.getText().toString());
            unitPrice = Integer.parseInt(edtUnitPrice.getText().toString());
        }catch (NumberFormatException e){
            edtQuantity.setError("Số lượng phải lớn hơn 0");
            return;
        }

        if (quantity < 1){
            edtQuantity.setError("Số lượng phải lớn hơn 0");
            return;
        }else {
            Item item = new Item(itemName, quantity, unitPrice);
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            long userId = sharedPreferences.getLong("user_id", -1);
            if (userId > 0){
                long resultId = databaseHelper.addProduct(item, userId);
                if (resultId <= 0){
                    Toast.makeText(this, "Error add product (item) to database", Toast.LENGTH_SHORT).show();
                    return;
                }
                AppData.ListItem.add(item);
                Toast.makeText(this, "Add product successfully (Id = " + resultId + ")", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(this, "Error get userId", Toast.LENGTH_SHORT).show();
            }

        }
    }

}