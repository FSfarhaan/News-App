package com.example.news.utils;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.news.R;
import com.example.news.adapters.NewsAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class NewsDetailBottomSheet extends BottomSheetDialogFragment {

    private String imgUrl;
    private String newsSource;
    private String newsTitle;
    private String newsTimeAgo;
    private String newsContent;
    private String urlToWeb;

    TextView newsTitleTextView;
    TextView newsSourceTextView;
    TextView newsTimeAgoTextView;
    TextView newsContentTextView;
    ImageView goBackFromDetails, imgOfNews1;
    Button watchLater, learnMore;
    ImageView shareNews;
    DbHelper db;
    CardView shareNewsCV;

    private NewsAdapter.OverlayVisibilityListener overlayVisibilityListener;

    // Method to set the news data
    public void setNewsData(String imgUrl, String source, String title, String timeAgo, String urlToWeb, String content) {
        this.imgUrl = imgUrl;
        this.newsSource = source;
        this.newsTitle = title;
        this.newsTimeAgo = timeAgo;
        this.urlToWeb = urlToWeb;
        this.newsContent = content;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setDimAmount(0.9f);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // Set fixed height for the Bottom Sheet
            View view = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (view != null) {
                // Set a fixed height for the bottom sheet
                view.getLayoutParams().height = 1800; // Set your desired fixed height
                view.requestLayout();
            }
            // Toast.makeText(getContext(), "Bottom sheet displayed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Error displaying bottom sheet", Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        // Initialize views
        newsTitleTextView = view.findViewById(R.id.newsTitle1);
        newsSourceTextView = view.findViewById(R.id.newsSource1);
        newsTimeAgoTextView = view.findViewById(R.id.newsTimeAgo1);
        newsContentTextView = view.findViewById(R.id.newsContent1);
        goBackFromDetails = view.findViewById(R.id.goBackFromDetails);
        imgOfNews1 = view.findViewById(R.id.imgOfNews1);
        watchLater = view.findViewById(R.id.watchLater);
        shareNews = view.findViewById(R.id.shareNews);
        shareNewsCV = view.findViewById(R.id.shareNewsCV);
        learnMore = view.findViewById(R.id.learnMore);

        db = new DbHelper(getContext());

        // Set news data to the views
        newsTitleTextView.setText(newsTitle);
        newsSourceTextView.setText(newsSource);
        newsTimeAgoTextView.setText(newsTimeAgo);
        newsContentTextView.setText(newsContent);
        Glide.with(this).load(imgUrl).into(imgOfNews1);

        // Set up the go back button click listener
        goBackFromDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        watchLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.insertSavedNews(imgUrl, newsSource, newsTitle, newsTimeAgo, urlToWeb, newsContent);
                Toast.makeText(getContext(), "Added to Watch later", Toast.LENGTH_SHORT).show();
            }
        });

        shareNewsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to share the URL
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain"); // Set the type as plain text

                // Add the URL as extra text to the intent
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this news: \n" + urlToWeb);

                // Start the activity to choose where to share
                Intent chooser = Intent.createChooser(shareIntent, "Share news via...");
                v.getContext().startActivity(chooser);
            }
        });

        learnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri webpage = Uri.parse(urlToWeb);
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (overlayVisibilityListener != null) {
            overlayVisibilityListener.hideOverlay(); // Notify listener to hide overlay
        }
    }

    // Method to set the listener
    public void setOverlayVisibilityListener(NewsAdapter.OverlayVisibilityListener listener) {
        this.overlayVisibilityListener = listener;
    }
}
