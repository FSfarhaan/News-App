package com.example.news.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.news.data.SharedPreferencesHelper;

import java.util.Calendar;

public class AlarmScheduler {

    public static void scheduleNotification(Context context) {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);

        int notificationStatus = sharedPreferencesHelper.getNotificationStatus();
        int firstNotificationAt = sharedPreferencesHelper.getFirstNotificationAt();
        int notificationInterval = sharedPreferencesHelper.getNotificationRepeatInterval();

        if (notificationStatus == 1) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            // Set the time for the first notification
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, firstNotificationAt);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            long repeatInterval = notificationInterval * AlarmManager.INTERVAL_HOUR;

            // Set the repeating alarm
            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), repeatInterval, pendingIntent);
            }
        }
    }

    public static void cancelNotification(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
