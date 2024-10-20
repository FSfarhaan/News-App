package com.example.news.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.news.NewsModel;
import com.example.news.R;
import com.example.news.utils.DbHelper;
import com.example.news.utils.NewsDetailBottomSheet;

import java.util.ArrayList;
import java.time.Duration;
import java.time.Instant;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    ArrayList<NewsModel.Articles> arrayList;
    Context context;
    String fragment;
    DbHelper db;
    OverlayVisibilityListener listener;

    public NewsAdapter(ArrayList<NewsModel.Articles> arrayList, Context context, String fragment, OverlayVisibilityListener listener) {
        this.arrayList = arrayList;
        this.context = context;
        this.fragment = fragment;
        this.listener = listener;
        db = new DbHelper(context);
    }

    public interface OverlayVisibilityListener {
        void showOverlay();
        void hideOverlay();
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

        holder.itemView.setOnClickListener(v -> {
            NewsDetailBottomSheet bottomSheet = new NewsDetailBottomSheet();
            if (articles.getUrlToImage() != null) {
                bottomSheet.setNewsData(
                        articles.getUrlToImage(),
                        articles.getSource().getName(),
                        articles.getTitle(),
                        timeDifference(articles.getPublishedAt()),
                        articles.getUrl(),
                        articles.getContent()
                );
            } else {
                bottomSheet.setNewsData(
                        "https://cdn.pixabay.com/photo/2015/02/15/09/33/news-636978_1280.jpg",
                        articles.getSource().getName(),
                        articles.getTitle(),
                        timeDifference(articles.getPublishedAt()),
                        articles.getUrl(),
                        articles.getContent()
                );
            }
            bottomSheet.setOverlayVisibilityListener(listener);

            bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), "newsDetailBottomSheet");
            listener.showOverlay();
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(fragment.equals("watchLater")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                            .setTitle("Remove from watch later")
                            .setMessage("Are you sure about this?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialogInterface, i) -> {
                                db.deleteSavedNews(articles.getUrl());
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
