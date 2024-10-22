package com.example.news.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news.MainActivity;
import com.example.news.NewsApi;
import com.example.news.NewsModel;
import com.example.news.R;
import com.example.news.adapters.NewsAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchFragment extends Fragment implements NewsAdapter.OverlayVisibilityListener {
    RecyclerView watchLaterRV;
    NewsAdapter newsAdapter;
    ArrayList<NewsModel.Articles> newsArrayList = new ArrayList<>();
    EditText searchNews;
    TextView showResults;
    View dimOverlay;
    ShimmerFrameLayout shimmerSearch;

    private LinearLayout filterLayout, searchNewsLL, noNewsLL;
    private Spinner languageSpinner, countrySpinner, numberSpinner;
    private Button fromDateButton, toDateButton;
    private String selectedFromDate, selectedToDate;

    // Variables to store selected values
    private String selectedLanguage = null;
    private String selectedCountry = null;
    private String selectedNumber = "20";

    public SearchFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchNews = view.findViewById(R.id.searchNews);
        watchLaterRV = view.findViewById(R.id.watchLaterRV);
        showResults = view.findViewById(R.id.showResults);
        dimOverlay = view.findViewById(R.id.dimOverlay);

        filterLayout = view.findViewById(R.id.filterLayout);
        languageSpinner = view.findViewById(R.id.languageSpinner);
        countrySpinner = view.findViewById(R.id.countrySpinner);
        numberSpinner = view.findViewById(R.id.numberSpinner);
        shimmerSearch = view.findViewById(R.id.shimmerSearch);
        searchNewsLL = view.findViewById(R.id.searchNewsLL);
        noNewsLL = view.findViewById(R.id.noNewsLL);

        newsAdapter = new NewsAdapter(newsArrayList, getContext(), "Search", this);
        watchLaterRV.setLayoutManager(new LinearLayoutManager(getContext()));
        watchLaterRV.setAdapter(newsAdapter);

        if (newsArrayList.isEmpty()) {
            searchNewsLL.setVisibility(View.VISIBLE);
        } else {
            searchNewsLL.setVisibility(View.GONE);
        }

        searchNews.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    if(!searchNews.getText().toString().trim().isEmpty()) searchNewsByWords();
                    return true;
                }
                return false;
            }
        });

        searchNews.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (searchNews.getCompoundDrawables()[2] != null) {
                        if (event.getRawX() >= (searchNews.getRight() - searchNews.getCompoundDrawables()[2].getBounds().width())) {
                            if ((filterLayout.getVisibility() == View.GONE)) {
                                filterLayout.setVisibility(View.VISIBLE);
                            } else {
                                filterLayout.setVisibility(View.GONE);
                            }
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.contains("(") && selectedItem.contains(")")) {
                    selectedLanguage = selectedItem.substring(selectedItem.indexOf('(') + 1, selectedItem.indexOf(')'));
                } else {
                    selectedLanguage = null; // Keep it null if default
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedLanguage = null; // Set to null if nothing selected
            }
        });

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.contains("(") && selectedItem.contains(")")) {
                    selectedCountry = selectedItem.substring(selectedItem.indexOf('(') + 1, selectedItem.indexOf(')'));
                } else {
                    selectedCountry = null; // Keep it null if default
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCountry = null; // Set to null if nothing selected
            }
        });

        numberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedNumber = parent.getItemAtPosition(position).toString();
                if (selectedNumber.equals("Max Articles")) {
                    selectedNumber = null; // Set to null if default
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }

    private void searchNewsByWords() {
        shimmerSearch.setVisibility(View.VISIBLE);
        shimmerSearch.startShimmer();
        searchNewsLL.setVisibility(View.GONE);

        String keywords = searchNews.getText().toString();
        String API_KEY = "5ee9f06f4f41ff9da51c2dd0e62d8077";
        String BASE_URL = "https://gnews.io/api/v4/";

        // Default values if not provided
        String language = selectedLanguage != null ? selectedLanguage : null;
        String country = selectedCountry != null ? selectedCountry : null;
        int number = selectedNumber != null ? Integer.parseInt(selectedNumber) : 20;

        Log.d("SearchFragment", "Params: keywords=" + keywords + ", language=" + language + ", country=" + country + ", max=" + number);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        newsArrayList.clear();
        NewsApi newsApi = retrofit.create(NewsApi.class);

        // Call the API without source parameter
        Call<NewsModel> call = newsApi.getNewsByKeywords(API_KEY, keywords, language, country, number);
        Log.d("Bata na yaar", "Params: language: " + language + " country: " + country + " number: " + number + " keywords: " + keywords);

        call.enqueue(new Callback<NewsModel>() {
            @Override
            public void onResponse(Call<NewsModel> call, Response<NewsModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NewsModel.Articles> allArticles = response.body().getArticles();
                    if (!allArticles.isEmpty()) {
                        newsArrayList.clear();
                        newsArrayList.addAll(allArticles);
                        noNewsLL.setVisibility(View.GONE);
                        newsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "No articles found for the selected query.", Toast.LENGTH_SHORT).show();
                        noNewsLL.setVisibility(View.VISIBLE);
                    }
                    showResults.setVisibility(View.VISIBLE);
                    showResults.setText(" Showing " + newsArrayList.size() + " articles related to:\n \"" + keywords + "\" ");
                } else {
                    Toast.makeText(getContext(), "Sorry, can't fetch news.", Toast.LENGTH_SHORT).show();
                    noNewsLL.setVisibility(View.VISIBLE);
                }
                shimmerSearch.stopShimmer();
                shimmerSearch.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<NewsModel> call, Throwable t) {
                noNewsLL.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog(final Button dateButton, boolean isFromDate) {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                dateButton.setText(selectedDate);
                if (isFromDate) {
                    selectedFromDate = selectedDate;
                } else {
                    selectedToDate = selectedDate;
                }
            }
        }, year, month, day);

        datePickerDialog.show();
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
