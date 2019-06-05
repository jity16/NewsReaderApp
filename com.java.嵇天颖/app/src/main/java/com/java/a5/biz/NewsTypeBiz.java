package com.java.a5.biz;

import android.content.Context;
import android.util.Log;

import com.java.a5.bean.NewsType;
import com.java.a5.dao.NewsTypeDao;
import com.java.a5.utils.NewsAPIUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理新闻类型
 */

public class NewsTypeBiz {
    private Context mContext;

    private NewsTypeDao mNewsTypeDao;

    public NewsTypeBiz(Context context) {
        mContext = context;
        mNewsTypeDao = new NewsTypeDao(context);
    }
    /*获取数据库中的新闻频道*/
    public List<NewsType> getNewsTypeCache(){
        try {
            return mNewsTypeDao.getCache();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*在服务器拉取数据，解析*/
    public List<NewsType> getNewsTypes(boolean netAvailable) throws Exception {
        String jsonStr = NewsAPIUtils.getTypeUrl();
        Log.i(getClass().getName(), "getNewsTypes: " + jsonStr);
        String PATTERN = "([0-9]{1,2}):(\\w{2}),";
        Pattern p =Pattern.compile(PATTERN);
        Matcher m = p.matcher(jsonStr);
        mNewsTypeDao.setAllIsExist(false);
        List<NewsType> newsTypes = new ArrayList<NewsType>();
        while(m.find()) {
            Log.i("getNewsType", "getKeyValue   " + m.group(1) + ":" + m.group(2));
            NewsType newsType = new NewsType();
            newsType.setUrlType(m.group(1));
            newsType.setShowType(m.group(2));
            newsType.setExist(true);
            NewsType originNewsType = mNewsTypeDao.searchByShowType(newsType.getShowType());
            //TODO 数据库与find的不一致时需要测试
            if(originNewsType == null) { //原先数据库中不存在
                newsType.setShowOrder(0); //默认要收看
                newsTypes.add(newsType); //默认要显示
            }
            else { //原先数据库中存在
                //showOrder与原先保持一致
                int originShowOrder = originNewsType.getShowOrder();
                newsType.setShowOrder(originShowOrder);
                //是否显示
                if(originShowOrder >= 0)
                    newsTypes.add(newsType);
            }
            //要保存
            mNewsTypeDao.createOrUpdate(newsType);
        }
        Collections.sort(newsTypes);
        return newsTypes;

    }
}
