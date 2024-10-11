package com.example.news.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news.NewsApi;
import com.example.news.NewsModel;
import com.example.news.R;
import com.example.news.adapters.NewsAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchFragment extends Fragment {
    RecyclerView watchLaterRV;
    NewsAdapter newsAdapter;
    ArrayList<NewsModel.Articles> newsArrayList = new ArrayList<>();
    EditText searchNews;
    TextView showResults;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchNews = view.findViewById(R.id.searchNews);
        watchLaterRV = view.findViewById(R.id.watchLaterRV);
        showResults = view.findViewById(R.id.showResults);
        newsAdapter = new NewsAdapter(newsArrayList, getContext());
        watchLaterRV.setLayoutManager(new LinearLayoutManager(getContext()));
        watchLaterRV.setAdapter(newsAdapter);

        searchNews.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // Check if the action ID is the Enter key action
                if (actionId == EditorInfo.IME_ACTION_DONE || event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    searchNewsByWords();
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    private void searchNewsByWords() {
        String keywords = searchNews.getText().toString();
        String API_KEY = "178913a53262a17bb0e2f7172d7b3f87";
        String BASE_URL = "https://gnews.io/api/v4/";
        String country = "in";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        newsArrayList.clear();
        NewsApi newsApi = retrofit.create(NewsApi.class);
        Call<NewsModel> call = newsApi.getNewsByKeywords(API_KEY, keywords, country);

        call.enqueue(new Callback<NewsModel>() {
            @Override
            public void onResponse(Call<NewsModel> call, Response<NewsModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NewsModel.Articles> allArticles = response.body().getArticles();

                    if (!allArticles.isEmpty()) {
                        newsArrayList.addAll(allArticles);
                        newsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "No articles found for the selected category.", Toast.LENGTH_SHORT).show();
                    }

                    showResults.setVisibility(View.VISIBLE);
                    showResults.setText(" Showing " + newsArrayList.size() + " articles related to:\n \"" + keywords + "\" ");
                } else {
                    Toast.makeText(getContext(), "Sorry, can't fetch news.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NewsModel> call, Throwable t) {

            }
        });
    }
}
