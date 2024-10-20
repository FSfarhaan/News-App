package com.example.news.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news.MainActivity;
import com.example.news.NewsModel;
import com.example.news.R;
import com.example.news.adapters.NewsAdapter;
import com.example.news.utils.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class WatchLaterFragment extends Fragment implements NewsAdapter.OverlayVisibilityListener {
    RecyclerView watchLaterRV;
    NewsAdapter newsAdapter;
    ArrayList<NewsModel.Articles> newsArrayList = new ArrayList<>();
    DbHelper db;
    View dimOverlay;

    public WatchLaterFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subscription, container, false);
        dimOverlay = view.findViewById(R.id.dimOverlay);

        // Initialize DbHelper here after the context is available
        db = new DbHelper(getContext());

        watchLaterRV = view.findViewById(R.id.watchLater);
        newsAdapter = new NewsAdapter(newsArrayList, getContext(), "watchLater", this);
        watchLaterRV.setLayoutManager(new LinearLayoutManager(getContext()));
        watchLaterRV.setAdapter(newsAdapter);

        getSavedNews();

        return view;
    }

    public void getSavedNews() {
        List<NewsModel.Articles> savedNews = db.getSavedNews();
        newsArrayList.clear();
        newsArrayList.addAll(savedNews);
        newsAdapter.notifyDataSetChanged();  // Notify the adapter after it's initialized
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

