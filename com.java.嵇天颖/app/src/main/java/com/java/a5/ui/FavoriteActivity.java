package com.java.a5.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.java.a5.R;
import com.java.a5.bean.FavoriteItem;
import com.java.a5.dao.FavoriteItemDao;

import java.util.Collections;
import java.util.List;

/**
 * Created by wuhanghang on 16-9-3.
 */

public class FavoriteActivity extends BaseActivity {

    private FavoriteItemDao mFavoriteItemDao;
    private FavoriteAdapter mFavoriteAdapter;
    private List<FavoriteItem> mFavoriteItems;
    private ListView mFavoriteListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_18dp));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FavoriteActivity.this.finish();
//                Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
//                startActivity(intent);
            }
        });
        mFavoriteItemDao = new FavoriteItemDao(FavoriteActivity.this);
        mFavoriteItems = mFavoriteItemDao.queryAll();
        Collections.reverse(mFavoriteItems);
        mFavoriteAdapter = new FavoriteAdapter(FavoriteActivity.this, mFavoriteItems);
        mFavoriteListView = (ListView)findViewById(R.id.favoriteListView);
        mFavoriteListView.setAdapter(mFavoriteAdapter);
        mFavoriteListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                FavoriteItem favoriteItem = mFavoriteItems.get(position);
                //打开显示新闻内容的Activity,把新闻的url作为参数传过去
                Intent startActivityIntent = new Intent(FavoriteActivity.this,NewsContentActivity.class);

                view.setDrawingCacheEnabled(true);
                view.setPressed(false);
                view.refreshDrawableState();
                Bitmap bitmap = view.getDrawingCache();
                ActivityOptions options = ActivityOptions.makeThumbnailScaleUpAnimation(
                        view, bitmap, 0, 0);

                Bundle urlBundle = new Bundle();
                urlBundle.putString("news_URL",favoriteItem.getSourceUrl());
                urlBundle.putString("news_Title", favoriteItem.getTitle());
                urlBundle.putString("news_ID", favoriteItem.getNewsId());
                urlBundle.putString("news_Author", favoriteItem.getOrigin());
                //urlBundle.putString("newsClassTag",favoriteItem.getType());
                urlBundle.putString("caller", "FavoriteActivity");
                startActivityIntent.putExtra("key",urlBundle);
                ActivityCompat.startActivity(FavoriteActivity.this, startActivityIntent, options.toBundle());
            }
        });
    }
}
