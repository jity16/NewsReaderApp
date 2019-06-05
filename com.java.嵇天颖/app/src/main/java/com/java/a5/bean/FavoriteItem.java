package com.java.a5.bean;

import com.j256.ormlite.field.DatabaseField;

public class FavoriteItem {
    @DatabaseField(id = true)
    private String news_ID;
    @DatabaseField
    private String news_URL;
    @DatabaseField
    private String news_Title;
    @DatabaseField
    private String news_Author;

    public String getNewsId() {
        return news_ID;
    }

    public String getSourceUrl() {
        return news_URL;
    }

    public String getTitle() {
        return news_Title;
    }

    public String getOrigin() {
        return news_Author;
    }

    public void setNewsId(String newsId) {
        this.news_ID = newsId;
    }
    public void setSourceUrl(String sourceUrl) {
        this.news_URL = sourceUrl;
    }
    public void setTitle(String title) {
        this.news_Title = title;
    }
    public void setOrigin(String origin) {
        this.news_Author = origin;
    }
}
