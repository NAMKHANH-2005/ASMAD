package com.example.se07203_b5;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmScheduler {

    // ===== HẰNG SỐ TRUYỀN DỮ LIỆU =====
    public static final String EXTRA_NOTIFICATION_ID = "notification_id";
    public static final String EXTRA_EXPENSE_NAME = "expense_name";
    public static final String EXTRA_EXPENSE_AMOUNT = "expense_amount";

    // ===== ĐẶT LỊCH NHẮC NHỞ HÀNG THÁNG =====
    public static void scheduleMonthlyAlarm(Context context, RecurringExpense expense) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationReceiver.class);

        // Truyền dữ liệu sang BroadcastReceiver
        intent.putExtra(EXTRA_NOTIFICATION_ID, expense.getId());
        intent.putExtra(EXTRA_EXPENSE_NAME, expense.getName());
        intent.putExtra(EXTRA_EXPENSE_AMOUNT, expense.getAmount());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                expense.getId(),  // Mỗi khoản chi một requestCode riêng
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // ===== CẤU HÌNH THỜI GIAN =====
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, expense.getDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, expense.getHour());
        calendar.set(Calendar.MINUTE, expense.getMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Nếu thời gian trong tháng đã trôi qua → đặt sang tháng sau
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.MONTH, 1);
        }

        // Android 12+ cần quyền SCHEDULE_EXACT_ALARM
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && !alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(context, "Ứng dụng cần quyền đặt báo thức chính xác.", Toast.LENGTH_LONG).show();
            return;
        }

        // ===== ĐẶT ALARM CHÍNH XÁC =====
        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );

        Toast.makeText(context, "Đã đặt lịch nhắc nhở cho: " + expense.getName(), Toast.LENGTH_SHORT).show();
    }

    // ===== HỦY ALARM =====
    public static void cancelAlarm(Context context, RecurringExpense expense) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                expense.getId(),
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}