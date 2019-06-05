package com.java.a5.dao;

import android.content.Context;

import com.java.a5.bean.FavoriteItem;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by wuhanghang on 16-9-3.
 */

public class FavoriteItemDao {
    private RuntimeExceptionDao<FavoriteItem,Integer> mFavoriteItemDao;
    private DataBaseHelper mDataBaseHelpter;

    public FavoriteItemDao(Context context) {
        mDataBaseHelpter = DataBaseHelper.getHelper(context);
        this.mFavoriteItemDao = mDataBaseHelpter.getFavoriteItemRuntimeDao();
    }

    public void createOrUpdate(FavoriteItem favoriteItem){
        mFavoriteItemDao.createOrUpdate(favoriteItem);
    }

    public List<FavoriteItem> queryAll(){
        List<FavoriteItem> favorites = mFavoriteItemDao.queryForAll();
        return favorites;
    }
    public int deleteByNewsId(String newsId) throws SQLException {
        DeleteBuilder<FavoriteItem, Integer> deleteBuilder = mFavoriteItemDao.deleteBuilder();
        deleteBuilder.where().eq("news_ID",newsId);
        return deleteBuilder.delete();
    }
    public boolean searchIsExistByNewsId(String newsId) throws SQLException {
        List<FavoriteItem> favoriteItems = mFavoriteItemDao.queryBuilder().where().eq("news_ID",newsId).query();
        return (favoriteItems.size() > 0);
    }
}
