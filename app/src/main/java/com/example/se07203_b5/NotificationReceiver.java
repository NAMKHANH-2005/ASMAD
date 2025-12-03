package com.example.se07203_b5;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.text.NumberFormat;
import java.util.Locale;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "RECURRING_EXPENSE_CHANNEL";
    private static final CharSequence CHANNEL_NAME = "Thông báo chi tiêu định kỳ";

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // ===== LẤY DỮ LIỆU =====
        String expenseName = intent.getStringExtra(AlarmScheduler.EXTRA_EXPENSE_NAME);
        double expenseAmount = intent.getDoubleExtra(AlarmScheduler.EXTRA_EXPENSE_AMOUNT, 0.0);
        int notificationId = intent.getIntExtra(AlarmScheduler.EXTRA_NOTIFICATION_ID, 0);

        // ===== TẠO CHANNEL (bắt buộc Android 8+) =====
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Kênh gửi thông báo cho chi tiêu định kỳ");

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Định dạng tiền
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedAmount = formatter.format(expenseAmount);

        // ===== TẠO NOTIFICATION =====
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification) // nhớ thêm icon vào res/drawable
                .setContentTitle("Đến hạn thanh toán: " + expenseName)
                .setContentText("Vui lòng thanh toán khoản chi: " + formattedAmount)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // ===== HIỂN THỊ =====
        if (notificationManager != null) {
            notificationManager.notify(notificationId, builder.build());
        }
    }
}