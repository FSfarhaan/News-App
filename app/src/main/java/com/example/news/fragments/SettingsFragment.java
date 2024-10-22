package com.example.news.fragments;

import static android.content.Context.POWER_SERVICE;
import static android.content.res.Resources.getSystem;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.news.R;
import com.example.news.data.DbHelper;
import com.example.news.data.SharedPreferencesHelper;
import com.example.news.utils.AlarmScheduler;
import com.example.news.utils.NotificationReceiver;

public class SettingsFragment extends Fragment {
    ImageView toggleNotifications, deleteData;
    Button selectTime, setTime;
    Spinner timeAfterSpinner, languageSpinner, countrySpinner, numberSpinner;
    CardView timePickerLL;
    TimePicker timePicker;
    View dimOverlay;
    SharedPreferencesHelper helper;
    LinearLayout extraNotification;
    DbHelper db;
    final boolean[] isNotificationOn = {false};

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        toggleNotifications = view.findViewById(R.id.toggleNotifications);
        selectTime = view.findViewById(R.id.selectTime);
        setTime = view.findViewById(R.id.setTime);
        timePickerLL = view.findViewById(R.id.timePickerLL);
        timePicker = view.findViewById(R.id.timePicker);
        dimOverlay = view.findViewById(R.id.dimOverlay);
        timeAfterSpinner = view.findViewById(R.id.timeAfterSpinner);
        languageSpinner = view.findViewById(R.id.languageSpinner);
        countrySpinner = view.findViewById(R.id.countrySpinner);
        numberSpinner = view.findViewById(R.id.numberSpinner);
        extraNotification = view.findViewById(R.id.extraNotification);
        deleteData = view.findViewById(R.id.deleteData);

        db = new DbHelper(getContext());
        helper = new SharedPreferencesHelper(getContext());

        manageBatteryPerformance();
        checkForDefaultValues();

