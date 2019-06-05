package com.java.a5.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 新闻实体类
 */
public class NewsItem {
    @DatabaseField(id = true)
    private String news_ID;

    @DatabaseField
    private String newsClassTag;

    @DatabaseField
    private String news_Time;

    //class Imgs {String url;}
    //private Imgs imgs;
    @DatabaseField
    private String news_Pictures;

    @DatabaseField
    private String news_Author;

    //class Source {String url;}
    //private Source source;
    @DatabaseField
    private String news_URL;

    @DatabaseField
    private String news_Title;

    @DatabaseField
    private int pageNo;

    private Bitmap bitmap = null;

    public String getTitle() {
        return news_Title;
    }
    public void setTitle(String news_Title) {
        this.news_Title = news_Title;
    }

    public int getPageNumber() {
        return pageNo;
    }
    public void setPageNumber(int pageNo) {
        this.pageNo = pageNo;
    }

    //public void obtainSourceUrl() {news_URL = source.url;}
    public String getSourceUrl() {return news_URL;}
    public void setSourceUrl(String news_URL) {
        this.news_URL = news_URL;
    }

    //public void obtainImgsUrl() {news_Pictures = imgs.url;}
    public String getImgsUrl() {return news_Pictures;}
    public void setImgsUrl(String news_Pictures) {this.news_Pictures = news_Pictures;}

    public String getOrigin() { return news_Author; }

    public String getNews_id() {return news_ID;}
    public void setID(String news_ID) {
        this.news_ID = news_ID;
    }

    public String getType() {return newsClassTag;}
    public void setType(String type) { newsClassTag = type; }

    public String getNews_time() { return news_Time;}

//
//    @Override
//    public String toString() {
//        return "NewsItem{" +
//                    "newsClassTag=" + newsClassTag +
//                    ", news_Pictures=" + imgs.url +
//                    ", news_ID=" + news_ID +
//                    ", news_Author=" + news_Author +
//                    ", news_URL=" + source.url +
//                    ", news_Title=" + news_Title +
//                "}";
//    }
    public void setBitmap() {
        if (bitmap == null)  {
            bitmap = generateBitMap(getImgsUrl());
        }
    }
    public Bitmap generateBitMap(String urlStr) {
        Bitmap bitmap = null;
        InputStream is = null;
        Log.i("NewsItem", "getBitMapUrl: " + urlStr);
        try {
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            is = conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }
    public Bitmap getBitmap() { return bitmap;}

}
