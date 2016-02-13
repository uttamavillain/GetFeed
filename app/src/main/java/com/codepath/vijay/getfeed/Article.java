package com.codepath.vijay.getfeed;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by uttamavillain on 2/9/16.
 */
public class Article {
    private static final String  TAG = Article.class.getName();
    private String articleUrl;

    public String getArticleUrl() {
        return articleUrl;
    }

    public String getHeadLines() {
        return headLines;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    private String headLines;
    private String thumbNail;
    private String pub_date;

    public Article(JSONObject jsonObject) {
        try {
            this.articleUrl = jsonObject.getString("web_url");
            this.headLines = jsonObject.getJSONObject("headline").getString("main");
            this.pub_date = jsonObject.getString("pub_date");
            Log.d(TAG, "publishing_date " + pub_date);
            JSONArray multiMedia = jsonObject.getJSONArray("multimedia");
            if(multiMedia.length() > 0) {
                JSONObject multiMediaJson = multiMedia.getJSONObject(0);
                this.thumbNail = "http://nytimes.com/"+multiMediaJson.getString("url");
            }
        } catch (JSONException e) {
            this.thumbNail = "";
            e.printStackTrace();
        }
    }

    public static ArrayList<Article> fromJsonArray(JSONArray array) {
        ArrayList<Article> result = new ArrayList<Article>();
        for(int i=0; i<array.length(); i++) {
            try {
                result.add(new Article(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