        // Toggle Notification On/Off
        toggleNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageNotificationPermission();
            }
        });

        // Select Time for First Notification
        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timePickerLL.getVisibility() == View.GONE) {
                    timePickerLL.setVisibility(View.VISIBLE);
                    dimOverlay.setVisibility(View.VISIBLE);
                    configureTimePicker();
                    setTime.setOnClickListener(view -> {
                        int hour = timePicker.getHour();

                        if(hour < 10) selectTime.setText("0" + hour + ":00");
                        else selectTime.setText(hour + ":00");

                        helper.setFirstNotificationAt(hour);
                        timePickerLL.setVisibility(View.GONE);
                        dimOverlay.setVisibility(View.GONE);

                        callNotification(hour);
                    });
                } else {
                    timePickerLL.setVisibility(View.GONE);
                    dimOverlay.setVisibility(View.GONE);
                }
            }
        });

        // Spinner Logic for Time After Notification
        timeAfterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedInterval = parent.getItemAtPosition(position).toString();
                int interval = Integer.parseInt(selectedInterval.split(" ")[0]);
                helper.setNotificationRepeatInterval(interval);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Spinner Logic for Language Selection
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                String selectedLanguage;
                if (selectedItem.contains("(") && selectedItem.contains(")")) {
                    selectedLanguage = selectedItem.substring(selectedItem.indexOf('(') + 1, selectedItem.indexOf(')'));
                } else {
                    selectedLanguage = null; // Keep it null if default
                }
                helper.setLanguage(selectedLanguage); // Save the language code

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Spinner Logic for Country Selection
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                String selectedCountry;
                if (selectedItem.contains("(") && selectedItem.contains(")")) {
                    selectedCountry = selectedItem.substring(selectedItem.indexOf('(') + 1, selectedItem.indexOf(')'));
                } else {
                    selectedCountry = null; // Keep it null if default
                }
                helper.setCountry(selectedCountry); // Save the country code
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Spinner Logic for Maximum Number of News
        numberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMaxNews = parent.getItemAtPosition(position).toString();
                int maxNews;
                if(!selectedMaxNews.equals("Max Articles")) {
                    maxNews = Integer.parseInt(selectedMaxNews);
                }
                else maxNews = 20;
                helper.setMaxNumbers(maxNews);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        deleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Delete data")
                        .setMessage("Data will be permanently deleted. Are you sure of this?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            db.deleteAllSavedNews();
                            helper.clear();
                            Toast.makeText(getContext(), "Data deleted successfully.", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> {
                            // Do nothing, leave notifications on
                        });

                dialog.show();
            }
        });

        return view;
    }

    private void manageBatteryPerformance() {
        String packageName = requireActivity().getPackageName();
        PowerManager pm = (PowerManager) requireContext().getSystemService(POWER_SERVICE);
        if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName)) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }
    }

    public void manageNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the permission is granted
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            } else {
                // If already granted, call getAlarmPermission directly
                getAlarmPermission();
                toggleNotificationImage();
            }
        } else {
            // For older versions, directly call getAlarmPermission
            getAlarmPermission();
            toggleNotificationImage();
        }
    }

    public void toggleNotificationImage() {
        if (!isNotificationOn[0]) {  // When notifications are off, turn them on
            isNotificationOn[0] = true;
            toggleNotifications.setImageResource(R.drawable.switch_on_icon);
            extraNotification.setVisibility(View.VISIBLE);
            helper.setNotificationStatus(1);  // Assuming 1 means on
            Toast.makeText(getContext(), "Turned Notifications on", Toast.LENGTH_SHORT).show();
        } else {  // When notifications are on, prompt user to confirm turning them off
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                    .setTitle("Turn off Notifications")
                    .setMessage("You will not receive notifications. Are you sure of this?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        helper.setNotificationStatus(0);  // Assuming 0 means off
                        toggleNotifications.setImageResource(R.drawable.switch_off_icon);
                        Toast.makeText(getContext(), "Turned Notifications off", Toast.LENGTH_SHORT).show();
                        extraNotification.setVisibility(View.GONE);
                        isNotificationOn[0] = false;
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                        // Do nothing, leave notifications on
                    });
            dialog.show();
        }
    }


    public void configureTimePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            timePicker.setIs24HourView(true); // Set to false to show AM/PM
        }
        int minuteSpinnerId = Resources.getSystem().getIdentifier("minute", "id", "android");
        if (minuteSpinnerId != 0) {
            View minuteSpinner = timePicker.findViewById(minuteSpinnerId);
            if (minuteSpinner != null) {
                minuteSpinner.setVisibility(View.GONE);  // Hide the minute spinner
            }
        }
        int dividerId = Resources.getSystem().getIdentifier("divider", "id", "android");
        if (dividerId != 0) {
            View divider = timePicker.findViewById(dividerId);
            if (divider != null) {
                divider.setVisibility(View.GONE);  // Hide the divider
            }
        }
    }

    // Check for stored values and set them, if not found set defaults
    public void checkForDefaultValues() {
        // Notification Status
        int notificationStatus = helper.getNotificationStatus();
        if (notificationStatus == 1) {
            toggleNotifications.setImageResource(R.drawable.switch_on_icon);
            extraNotification.setVisibility(View.VISIBLE);
            isNotificationOn[0] = true;
        } else {
            toggleNotifications.setImageResource(R.drawable.switch_off_icon);
            extraNotification.setVisibility(View.GONE);
            isNotificationOn[0] = false;
        }

        // First Notification Time
        if(extraNotification.getVisibility() != View.GONE){
            int firstNotification = helper.getFirstNotificationAt();
            if (firstNotification != -1) {
                selectTime.setText(String.format("%02d:00", firstNotification));
            } else {
                selectTime.setText("07:00"); // Default time
                helper.setFirstNotificationAt(7);
            }

            // Time After Spinner (Notification Repeat Interval)
            int repeatInterval = helper.getNotificationRepeatInterval();
            if (repeatInterval != -1) {
                timeAfterSpinner.setSelection(getSpinnerIndex(timeAfterSpinner, repeatInterval + " Hours"));
            } else {
                timeAfterSpinner.setSelection(getSpinnerIndex(timeAfterSpinner, "24 Hours")); // Default repeat interval
                helper.setNotificationRepeatInterval(24);
            }
        }


        // Language Spinner
        String language = helper.getLanguage();
        if (language != null && !language.isEmpty()) {
            // Find the index of the item in the format "Language (code)"
            languageSpinner.setSelection(getLanguageSpinnerIndex(languageSpinner, language));
        } else {
            // Default to "English (en)" if no language is saved
            languageSpinner.setSelection(getLanguageSpinnerIndex(languageSpinner, "en"));
            helper.setLanguage("en");
        }

        // Country Spinner
        String country = helper.getCountry();
        if (country != null && !country.isEmpty()) {
            // Find the index of the item in the format "Country (code)"
            countrySpinner.setSelection(getCountrySpinnerIndex(countrySpinner, country));
        } else {
            // Default to "India (in)" if no country is saved
            countrySpinner.setSelection(getCountrySpinnerIndex(countrySpinner, "in"));
            helper.setCountry("in");
        }

        // Max Number of News
        int maxNews = helper.getMaxNumbers();
        if (maxNews != -1) {
            numberSpinner.setSelection(getSpinnerIndex(numberSpinner, String.valueOf(maxNews)));
        } else {
            numberSpinner.setSelection(getSpinnerIndex(numberSpinner, "20")); // Default max number of news
            helper.setMaxNumbers(20);
        }
    }

    // Helper method to find the index of an item in the spinner
    private int getSpinnerIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().contains(value)) {
                return i;
            }
        }
        return 0; // Default to first item if not found
    }

    private int getCountrySpinnerIndex(Spinner spinner, String countryCode) {
        for (int i = 0; i < spinner.getCount(); i++) {
            String item = spinner.getItemAtPosition(i).toString();
            // Extract the two-letter code from the spinner item, which is in parentheses
            if (item.contains("(") && item.contains(")")) {
                String code = item.substring(item.indexOf("(") + 1, item.indexOf(")"));
                if (code.equalsIgnoreCase(countryCode)) {
                    return i; // Return the matching index
                }
            }
        }
        return 0; // Return 0 if no match is found (i.e., "Select Country")
    }

    private int getLanguageSpinnerIndex(Spinner spinner, String languageCode) {
        for (int i = 0; i < spinner.getCount(); i++) {
            String item = spinner.getItemAtPosition(i).toString();
            // Extract the two-letter code from the spinner item, which is in parentheses
            if (item.contains("(") && item.contains(")")) {
                String code = item.substring(item.indexOf("(") + 1, item.indexOf(")"));
                if (code.equalsIgnoreCase(languageCode)) {
                    return i; // Return the matching index
                }
            }
        }
        return 0; // Return 0 if no match is found (i.e., "Select Language")
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {  // Request code for POST_NOTIFICATIONS
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, now call getAlarmPermission
                getAlarmPermission();
                toggleNotificationImage();
            } else {
                // Permission was denied, handle accordingly (e.g., show a message)
                Toast.makeText(requireContext(), "Notification permission is required to proceed", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void getAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

            // Check if exact alarm scheduling is permitted
            if (!alarmManager.canScheduleExactAlarms()) {
                // Inform the user and guide them to the settings page to grant the permission
                Toast.makeText(requireContext(), "Please grant alarm permission", Toast.LENGTH_LONG).show();

                // Open the settings page to request exact alarm permission
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
        // Call your alarm scheduler method here (replace with your actual scheduling method)
        // AlarmScheduler.scheduleRepeatingNotification(requireContext(), 1);  // Example call
    }

    public void callNotification(int hour) {
        Context context = getContext();
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("notification_title", "Quick News");
        intent.putExtra("notification_message", "Notifications are set at " + hour + " hours");

        // Sending a broadcast to the NotificationReceiver
        context.sendBroadcast(intent);
    }

}
