package com.java.a5.dao;

import android.content.Context;

import com.java.a5.bean.NewsType;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.List;

/**
 * 对新闻列表的数据库操作
 */
public class NewsTypeDao {

    private RuntimeExceptionDao<NewsType,Integer> mNewsTypeDao;
    private DataBaseHelper mDataBaseHelpter;

    public NewsTypeDao(Context context) {
        mDataBaseHelpter = DataBaseHelper.getHelper(context);
        this.mNewsTypeDao = mDataBaseHelpter.getNewsTypeRuntimeDao();
    }

    /**
     * 更新或添加
     * @param newsType 需要更新的新闻列表项
     */
    public void createOrUpdate(NewsType newsType){
        mNewsTypeDao.createOrUpdate(newsType);
    }

    //网络有问题的情况下获取数据库中的缓存
    public List<NewsType> getCache() throws SQLException {
        List<NewsType> newsTypes = mNewsTypeDao.queryBuilder().where().ge("showOrder",0).query();
        if (newsTypes.size() > 0){
            return newsTypes;
        }
        return null;
    }


    public List<NewsType> queryAll(){
        List<NewsType> news = mNewsTypeDao.queryForAll();
        return news;
    }

    public NewsType searchByUrlType(String urlType) throws SQLException {
        List<NewsType> newsTypes = mNewsTypeDao.queryBuilder().where().eq("urlType",urlType).query();
        if (newsTypes.size() > 0){
            return newsTypes.get(0);
        }
        return null;
    }

    public NewsType searchByShowType(String showType) throws SQLException {
        List<NewsType> newsTypes = mNewsTypeDao.queryBuilder().where().eq("showType",showType).query();
        if (newsTypes.size() > 0){
            return newsTypes.get(0);
        }
        return null;
    }

    public void setAllIsExist(boolean isExist) {
        List<NewsType> newsTypes = queryAll();
        for(NewsType newsType :newsTypes) {
            newsType.setExist(isExist);
            createOrUpdate(newsType);
        }
    }

}
