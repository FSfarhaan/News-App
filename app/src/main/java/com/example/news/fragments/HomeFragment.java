package com.example.news.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.news.MainActivity;
import com.example.news.NewsApi;
import com.example.news.NewsDetailActivity;
import com.example.news.NewsModel;
import com.example.news.R;
import com.example.news.adapters.NavbarAdapter;
import com.example.news.adapters.NewsAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment implements NavbarAdapter.OnCategoryClickListener {
    RecyclerView navRV, newsRV;
    NavbarAdapter navAdapter;
    NewsAdapter newsAdapter;
    ArrayList<String> navArrayList = new ArrayList<>();
    ArrayList<NewsModel.Articles> newsArrayList = new ArrayList<>();

    ImageView imgOfNews1;
    CardView imgNews1;
    TextView titleOfNews1, nameOfNews1, timeAgoOfNews1, newsStatus;

    ShimmerFrameLayout shimmerFrameLayout, shimmerNews1;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //The Cardview
        imgNews1 = view.findViewById(R.id.imgNews1);

        imgOfNews1 = view.findViewById(R.id.imgOfNews1);
        titleOfNews1 = view.findViewById(R.id.titleOfNews1);
        nameOfNews1 = view.findViewById(R.id.nameOfNews1);
        timeAgoOfNews1 = view.findViewById(R.id.timeAgoOfNews1);
        newsStatus = view.findViewById(R.id.newsStatus);

        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout);
        shimmerNews1 = view.findViewById(R.id.shimmerNews1);

        navArrayList.clear();
        Collections.addAll(navArrayList, "General", "Entertainment", "Business", "Sports", "Health", "Technology");
        navRV = view.findViewById(R.id.navRV);
        navAdapter = new NavbarAdapter(navArrayList, getContext());
        navRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        navRV.setAdapter(navAdapter);
        navAdapter.setOnCategoryClickListener(this);

        shimmerFrameLayout.startShimmer();
        shimmerNews1.startShimmer();
        getNews("general");

        newsRV = view.findViewById(R.id.newsRV);
        newsAdapter = new NewsAdapter(newsArrayList, getContext(), "Home");
        newsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        newsRV.setAdapter(newsAdapter);

        return view;
    }

    public void getNews(String category) {
        // String sources = "the-times-of-india,hindustan-times,india-today,the-hindu,ndtv-news,the-indian-express";
        String API_KEY = "YOUR_API_KEY";
        String BASE_URL = "https://gnews.io/api/v4/";
        String country = "in";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        newsArrayList.clear();
        NewsApi newsApi = retrofit.create(NewsApi.class);
        Call<NewsModel> call = newsApi.getNewsByCategory(API_KEY, category, country);

        Log.d("HomeFragment", "Request URL: " + call.request().url().toString());

        call.enqueue(new Callback<NewsModel>() {
            @Override
            public void onResponse(Call<NewsModel> call, Response<NewsModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NewsModel.Articles> allArticles = response.body().getArticles();

                    if (!allArticles.isEmpty()) {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        shimmerNews1.stopShimmer();
                        shimmerNews1.setVisibility(View.GONE);
                        newsRV.setVisibility(View.VISIBLE);
                        imgNews1.setVisibility(View.VISIBLE);
                        NewsModel.Articles firstArticle = allArticles.get(0);
                        updateUIWithFirstArticle(firstArticle);

                        newsArrayList.addAll(allArticles.subList(1, allArticles.size()));
                        newsAdapter.notifyDataSetChanged();
                    } else {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        shimmerNews1.stopShimmer();
                        shimmerNews1.setVisibility(View.GONE);
                        newsRV.setVisibility(View.VISIBLE);
                        imgNews1.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "No articles found for the selected category.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    shimmerNews1.stopShimmer();
                    shimmerNews1.setVisibility(View.GONE);
                    newsRV.setVisibility(View.VISIBLE);
                    imgNews1.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Sorry, can't fetch news.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsModel> call, Throwable t) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                shimmerNews1.stopShimmer();
                shimmerNews1.setVisibility(View.GONE);
                newsRV.setVisibility(View.VISIBLE);
                imgNews1.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIWithFirstArticle(NewsModel.Articles article) {
        String title = article.getTitle();
        String name = article.getSource().getName();
        String content = article.getContent();
        String time = timeDifference(article.getPublishedAt());
        String urlImage = article.getUrlToImage();
        String urlToWeb = article.getUrl();

        titleOfNews1.setText(title);
        nameOfNews1.setText(name);
        timeAgoOfNews1.setText(time);
        Glide.with(getContext()).load(urlImage).placeholder(R.drawable.news_placeholder_img).into(imgOfNews1);

        imgOfNews1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewsDetailActivity.class);
                intent.putExtra("newsImgUrl", urlImage != null ? urlImage : "https://cdn.pixabay.com/photo/2015/02/15/09/33/news-636978_1280.jpg");
                intent.putExtra("newsSource", name);
                intent.putExtra("newsTitle", title);
                intent.putExtra("newsTimeAgo", time);
                intent.putExtra("newsUrlToWeb", urlToWeb);
                intent.putExtra("newsContent", content);

                getContext().startActivity(intent);
                if (getContext() instanceof MainActivity) {
                    ((MainActivity) getContext()).finish();
                }
            }
        });
    }

    @Override
    public void onCategoryClicked(String category) {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        shimmerNews1.setVisibility(View.VISIBLE);
        shimmerNews1.startShimmer();
        imgNews1.setVisibility(View.GONE);
        newsRV.setVisibility(View.GONE);
        getNews(category);
    }

    public static String timeDifference(String dateTimeString) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            Instant inputTime = Instant.parse(dateTimeString);
            Instant currentTime = Instant.now();
            Duration duration = Duration.between(inputTime, currentTime);

            long minutesPassed = duration.toMinutes();
            long hoursPassed = duration.toHours();

            if (minutesPassed < 60) {
                return minutesPassed + " mins ago";
            } else {
                return hoursPassed + " hours ago";
            }
        } else {
            return dateTimeString;
        }
    }
}
