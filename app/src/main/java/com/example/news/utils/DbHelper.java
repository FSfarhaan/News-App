package com.example.news.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.news.NewsModel;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NewsDB";

    // Liked Videos Table
    public static final String TABLE_LIKED_NEWS = "LikedNews";
    // Saved Videos Table
    public static final String TABLE_SAVED_NEWS = "SavedNews";

    public static final String KEY_ID = "id";
    public static final String KEY_IMG_URL = "imgUrl";
    public static final String KEY_SOURCE = "source";
    public static final String KEY_TITLE = "title";
    public static final String KEY_TIME_AGO = "publishedAt";
    public static final String KEY_URL_TO_WEB = "urlToWeb";
    public static final String KEY_CONTENT = "content";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Liked Videos Table
        db.execSQL("CREATE TABLE " + TABLE_LIKED_NEWS + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_IMG_URL + " TEXT, "
                + KEY_SOURCE + " TEXT, "
                + KEY_TITLE + " TEXT, "
                + KEY_TIME_AGO + " TEXT, "
                + KEY_URL_TO_WEB + " TEXT UNIQUE, "
                + KEY_CONTENT + " TEXT)"
        );

        // Create Saved Videos Table
        db.execSQL("CREATE TABLE " + TABLE_SAVED_NEWS + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_IMG_URL + " TEXT, "
                + KEY_SOURCE + " TEXT, "
                + KEY_TITLE + " TEXT, "
                + KEY_TIME_AGO + " TEXT, "
                + KEY_URL_TO_WEB + " TEXT UNIQUE, "
                + KEY_CONTENT + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIKED_NEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED_NEWS);
        onCreate(db);
    }

    // Insert into Liked Videos
    public void insertLikedNews(String imgUrl, String source, String title, String timeAgo, String urlToWeb, String content) {
        insertNews(TABLE_LIKED_NEWS, imgUrl, source, title, timeAgo, urlToWeb, content);
    }

    // Insert into Saved Videos
    public void insertSavedNews(String imgUrl, String source, String title, String timeAgo, String urlToWeb, String content) {
        insertNews(TABLE_SAVED_NEWS, imgUrl, source, title, timeAgo, urlToWeb, content);
    }

    private void insertNews(String tableName, String imgUrl, String source, String title, String timeAgo, String urlToWeb, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IMG_URL, imgUrl);
        values.put(KEY_SOURCE, source);
        values.put(KEY_TITLE, title);
        values.put(KEY_TIME_AGO, timeAgo);
        values.put(KEY_URL_TO_WEB, urlToWeb);
        values.put(KEY_CONTENT, content);
        db.insert(tableName, null, values);
        db.close();
    }

    // Delete from Liked Videos
    public void deleteLikedNews(String urlToWeb) {
        deleteNews(TABLE_LIKED_NEWS, urlToWeb);
    }

    // Delete from Saved Videos
    public void deleteSavedNews(String urlToWeb) {
        deleteNews(TABLE_SAVED_NEWS, urlToWeb);
    }

    private void deleteNews(String tableName, String urlToWeb) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, KEY_URL_TO_WEB + " = ?", new String[]{urlToWeb});
        db.close();
    }

    // Get all Liked Videos
    public List<NewsModel.Articles> getLikedNews() {
        return getNews(TABLE_LIKED_NEWS);
    }

    // Get all Saved Videos
    public List<NewsModel.Articles> getSavedNews() {
        return getNews(TABLE_SAVED_NEWS);
    }

    private List<NewsModel.Articles> getNews(String tableName) {
        List<NewsModel.Articles> articlesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    NewsModel.Articles article = new NewsModel.Articles();
                    article.setUrlToImage(cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMG_URL)));
                    article.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
                    article.setContent(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTENT)));
                    article.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(KEY_URL_TO_WEB)));
                    article.setPublishedAt(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIME_AGO)));

                    NewsModel.Articles.Source source = new NewsModel.Articles.Source();
                    source.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SOURCE)));
                    article.setSource(source);

                    articlesList.add(article);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return articlesList;
    }
}
