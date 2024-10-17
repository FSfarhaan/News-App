package com.example.news.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news.NewsModel;
import com.example.news.R;
import com.example.news.adapters.NewsAdapter;
import com.example.news.utils.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment {
    RecyclerView watchLaterRV;
    NewsAdapter newsAdapter;
    ArrayList<NewsModel.Articles> newsArrayList = new ArrayList<>();
    DbHelper db;

    public FavouritesFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subscription, container, false);

        // Initialize DbHelper here after the context is available
        db = new DbHelper(getContext());

        watchLaterRV = view.findViewById(R.id.watchLater);
        newsAdapter = new NewsAdapter(newsArrayList, getContext(), "Fav");
        watchLaterRV.setLayoutManager(new LinearLayoutManager(getContext()));
        watchLaterRV.setAdapter(newsAdapter);

        getLikedNews();

        return view;
    }

    public void getLikedNews() {
        List<NewsModel.Articles> likedNews = db.getLikedNews();
        newsArrayList.addAll(likedNews);
        newsAdapter.notifyDataSetChanged();  // Notify the adapter after it's initialized
    }
}

