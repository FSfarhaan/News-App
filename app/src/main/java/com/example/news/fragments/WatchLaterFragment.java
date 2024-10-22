package com.example.news.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news.MainActivity;
import com.example.news.NewsModel;
import com.example.news.R;
import com.example.news.adapters.NewsAdapter;
import com.example.news.data.DbHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WatchLaterFragment extends Fragment implements NewsAdapter.OverlayVisibilityListener {
    RecyclerView watchLaterRV;
    NewsAdapter newsAdapter;
    ArrayList<NewsModel.Articles> newsArrayList = new ArrayList<>();
    DbHelper db;
    View dimOverlay;
    LinearLayout noWatchLaterNews;
    Spinner sortBySpinner;
    String orderOfNews = "Oldest First";

    public WatchLaterFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_watch_later, container, false);
        dimOverlay = view.findViewById(R.id.dimOverlay);
        noWatchLaterNews = view.findViewById(R.id.noWatchLaterNews);
        sortBySpinner = view.findViewById(R.id.sortBySpinner);

        // Initialize DbHelper here after the context is available
        db = new DbHelper(getContext());

        watchLaterRV = view.findViewById(R.id.watchLater);
        newsAdapter = new NewsAdapter(newsArrayList, getContext(), "watchLater", this);
        watchLaterRV.setLayoutManager(new LinearLayoutManager(getContext()));
        watchLaterRV.setAdapter(newsAdapter);

        // Set up the spinner listener
        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Apply sorting whenever the spinner option changes
                applySorting();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });

        // Load saved news
        getSavedNews();

        return view;
    }

    // Method to get saved news from DB
    public void getSavedNews() {
        List<NewsModel.Articles> savedNews = db.getSavedNews();
        if (savedNews.isEmpty()) {
            noWatchLaterNews.setVisibility(View.VISIBLE);
        } else {
            noWatchLaterNews.setVisibility(View.GONE);
        }

        newsArrayList.clear();
        newsArrayList.addAll(savedNews);

        // Apply sorting based on current spinner selection
        applySorting();

        newsAdapter.notifyDataSetChanged();  // Notify the adapter after it's initialized
    }

    // Apply sorting based on spinner selection
    private void applySorting() {
        String selectedOption = sortBySpinner.getSelectedItem().toString();
        if (!selectedOption.equals(orderOfNews)) {
            // Reverse the list for newest first
            Collections.reverse(newsArrayList);
            orderOfNews = selectedOption;
        }
        // If "Oldest First" is selected, the list remains as it is
        newsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showOverlay() {
        dimOverlay.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).setStatusBarColor(Color.argb(128, 0, 0, 0));
    }

    @Override
    public void hideOverlay() {
        dimOverlay.setVisibility(View.GONE);
        ((MainActivity) getActivity()).setStatusBarColor(Color.argb(255, 255, 255, 255));
    }
}
