package com.example.news.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.news.MainActivity;
import com.example.news.NewsDetailActivity;
import com.example.news.NewsModel;
import com.example.news.R;
import com.example.news.fragments.FavouritesFragment;
import com.example.news.fragments.PersonalFragment;
import com.example.news.utils.DbHelper;

import java.util.ArrayList;
import java.time.Duration;
import java.time.Instant;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    ArrayList<NewsModel.Articles> arrayList;
    Context context;
    String fragment;
    DbHelper db;

    public NewsAdapter(ArrayList<NewsModel.Articles> arrayList, Context context, String fragment) {
        this.arrayList = arrayList;
        this.context = context;
        this.fragment = fragment;
        db = new DbHelper(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsModel.Articles articles = arrayList.get(position);
        // holder.newsImg.setImageResource(R.drawable.tp);
        holder.newsTitle.setText(articles.getTitle());
        holder.newsSource.setText(articles.getSource().getName());
        holder.newsTimeAgo.setText(timeDifference(articles.getPublishedAt()));

        Glide.with(holder.itemView.getContext()).load(articles.getUrlToImage()).placeholder(R.drawable.news_placeholder_img).into(holder.newsImg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsDetailActivity.class);
                if(articles.getUrlToImage() != null) intent.putExtra("newsImgUrl", articles.getUrlToImage());
                else intent.putExtra("newsImgUrl", "https://cdn.pixabay.com/photo/2015/02/15/09/33/news-636978_1280.jpg");
                intent.putExtra("newsSource", articles.getSource().getName());
                intent.putExtra("newsTitle", articles.getTitle());
                intent.putExtra("newsTimeAgo", timeDifference(articles.getPublishedAt()));
                intent.putExtra("newsUrlToWeb", articles.getUrl());
                intent.putExtra("newsContent", articles.getContent());

                context.startActivity(intent);
                if(context instanceof MainActivity) {
                    ((MainActivity) context).finish();
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(fragment.equals("Fav")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                            .setTitle("Remove from favourites")
                            .setMessage("Are you sure about this?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialogInterface, i) -> {
                                db.deleteLikedNews(articles.getUrl());
                                arrayList.remove(arrayList.get(position));
                                notifyItemRemoved(holder.getAdapterPosition());
                                notifyItemRangeChanged(position, arrayList.size());
                            })
                            .setNegativeButton("No", (dialogInterface, i) -> {

                            });
                    dialog.show();
                }
                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static String timeDifference(String dateTimeString) {
        if(dateTimeString.contains("hour") || dateTimeString.contains("min")) return dateTimeString;
        // Parse the input date-time string to an Instant
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Instant inputTime = Instant.parse(dateTimeString);
            Instant currentTime = Instant.now();
            Duration duration = Duration.between(inputTime, currentTime);

            // Calculate the total minutes and hours passed
            long minutesPassed = duration.toMinutes();
            long hoursPassed = duration.toHours();

            // Determine the output string based on the duration
            if (minutesPassed < 60) {
                return minutesPassed + " mins ago";
            } else {
                return hoursPassed + " hours ago";
            }
        }
        else return dateTimeString;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView newsImg;
        TextView newsTitle, newsSource, newsTimeAgo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            newsImg = itemView.findViewById(R.id.newsImg);
            newsTitle = itemView.findViewById(R.id.newsTitle);
            newsSource = itemView.findViewById(R.id.newsSource);
            newsTimeAgo = itemView.findViewById(R.id.newsTimeAgo);
        }
    }
}
