package com.java.a5.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.java.a5.bean.FavoriteItem;
import com.java.a5.bean.NewsItem;
import com.java.a5.bean.NewsItemSeen;
import com.java.a5.bean.NewsType;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 *
 */
public class DataBaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "News.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<NewsType,Integer> newsTypeDao = null;
    private Dao<NewsItem,Integer> newsItemDao = null;
    private Dao<FavoriteItem,Integer> favoriteItemDao = null;
    private Dao<NewsItemSeen,Integer> newsItemSeenDao = null;

    private RuntimeExceptionDao<NewsType,Integer> newsTypeRuntimeDao = null;
    private RuntimeExceptionDao<NewsItem,Integer> newsItemRuntimeDao = null;
    private RuntimeExceptionDao<FavoriteItem,Integer> favoriteItemRuntimeDao = null;
    private RuntimeExceptionDao<NewsItemSeen,Integer> newsItemSeenRuntimeDao = null;


    private static DataBaseHelper helper = null;

    private DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 获取helper对象
     * @param context
     * @return helper对象
     */
    public static DataBaseHelper getHelper(Context context){
        if (helper == null){
            helper = new DataBaseHelper(context);
        }
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {

        try {

            TableUtils.createTable(connectionSource, NewsType.class);
            TableUtils.createTable(connectionSource, NewsItem.class);
            TableUtils.createTable(connectionSource, FavoriteItem.class);
            TableUtils.createTable(connectionSource, NewsItemSeen.class);

            newsItemDao = getNewsItemDao();
            newsTypeDao = getNewsTypeDao();
            favoriteItemDao = getFavoriteItemDao();
            newsItemSeenDao = getNewsItemSeenDao();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {

        try {
            TableUtils.dropTable(connectionSource, NewsItem.class, true);
            TableUtils.dropTable(connectionSource, NewsType.class, true);
            TableUtils.dropTable(connectionSource, FavoriteItem.class, true);
            TableUtils.dropTable(connectionSource, NewsItemSeen.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Dao<NewsType,Integer> getNewsTypeDao() throws SQLException {
        if (newsTypeDao == null){
            newsTypeDao = getDao(NewsType.class);
        }
        return newsTypeDao;
    }
    public Dao<NewsItem,Integer> getNewsItemDao() throws SQLException {
        if (newsItemDao == null){
            newsItemDao = getDao(NewsItem.class);
        }
        return newsItemDao;
    }
    public Dao<FavoriteItem,Integer> getFavoriteItemDao() throws SQLException {
        if (favoriteItemDao == null){
            favoriteItemDao = getDao(FavoriteItem.class);
        }
        return favoriteItemDao;
    }

    public Dao<NewsItemSeen,Integer> getNewsItemSeenDao() throws SQLException {
        if (newsItemSeenDao == null){
            newsItemSeenDao = getDao(NewsItemSeen.class);
        }
        return newsItemSeenDao;
    }


    public RuntimeExceptionDao<NewsType, Integer> getNewsTypeRuntimeDao() {
        if (newsTypeRuntimeDao == null){
            newsTypeRuntimeDao = getRuntimeExceptionDao(NewsType.class);
        }
        return newsTypeRuntimeDao;

    }
    public RuntimeExceptionDao<NewsItem, Integer> getNewsItemRuntimeDao() {
        if (newsItemRuntimeDao == null){
            newsItemRuntimeDao = getRuntimeExceptionDao(NewsItem.class);
        }
        return newsItemRuntimeDao;
    }
    public RuntimeExceptionDao<FavoriteItem, Integer> getFavoriteItemRuntimeDao() {
        if (favoriteItemRuntimeDao == null){
            favoriteItemRuntimeDao = getRuntimeExceptionDao(FavoriteItem.class);
        }
        return favoriteItemRuntimeDao;
    }
    public RuntimeExceptionDao<NewsItemSeen, Integer> getNewsItemSeenRuntimeDao() {
        if (newsItemSeenRuntimeDao == null){
            newsItemSeenRuntimeDao = getRuntimeExceptionDao(NewsItemSeen.class);
        }
        return newsItemSeenRuntimeDao;

    }


}
