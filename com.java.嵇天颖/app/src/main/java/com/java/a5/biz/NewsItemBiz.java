package com.java.a5.biz;

import android.content.Context;
import android.util.Log;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntryImpl;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;
import com.java.a5.bean.NewsItem;
import com.java.a5.bean.NewsItemSeen;
import com.java.a5.dao.NewsItemDao;
import com.java.a5.dao.NewsItemSeenDao;
import com.java.a5.utils.FileUtils;
import com.java.a5.utils.HttpUtils;
import com.java.a5.utils.NewsAPIUtils;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//上面直接变成谷歌的包


/**
 * 处理新闻的业务逻辑类
 */
public class NewsItemBiz {

    private NewsItemDao mNewsItemDao;
    private NewsItemSeenDao mNewsItemSeenDao;
    private FileUtils fileUtils;

    public NewsItemBiz(Context context) {
        mNewsItemDao = new NewsItemDao(context);
        mNewsItemSeenDao = new NewsItemSeenDao(context);
        fileUtils = new FileUtils(context);
    }
    public NewsItemDao getmNewsItemDao(){
        return mNewsItemDao;
    }


    /**
     * 获取新闻项的数据库缓存
     * @param newsType  类型
     * @param currentPage   当前页
     * @return  新闻项列表缓存
     * @throws SQLException
     */
    public List<NewsItem> getNewsItemCache(String newsType,int currentPage) throws SQLException {
        return mNewsItemDao.getCache(newsType);
    }

    /**
     * 根据新闻类型和页码得到新闻列表
     * @param newsType      新闻URL类型
     * @param currentPage   页码
     * @return              新闻列表
     * @throws Exception
     */
//    public List<NewsItem> getNewsItems(String newsType,int currentPage) throws Exception {
//
//        String url = NewsAPIUtils.getNewsUrl(newsType, currentPage);
//
//        String jsonStr = null;
//        //如果服务器未返回数据,则返回数据库中的数据
//        try {
//            jsonStr = HttpUtils.doGet(url);
//        }catch (Exception ex){
//            return getNewsItemCache(newsType,currentPage);
//        }
//        Log.i("GETITEM", "getNewsItems: " + url);
//        jsonStr = StringUtils.replaceBlankAndSpace(jsonStr);
//
//        //jsonStr = StringUtils.replaceNme(jsonStr);
//
//        String PATTERN = "\"list\":(\\[.*\\])";
//        Pattern p =Pattern.compile(PATTERN);
//        Matcher m = p.matcher(jsonStr);
//        String jsonList = null;
//        if(m.find()) jsonList = m.group(1);
//        else return null;
//        Log.i(getClass().getName(), "jsonList: " + jsonList);
//
//        jsonList = StringUtils.getRightJsonSyntax(jsonList);
//        Log.i(getClass().getName(), "rightJsonList: " + jsonList);
//
//
//        List<NewsItem> newsItems = new ArrayList<NewsItem>();
//        try {
//            newsItems = new Gson().fromJson(jsonList, new TypeToken<List<NewsItem>>() {}.getType());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        for(NewsItem newsItem : newsItems) {
//            //Log.i(newsItem.getTitle(),"newsItemInfo"+newsItem.getSourceUrl());
//            Log.i("info",currentPage+newsItem.getTitle());
//            mNewsItemDao.createOrUpdate(newsItem);
//        }
//        Log.i("newsItemInfo:", String.valueOf(newsItems.size()));
//        return newsItems;
//    }

    public List<NewsItem> getItems(String newsType) throws Exception{
        String rss = NewsAPIUtils.getNewsUrl(newsType);

        String jsonStr;
        try {
            jsonStr = HttpUtils.doGet(rss);
        }catch (Exception ex){
            return getNewsItemCache(newsType,1);
        }

        URL url = new URL(rss);


        SyndFeedInput input = new SyndFeedInput();
        XmlReader reader = new XmlReader(url);

        System.out.println("Rss源的编码格式为：" + reader.getEncoding());
        SyndFeed feed = input.build(reader);
        System.out.println("test");

        List<SyndEntryImpl> entries = feed.getEntries();

        NewsItem item = null;
        List<NewsItem> newsItems = new ArrayList<NewsItem>();

        //item = new NewsItem();

        for(SyndEntryImpl entry : entries) {
            item = new NewsItem();

            item.setTitle(entry.getTitle().trim());
            item.setSourceUrl(entry.getLink());
            item.setID(entry.getLink());
            item.setType(newsType);
            Log.i("url",entry.getLink());
            //item.setPubDate(entry.getPublishedDate());
            //item.setAuthor(entry.getAuthor());

            newsItems.add(item);
        }
        for(NewsItem newsItem : newsItems) {
            mNewsItemDao.createOrUpdate(newsItem);
        }

        //Log.i("newsItemInfo:", String.valueOf(newsItems.size()));

        Log.i("cacheseen",newsItems.toString());
        return newsItems;
    }

    public List<NewsItem> getNewsItems(String newsType,int currentPage) throws Exception {

        if (newsType.equals("0")){
            Log.i("mymynewsType",newsType);
            //int TuiJian[] = NewsAPIUtils.getTuiNewsUrl();
            int TuiJian[] = {0,0,0,0,0,0,0,0,0,0,0,0,0};
            String index[] = {"1","2","3","4","5","6","7","8","9","10","11","12"};
            int pos[]={0,0,0,0};
            int max_int[]={0,0,0,0};
            List<NewsItemSeen> newsItemsSeen = new ArrayList<NewsItemSeen>();
            newsItemsSeen = mNewsItemSeenDao.queryAll();
            Log.i("mymymaxsize", String.valueOf(newsItemsSeen.size()));
            for (int i = 0; i < newsItemsSeen.size(); i++)
            {
                TuiJian[Integer.parseInt(newsItemsSeen.get(i).getNewsClassTag())-1]++;
                Log.i("mymymaxi", String.valueOf(newsItemsSeen.get(i).getNewsClassTag()));
            }

            float pro[]={0,0,0,0};
            int sum = 0;
            List<NewsItem> newsItems = new ArrayList<NewsItem>();
            for (int i = 0; i < 1; i++)
            {
                int max = 0;
                for (int j = 0; j < 12; j++) {
                    if (TuiJian[j] > max) {
                        max = TuiJian[j];
                        pos[i] = j;
                    }
                }
               TuiJian[pos[i]] = 0;
               max_int[i] = max;
               Log.i("mymymaxindex", String.valueOf(pos[i]));
               sum += max;
            }
            if (sum==0) return  newsItems;
            Log.i("mymymax", String.valueOf(sum));
            for (int i = 0; i < 1; i++)
            {
                //if (max_int[i]==0) continue;
                int count = (int) ( 5 * (float)max_int[i] / sum);
                List<NewsItem> Items = getItems(index[pos[i]]);
                for (int j = 0; j < count; j++)
                    newsItems.add(Items.get(j));
            }
            return newsItems;
        }
        else{
            return getItems(newsType);
        }
    }

    public List<NewsItem> getNewsItemsByKey(String keyword,int currentPage) throws Exception {
        return mNewsItemDao.searchByKey(keyword);

    }

    /**
     * 清除缓存数据库
     */
    public void clearCache(){
        mNewsItemDao.deleteAll();
        mNewsItemSeenDao.deleteAll();
        fileUtils.deleteFile();
    }
}
