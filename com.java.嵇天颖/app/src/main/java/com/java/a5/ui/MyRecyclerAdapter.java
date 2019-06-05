package com.java.a5.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.java.a5.R;
import com.java.a5.bean.NewsItem;
import com.java.a5.dao.NewsItemSeenDao;
import com.java.a5.utils.FileUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {



    //当前显示的数据
    private List<NewsItem> mNewsList = new ArrayList<NewsItem>();

    //private NewsItemDao mNewsItemDao;
    private NewsItemSeenDao mNewsItemSeenDao;

    private Drawable mDrawble;

    //记录所有正在下载或等待下载的任务
    private Set<ViewHolder.BitmapWorkerTask> taskCollection;

    // 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉
    private LruCache<String, Bitmap> mMemoryCache;
    private FileUtils fileUtils;


    public MyRecyclerAdapter(Context context){
        this(context,null);
        //mNewsItemDao = new NewsItemDao(context);
        mNewsItemSeenDao = new NewsItemSeenDao(context);
    }


    public MyRecyclerAdapter(Context context, List<NewsItem> myDataset){
        mNewsList = myDataset != null ? myDataset : new ArrayList<NewsItem>();
        taskCollection = new HashSet<ViewHolder.BitmapWorkerTask>();
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 10;
        // 设置图片缓存大小为程序最大可用内存的1/10
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
        fileUtils = new FileUtils(context);
    }

    public List<NewsItem> getmNewsList() {
        return mNewsList;
    }
    public void addNews(List<NewsItem> news){
        mNewsList.addAll(news);
        Log.i("LIXU", "adapter" + mNewsList.size());
    }

    public void setNews(List<NewsItem> news){
        mNewsList = news;
        Log.i("LIXU", "adapter" + mNewsList.size());
    }

//    public void setNewsItemDao(NewsItemDao mNewsItemDao){
//        this.mNewsItemDao = mNewsItemDao;
//    }

    /**
     * 创建ViewHolder
     * @param viewGroup 父View
     * @param i 位置
     * @return 返回得到的ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview,
                viewGroup,false);

        //View x = v.findViewById(R.id.showList);
        //x.setBackgroundResource(R.drawable.view_click_state_seen);

        TextView titleTextView = (TextView) v.findViewById(R.id.titleTextView);
        TextView sourceTextView = (TextView) v.findViewById(R.id.sourceTextView);
        ImageView titleImageView = (ImageView) v.findViewById(R.id.titleImageView);
        return new ViewHolder(v,titleTextView,sourceTextView,titleImageView);
    }
    /**
     * 将数据绑定到ViewHolder
     * @param viewHolder    要绑定的ViewHolder
     * @param i             ViewHolder的位置
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        try {
            if (mNewsItemSeenDao.searchIsExistByNewsId(mNewsList.get(i).getNews_id()))
                viewHolder.mView.findViewById(R.id.showList).setBackgroundResource(R.drawable.view_click_state_seen);//设置看过的NewItem为灰色
        } catch (SQLException e) {
            e.printStackTrace();
        }
        viewHolder.bindData(mNewsList.get(i));
    }

    @Override
    public int getItemCount() {
        if (mNewsList != null) {
            return mNewsList.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private View mView;
        private TextView mTitleTextView;
        private TextView mSourceTextView;
        private ImageView mTitleImageView;

        private NewsItem mNewsItem;

        public ViewHolder(View v){
            super(v);
        }

        public ViewHolder(View v,TextView titleTextView,TextView sourceTextView,ImageView imageView){
            this(v);
            v.setOnClickListener(this);
            mTitleTextView = titleTextView;
            mSourceTextView = sourceTextView;
            mTitleImageView = imageView;
            mView = v;
        }

        @Override
        public void onClick(View view) {}

        public void bindData(NewsItem newsItem){
            mTitleTextView.setText(newsItem.getTitle());
            mSourceTextView.setText(newsItem.getOrigin());

            loadBitmaps(newsItem);
            mNewsItem = newsItem;
        }
        private void loadBitmaps(NewsItem newsItem) {
            String imageUrl = newsItem.getImgsUrl();
            try {
                //get from memory or file
                Bitmap bitmap = getBitmapFromCache(imageUrl);
                if (bitmap == null) {
                    BitmapWorkerTask task = new BitmapWorkerTask(newsItem, mTitleImageView);
                    taskCollection.add(task);
                    task.execute(imageUrl);
                } else {
                    mTitleImageView.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
            if (getBitmapFromMemoryCache(key) == null) {
                mMemoryCache.put(key, bitmap);
            }
        }

        public Bitmap getBitmapFromCache(String url) {
            Bitmap bitmap = getBitmapFromMemoryCache(url);
            String urlPathName = fileUtils.getUrlPathName(url);
            if(bitmap != null){
                return bitmap;
            }else if(fileUtils.isFileExists(urlPathName) && fileUtils.getFileSize(urlPathName) != 0){
                //从SD卡获取手机里面获取Bitmap
                bitmap = fileUtils.getBitmap(urlPathName);
                //将Bitmap 加入内存缓存
                addBitmapToMemoryCache(url, bitmap);
                return bitmap;
            }
            return null;
        }

        public Bitmap getBitmapFromMemoryCache(String key) {
            return mMemoryCache.get(key);
        }
        /**
         * 取消所有正在下载或等待下载的任务。
         */
        public void cancelAllTasks() {
            if (taskCollection != null) {
                for (BitmapWorkerTask task : taskCollection) {
                    task.cancel(false);
                }
            }
        }

        class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
            private String imageUrl;
            private ImageView mTitleImageView;
            private NewsItem mNewsItem;
            public BitmapWorkerTask(NewsItem newsItem, ImageView titleImageView) {
                mNewsItem = newsItem;
                mTitleImageView = titleImageView;
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                imageUrl = params[0];
                Bitmap bitmap = downloadBitmap(params[0]);
                if (bitmap != null) {
                    addBitmapToMemoryCache(params[0], bitmap);
                    try {
                        //保存在SD卡或者手机目录
                        fileUtils.savaBitmap(fileUtils.getUrlPathName(params[0]), bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                mTitleImageView.setImageBitmap(bitmap);
                taskCollection.remove(this);
            }

            private Bitmap downloadBitmap(String imageUrl) {

                mNewsItem.setBitmap();
                return mNewsItem.getBitmap();
            }

        }
    }
}