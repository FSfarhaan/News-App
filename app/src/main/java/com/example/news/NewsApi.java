package com.example.news;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApi {
    @GET("top-headlines")
    Call<NewsModel> getNewsByCategory(
            @Query("apikey") String apiKey,
            @Query("topic") String category,
            @Query("country") String country
    );

    @GET("top-headlines")
    Call<NewsModel> getNewsByKeywords(
            @Query("apikey") String apiKey,
            @Query("q") String category,
            @Query("lang") String language,
            @Query("country") String country,
            @Query("max") int max
    );
}
