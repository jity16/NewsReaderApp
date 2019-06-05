package com.java.a5.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.SearchView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.java.a5.R;
import com.java.a5.bean.NewsType;
import com.java.a5.ui.fragments.NewsListFragment;
import com.java.a5.ui.widget.GestureFrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Starry Sky on 2017/9/10.
 */

public class SearchActivity extends BaseActivity implements ObservableScrollViewCallbacks {
    private GestureFrameLayout mGestureFrameLayout;  //滑动返回
    private SearchView mSearchView;
    private ViewPager mViewPager;
    public List<NewsListFragment> mFragmentList;
    public int count = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_18dp));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.this.finish();
            }
        });
        mFragmentList = new ArrayList<NewsListFragment>();
        mViewPager = (ViewPager) findViewById(R.id.pager_search);
        mSearchView = (SearchView)findViewById(R.id.searchview);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        // 当点击搜索按钮时触发该方法
                @Override
                public boolean onQueryTextSubmit(String query){
                    count++;
                    ViewInit(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    mViewPager.clearFocus();
                    return false;
                }
            });
        mGestureFrameLayout = (GestureFrameLayout) findViewById(R.id.search_gesture_layout);
        mGestureFrameLayout.attachToActivity(this);
    }

    public void ViewInit(String keywords){


        SearchActivity.LoadNewsTypesTask loadDataTask = new SearchActivity.LoadNewsTypesTask(mFragmentList, false);
        loadDataTask.execute(keywords);
    }

    class LoadNewsTypesTask extends AsyncTask<String, Integer, String> {

       // private List<NewsListFragment> mFragmentList;
        private boolean mIsForced;

        public LoadNewsTypesTask(List<NewsListFragment> fragmentList, boolean forced) {
            super();
            //mFragmentList = fragmentList;
            mIsForced = forced;
        }

        @Override
        protected String doInBackground(String... currentPage) {
            return currentPage[0];
        }

        @Override
        protected void onPostExecute(String keywords) {
            NewsListFragment fragment = NewsListFragment.newInstance(new NewsType(), keywords);
            mFragmentList.add(fragment);
            MyViewPagerAdapter adapter = new MyViewPagerAdapter(getSupportFragmentManager(), mFragmentList);
            mViewPager.setAdapter(adapter);
            mViewPager.setCurrentItem(count);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }
}
