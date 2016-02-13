package com.codepath.vijay.getfeed.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.GridView;

import com.codepath.vijay.getfeed.Article;
import com.codepath.vijay.getfeed.ArticleArrayAdapter;
import com.codepath.vijay.getfeed.FilterDialog;
import com.codepath.vijay.getfeed.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private static final String TAG = SearchActivity.class.getName();

    private GridView gvSearch;
    private FilterDialog filterDialog;
    private int year, month, date;
    private boolean oldestFirst;
    private boolean isArts, isFashionStyle, isSports;
    private String query;
    private SwipeRefreshLayout swipeContainer;

    private List<Article> articles;
    ArticleArrayAdapter articleArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "The onCreate() event");
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUPViews();
    }

    private void setUPViews() {
        gvSearch = (GridView) findViewById(R.id.gvSearch);
        gvSearch.setNestedScrollingEnabled(true);
        articles = new ArrayList<Article>();
        articleArrayAdapter = new ArticleArrayAdapter(this, articles);
        gvSearch.setAdapter(articleArrayAdapter);
        query="";

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                articleArrayAdapter.clear();
                loadArticles(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        gvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ArticleBrowserActivity.class);
                intent.putExtra("url", articles.get(position).getArticleUrl());
                startActivity(intent);
            }
        });
        // Attach the listener to the AdapterView onCreate
        gvSearch.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadArticles(page-1);
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH)+1;
        date = Calendar.getInstance().get(Calendar.DATE);
        oldestFirst = true;
        isArts = isFashionStyle = isSports = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_filter) {
            showFilterDialog();
        } else if(id == R.id.action_search) {
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String q) {
                    articleArrayAdapter.clear();
                    query = q;
                    loadArticles(0);
                    searchView.clearFocus();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

        }

        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        filterDialog = FilterDialog.newInstance(year,month,date,oldestFirst,isArts, isFashionStyle, isSports);
        filterDialog.show(fm, "fragment_edit_name");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int date) {
        Log.i(TAG, "Date selected " + year + " " + (month + 1) + " " + date);
        filterDialog.updateDate(year, (month + 1), date);
    }

    public void refresh(int year, int month, int date, boolean oldestFirst, boolean isArts, boolean isFashionStyle, boolean isSports) {
        this.year = year;
        this.month = month;
        this.date = date;
        this.oldestFirst = oldestFirst;
        this.isArts = isArts;
        this.isFashionStyle = isFashionStyle;
        this.isSports = isSports;
        articleArrayAdapter.clear();
        loadArticles(0);
    }

    public void loadArticles(int page) {
        if(TextUtils.isEmpty(query))
            return;
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", "79e47b0dd044591506b66a8dffa82ca4:18:74340141");
        params.put("page", page);
        params.put("q",query);
        params.put("sort",oldestFirst?"oldest":"newest");
        params.put("begin_date", year + String.format("%02d", month) + String.format("%02d", date));
        String newsDeskVal = "";
        if(isArts||isFashionStyle||isSports) {
            params.put("fq", "news_desk:(\""+(isArts?"Arts":"")+(isFashionStyle?"Fashion  Style":"")+(isSports?"Sports":"")+"\")");
        }
        Log.d(TAG, "loading... http://api.nytimes.com/svc/search/v2/articlesearch.json?" + params.toString());
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResult = null;
                try {
                    articleJsonResult = response.getJSONObject("response").getJSONArray("docs");
                    articleArrayAdapter.addAll(Article.fromJsonArray(articleJsonResult));
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {
        // The minimum amount of items to have below your current scroll position
        // before loading more.
        private int visibleThreshold = 5;
        // The current offset index of data you have loaded
        private int currentPage = 0;
        // The total number of items in the dataset after the last load
        private int previousTotalItemCount = 0;
        // True if we are still waiting for the last set of data to load.
        private boolean loading = true;
        // Sets the starting page index
        private int startingPageIndex = 0;

        public EndlessScrollListener() {
        }

        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        public EndlessScrollListener(int visibleThreshold, int startPage) {
            this.visibleThreshold = visibleThreshold;
            this.startingPageIndex = startPage;
            this.currentPage = startPage;
        }

        // This happens many times a second during a scroll, so be wary of the code you place here.
        // We are given a few useful parameters to help us work out if we need to load some more data,
        // but first we check if we are waiting for the previous load to finish.
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            // If the total item count is zero and the previous isn't, assume the
            // list is invalidated and should be reset back to initial state
            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = this.startingPageIndex;
                this.previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) { this.loading = true; }
            }
            // If it's still loading, we check to see if the dataset count has
            // changed, if so we conclude it has finished loading and update the current page
            // number and total item count.
            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
                currentPage++;
            }

            // If it isn't currently loading, we check to see if we have breached
            // the visibleThreshold and need to reload more data.
            // If we do need to reload some more data, we execute onLoadMore to fetch the data.
            if (!loading && (firstVisibleItem + visibleItemCount + visibleThreshold) >= totalItemCount ) {
                loading = onLoadMore(currentPage + 1, totalItemCount);
            }
        }

        // Defines the process for actually loading more data based on page
        // Returns true if more data is being loaded; returns false if there is no more data to load.
        public abstract boolean onLoadMore(int page, int totalItemsCount);

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // Don't take any action on changed
        }
    }

    /** Called when the activity is about to become visible. */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "The onStart() event");
    }

    /** Called when the activity has become visible. */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "The onResume() event");
    }

    /** Called when another activity is taking focus. */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "The onPause() event");
    }

    /** Called when the activity is no longer visible. */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "The onStop() event");
    }

    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "The onDestroy() event");
    }
}
