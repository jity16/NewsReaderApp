package com.java.a5.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.java.a5.R;
import com.java.a5.bean.NewsType;
import com.java.a5.dao.NewsTypeDao;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.RemoveListener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wuhanghang on 16-9-2.
 */
public class CategoryManagementActivity  extends BaseActivity {

    private MyDragSortListViewAdapter watchCategoryAdapter;
    private MyDragSortListViewAdapter unwatchCategoryAdapter;
    private DragSortListView watchListView;
    private DragSortListView unwatchListView;
    private List<String> watchCategory;
    private List<String> unwatchCategory;

    private NewsTypeDao mNewsTypeDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_18dp));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategoryManagementActivity.this.finish();
                Log.i("drag", "watchCategory is: ");
                for(int i = 0; i < watchCategory.size(); ++i) {
                    Log.i("drag", watchCategory.get(i));
                    try {
                        NewsType newsType= mNewsTypeDao.searchByShowType(watchCategory.get(i));
                        newsType.setShowOrder(i);
                        mNewsTypeDao.createOrUpdate(newsType);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("drag", "unwatchCategory is: ");
                for(int i = 0; i < unwatchCategory.size(); ++i) {
                    Log.i("drag", unwatchCategory.get(i));
                    try {
                        NewsType newsType= mNewsTypeDao.searchByShowType(unwatchCategory.get(i));
                        newsType.setShowOrder(-1);
                        mNewsTypeDao.createOrUpdate(newsType);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("drag", "***********************");

                Intent intent = new Intent(CategoryManagementActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//刷新
                startActivity(intent);
            }
        });

        initData();
        //得到滑动watchListview并且设置监听器。

        watchListView.setDropListener(onDropWatch);
        unwatchListView.setDropListener(onDropUnwatch);
        watchListView.setRemoveListener(onRemoveWatch);
        unwatchListView.setRemoveListener(onRemoveUnwatch);


        watchCategoryAdapter = new MyDragSortListViewAdapter(CategoryManagementActivity.this, watchCategory, true);
        unwatchCategoryAdapter = new MyDragSortListViewAdapter(CategoryManagementActivity.this, unwatchCategory, false);
        watchListView.setAdapter(watchCategoryAdapter);
        unwatchListView.setAdapter(unwatchCategoryAdapter);
    }
    //监听器在手机拖动停下的时候触发
    private DragSortListView.DropListener onDropWatch =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {//from to 分别表示 被拖动控件原位置 和目标位置
                    if (from != to) {
                        String item = (String)watchCategoryAdapter.getItem(from);//得到listview的适配器
                        watchCategoryAdapter.remove(from);//在适配器中”原位置“的数据。
                        watchCategoryAdapter.insert(item, to);//在目标位置中插入被拖动的控件。
                    }
                }
            };

    private DragSortListView.DropListener onDropUnwatch =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {//from to 分别表示 被拖动控件原位置 和目标位置
                    if (from != to) {
                        String item = (String)unwatchCategoryAdapter.getItem(from);//得到listview的适配器
                        unwatchCategoryAdapter.remove(from);//在适配器中”原位置“的数据。
                        unwatchCategoryAdapter.insert(item, to);//在目标位置中插入被拖动的控件。
                    }
                }
            };
    private RemoveListener onRemoveWatch =
            new DragSortListView.RemoveListener() {
                @Override
                public void remove(int which) {
                    Log.i("-", "remove: watch");
                    String item = (String)watchCategoryAdapter.getItem(which);
                    unwatchCategoryAdapter.insert(item, 0);
                    watchCategoryAdapter.remove(which);
                }
            };

    private RemoveListener onRemoveUnwatch =
            new DragSortListView.RemoveListener() {
                @Override
                public void remove(int which) {
                    Log.i("-", "remove: unwatch");
                    String item = (String)unwatchCategoryAdapter.getItem(which);
                    watchCategoryAdapter.insert(item, 0);
                    unwatchCategoryAdapter.remove(which);
                }
            };

    private void initData() {//初始化
        watchCategory=new ArrayList<String>();
        unwatchCategory=new ArrayList<String>();
        mNewsTypeDao = new NewsTypeDao(CategoryManagementActivity.this);
        List<NewsType> allNewsType = mNewsTypeDao.queryAll();
        Collections.sort(allNewsType);
        for(NewsType newsType : allNewsType) {
            if(newsType.getShowOrder() >= 0)
                watchCategory.add(newsType.getShowType());
            else
                unwatchCategory.add(newsType.getShowType());
        }
        watchListView = (DragSortListView) findViewById(R.id.dslvWatchList);
        unwatchListView = (DragSortListView) findViewById(R.id.dslvUnwatchList);
    }
}