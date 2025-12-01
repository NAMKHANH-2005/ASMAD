package com.example.se07203_b5;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MonthlyReportActivity extends AppCompatActivity {

    private TextView tvCurrentMonth, tvMonthlyTotal;
    private Button btnPreviousMonth, btnNextMonth, btnBackFromMonthly;
    private BarChart barChart;
    private ListView lvMonthlyItems;

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private Calendar currentCalendar;
    private ArrayAdapter<String> itemsAdapter;
    private ArrayList<String> itemDetailsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_report);

        tvCurrentMonth = findViewById(R.id.tvCurrentMonth);
        tvMonthlyTotal = findViewById(R.id.tvMonthlyTotal);
        btnPreviousMonth = findViewById(R.id.btnPreviousMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        barChart = findViewById(R.id.barChart);
        lvMonthlyItems = findViewById(R.id.lvMonthlyItems);
        btnBackFromMonthly = findViewById(R.id.btnBackFromMonthly);

        dbHelper = new DatabaseHelper(this);
        currentCalendar = Calendar.getInstance();
        itemDetailsList = new ArrayList<>();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemDetailsList);
        lvMonthlyItems.setAdapter(itemsAdapter);

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);

        btnPreviousMonth.setOnClickListener(v -> changeMonth(-1));
        btnNextMonth.setOnClickListener(v -> changeMonth(1));
        btnBackFromMonthly.setOnClickListener(v -> finish());
        setupBarChart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentCalendar = Calendar.getInstance();
        updateReport();
    }

    private void changeMonth(int amount) {
        currentCalendar.add(Calendar.MONTH, amount);
        updateReport();
    }

    private void updateReport() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        tvCurrentMonth.setText(monthFormat.format(currentCalendar.getTime()));

        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String yearMonth = dbFormat.format(currentCalendar.getTime());
        long userId = sharedPreferences.getLong("user_id", -1);

        if (userId == -1) {
            return;
        }

        ArrayList<Item> items = dbHelper.getProductsForMonth(userId, yearMonth);
        updateChartAndList(items);
    }

    private void setupBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
    }

    private void updateChartAndList(ArrayList<Item> items) {
        if (items == null || items.isEmpty()) {
            barChart.clear();
            barChart.invalidate();
            itemDetailsList.clear();
            itemsAdapter.notifyDataSetChanged();
            tvMonthlyTotal.setText("Tổng chi tiêu tháng: 0 VNĐ");
            return;
        }

        double totalMonthlySpending = 0;
        itemDetailsList.clear();

        // ===> SỬA LỖI 1: DÙNG Map<String, Double> ĐỂ KHỚP VỚI KIỂU DỮ LIỆU TÍNH TOÁN <===
        Map<String, Double> spendingPerItem = new HashMap<>();
        for (Item item : items) {
            // Tính toán bằng double để đảm bảo độ chính xác
            double itemTotalCost = item.getUnitPrice() * item.getQuantity();
            totalMonthlySpending += itemTotalCost;

            // ===> SỬA LỖI 2: ĐỊNH DẠNG CHUỖI ĐÚNG CÁCH VỚI KIỂU DOUBLE <===
            String detail = String.format(Locale.US, "%s (%d x %,.0f) = %,.0f VNĐ",
                    item.getName(), item.getQuantity(), item.getUnitPrice(), itemTotalCost);
            itemDetailsList.add(detail);

            spendingPerItem.put(item.getName(), spendingPerItem.getOrDefault(item.getName(), 0.0) + itemTotalCost);
        }

        itemsAdapter.notifyDataSetChanged();
        tvMonthlyTotal.setText(String.format(Locale.US, "Tổng chi tiêu tháng: %,.0f VNĐ", totalMonthlySpending));

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, Double> entry : spendingPerItem.entrySet()) {
            // Chuyển từ Double sang float chỉ khi thêm vào BarEntry
            entries.add(new BarEntry(i, entry.getValue().floatValue()));
            labels.add(entry.getKey());
            i++;
        }
        if (!labels.isEmpty()) {
            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
            xAxis.setLabelCount(labels.size());
            if (labels.size() > 5) {
                xAxis.setLabelRotationAngle(-45);
            } else {
                xAxis.setLabelRotationAngle(0);
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "Chi tiêu");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        barChart.setData(barData);
        barChart.invalidate();
        barChart.animateY(1000);
    }
}