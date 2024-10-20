package com.example.news.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class AlarmScheduler {

    public static void scheduleRepeatingNotification(Context context, int intervalMinutes) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Create an Intent to trigger the BroadcastReceiver (NotificationReceiver)
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Log.d("AlarmScheduler", "Scheduling repeating notification every " + intervalMinutes + " minute(s)");

        // Schedule the repeating alarm
        if (alarmManager != null) {
            long intervalMillis = intervalMinutes * 60 * 1000; // Convert minutes to milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + intervalMillis, intervalMillis, pendingIntent);
            } else {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + intervalMillis, intervalMillis, pendingIntent);
            }
        }
    }
}
