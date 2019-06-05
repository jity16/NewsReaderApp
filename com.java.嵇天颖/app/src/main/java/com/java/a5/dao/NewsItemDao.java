package com.java.a5.dao;

import android.content.Context;

import com.java.a5.bean.NewsItem;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.List;

/**
 * 对新闻列表的数据库操作
 */
public class NewsItemDao {

    private RuntimeExceptionDao<NewsItem,Integer> mNewsItemDao;
    private DataBaseHelper mDataBaseHelpter;

    public NewsItemDao(Context context) {
        mDataBaseHelpter = DataBaseHelper.getHelper(context);
        this.mNewsItemDao = mDataBaseHelpter.getNewsItemRuntimeDao();
    }

    /**
     * 更新或添加
     * @param newsItem 需要更新的新闻列表项
     */
    public void createOrUpdate(NewsItem newsItem){
        mNewsItemDao.createOrUpdate(newsItem);
    }

    public List<NewsItem> getCache(String newsType)  throws SQLException {

        List<NewsItem> newsItems = mNewsItemDao.queryBuilder().where().eq("newsClassTag",newsType).query();
        if (newsItems.size() > 0) {
            return newsItems;
        }
        return null;
    }
    public boolean searchIsExistByNewsId(String newsId) throws SQLException {
        List<NewsItem> newsItems = mNewsItemDao.queryBuilder().where().eq("news_ID",newsId).query();
        return (newsItems.size() > 0);
    }

    public List<NewsItem> searchByKey(String key) throws SQLException {
        List<NewsItem> newsItems = mNewsItemDao.queryBuilder().where().like("news_Title","%"+key+"%").query();
        return newsItems;
    }

    public void deleteAll(){
        mNewsItemDao.delete(queryAll());
    }


    public List<NewsItem> queryAll(){
        List<NewsItem> news = mNewsItemDao.queryForAll();
        return news;
    }
}
