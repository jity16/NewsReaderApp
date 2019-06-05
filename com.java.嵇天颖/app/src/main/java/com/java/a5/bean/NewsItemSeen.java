package com.java.a5.bean;

/**
 * Created by Starry Sky on 2017/9/9.
 */

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "newsitemseen")
public class NewsItemSeen {
    @DatabaseField(id = true)
    private String news_ID;
    @DatabaseField
    private String news_URL;
    @DatabaseField
    private String news_Title;
    @DatabaseField
    private String news_Author;
    @DatabaseField
    private String newsClassTag;



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

    public String getNewsClassTag() {
        return newsClassTag;
    }

    public void setNewsId(String newsId) {
        this.news_ID = newsId;
    }
    public void setNewsClassTag(String type) { this.newsClassTag = type; }
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
