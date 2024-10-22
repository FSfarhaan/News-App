package com.example.news.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private static final String PREFS_NAME = "com.example.news";

    // Keys for the preferences
    private static final String KEY_NOTIFICATION_STATUS = "notification_status";
    private static final String KEY_FIRST_NOTIFICATION_AT = "first_notification_at";
    private static final String KEY_NOTIFICATION_REPEAT_INTERVAL = "notification_repeat_interval";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_MAX_NUMBERS = "max_numbers";

    private SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Save notification status (0 or 1)
    public void setNotificationStatus(int status) {
        sharedPreferences.edit().putInt(KEY_NOTIFICATION_STATUS, status).apply();
    }

    public int getNotificationStatus() {
        return sharedPreferences.getInt(KEY_NOTIFICATION_STATUS, 0);  // Default is 0
    }

    // Save first notification time (1 to 24)
    public void setFirstNotificationAt(int hour) {
        sharedPreferences.edit().putInt(KEY_FIRST_NOTIFICATION_AT, hour).apply();
    }

    public int getFirstNotificationAt() {
        return sharedPreferences.getInt(KEY_FIRST_NOTIFICATION_AT, 0);  // Default is 0
    }

    // Save notification repeat interval (24, 12, or 6)
    public void setNotificationRepeatInterval(int hours) {
        sharedPreferences.edit().putInt(KEY_NOTIFICATION_REPEAT_INTERVAL, hours).apply();
    }

    public int getNotificationRepeatInterval() {
        return sharedPreferences.getInt(KEY_NOTIFICATION_REPEAT_INTERVAL, 24);  // Default is 24
    }

    // Save language
    public void setLanguage(String language) {
        sharedPreferences.edit().putString(KEY_LANGUAGE, language).apply();
    }

    public String getLanguage() {
        return sharedPreferences.getString(KEY_LANGUAGE, "en");  // Default is English
    }

    // Save country
    public void setCountry(String country) {
        sharedPreferences.edit().putString(KEY_COUNTRY, country).apply();
    }

    public String getCountry() {
        return sharedPreferences.getString(KEY_COUNTRY, "in");  // Default is India
    }

    // Save max numbers
    public void setMaxNumbers(int maxNumbers) {
        sharedPreferences.edit().putInt(KEY_MAX_NUMBERS, maxNumbers).apply();
    }

    public int getMaxNumbers() {
        return sharedPreferences.getInt(KEY_MAX_NUMBERS, 20);  // Default is 10
    }

    // Clear specific value - Notification status
    public void clearNotificationStatus() {
        sharedPreferences.edit().remove(KEY_NOTIFICATION_STATUS).apply();
    }

    // Clear specific value - First notification time
    public void clearFirstNotificationAt() {
        sharedPreferences.edit().remove(KEY_FIRST_NOTIFICATION_AT).apply();
    }

    // Clear specific value - Notification repeat interval
    public void clearNotificationRepeatInterval() {
        sharedPreferences.edit().remove(KEY_NOTIFICATION_REPEAT_INTERVAL).apply();
    }

    // Clear specific value - Language
    public void clearLanguage() {
        sharedPreferences.edit().remove(KEY_LANGUAGE).apply();
    }

    // Clear specific value - Country
    public void clearCountry() {
        sharedPreferences.edit().remove(KEY_COUNTRY).apply();
    }

    // Clear specific value - Max numbers
    public void clearMaxNumbers() {
        sharedPreferences.edit().remove(KEY_MAX_NUMBERS).apply();
    }

    // Clear all data
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
