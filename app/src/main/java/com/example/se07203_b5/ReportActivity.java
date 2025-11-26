package com.example.se07203_b5;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {

    private PieChart pieChart;
    private TextView tvReportDetails;
    private Button btnBackToMain; // 1. Khai báo nút

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Ánh xạ các view
        pieChart = findViewById(R.id.pieChart);
        tvReportDetails = findViewById(R.id.tvReportDetails);
        btnBackToMain = findViewById(R.id.btnBackToMain); // 2. Ánh xạ nút

        setupPieChart();
        loadChartData();

        // 3. Thiết lập sự kiện click cho nút "Quay Lại"
        btnBackToMain.setOnClickListener(v -> {
            finish(); // Lệnh này sẽ đóng Activity hiện tại và quay về Activity trước đó (là MainActivity)
        });
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Chi Tiêu");
        pieChart.setCenterTextSize(24f);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(true);
    }

    private void loadChartData() {
        ArrayList<Item> items = AppData.ListItem;

        if (items == null || items.isEmpty()) {
            tvReportDetails.setText("Không có dữ liệu để hiển thị.");
            pieChart.clear();
            return;
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        double totalSpending = 0;
        int totalQuantity = 0;

        for (Item item : items) {
            double itemTotalCost = item.getUnitPrice() * item.getQuantity();
            totalSpending += itemTotalCost;
            totalQuantity += item.getQuantity();
            entries.add(new PieEntry((float) itemTotalCost, item.getName()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Danh mục chi tiêu");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

        String reportDetails = String.format(
                "Tổng chi tiêu: %,.0f\n" +
                        "Tổng số lượng hàng hóa: %d\n" +
                        "Số loại hàng hóa: %d",
                totalSpending, totalQuantity, items.size()
        );
        tvReportDetails.setText(reportDetails);
    }
}
