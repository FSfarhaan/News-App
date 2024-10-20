package com.example.news.fragments;

import static android.content.Context.POWER_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.news.MainActivity;
import com.example.news.R;

public class PersonalFragment extends Fragment {
    private static final String CHANNEL_ID = "My Channel";
    private static final int NOTIFICATION_ID = 100;
    private static final int REQ_CODE = 100;

    Button btn;
    NotificationManager nm;
    NotificationCompat.Builder builder;  // Declare builder at the class level

    public PersonalFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btn = view.findViewById(R.id.btn);

        String packageName = requireActivity().getPackageName();
        PowerManager pm = (PowerManager) requireContext().getSystemService(POWER_SERVICE);
        if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName)) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.home_icon, null);
        Bitmap largeIcon = null;
        if (drawable instanceof BitmapDrawable) {
            largeIcon = ((BitmapDrawable) drawable).getBitmap();
        }

        Intent iNotify = new Intent(requireContext(), MainActivity.class);
        iNotify.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(requireContext(), REQ_CODE, iNotify, PendingIntent.FLAG_IMMUTABLE);

        // Big Picture Style
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                .bigPicture(((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.news_placeholder_img, null)).getBitmap())
                .bigLargeIcon(largeIcon)
                .setBigContentTitle("This notification is sent by Farhaan Shaikh")
                .setSummaryText("You lied to yourself");

        nm = (NotificationManager) requireContext().getSystemService(NotificationManager.class);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            nm.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "New Channel", NotificationManager.IMPORTANCE_HIGH));
        }

        // Initialize the builder as a class-level variable
        builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.home_icon)
                .setContentText("New Message")
                .setSubText("Chhota message")
                .setContentIntent(pi)
                .setAutoCancel(false)
                .setOngoing(true)
                .setStyle(bigPictureStyle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            } else {
                // Set button click listener
                btn.setOnClickListener(v -> nm.notify(NOTIFICATION_ID, builder.build()));
            }
        } else {
            // Set button click listener
            btn.setOnClickListener(v -> nm.notify(NOTIFICATION_ID, builder.build()));
        }

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Now that builder is a class-level variable, you can access it here
            btn.setOnClickListener(v -> nm.notify(NOTIFICATION_ID, builder.build()));
        } else {
            // Permission denied, handle accordingly
        }
    }
}

