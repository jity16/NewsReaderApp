package com.java.a5.ui;


import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconToolbar;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.iflytek.cloud.SpeechUtility;
import com.java.a5.QuickNews;
import com.java.a5.R;
import com.java.a5.bean.NewsType;
import com.java.a5.biz.NewsItemBiz;
import com.java.a5.biz.NewsTypeBiz;
import com.java.a5.ui.fragments.NewsListFragment;
import com.java.a5.utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.OnClickListener;


public class MainActivity extends BaseActivity implements ObservableScrollViewCallbacks {


    //文字模式
    private QuickNews quickNews;

    private ViewPager mViewPager;
    //新闻列表
    private List<NewsListFragment> mFragmentList;

    private ViewGroup mMainPage;
    private DrawerLayout mDrawerLayout;
    private ViewGroup mDrawer;

    //设置按钮区域
    private View mAppSetting;
    private View mAboutButton;
    private View mShareButton;
    private View mFavoriteButton;
    private View mCategoryManagement;
    private View mImageButton;
    private ImageButton searchButton;

    private MaterialMenuIconToolbar mMaterialMenu;

    PagerSlidingTabStrip mTabs;

    private ViewGroup mContent;

    //缓存
    private List<NewsType> mNewsTypes = new ArrayList<NewsType>();

    //侧边栏头部图片
    private ImageView mHeaderImage;

    //标识是否点击过一次back退出
    private boolean mIsExit = false;
    //点击返回键时，延时 TIME_TO_EXIT 毫秒发送此handler重置mIsExit，再其被重置前如果再按一次返回键则退出应用
    private Handler mExitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mIsExit = false;
        }
    };
    final static int TIME_TO_EXIT = 2000;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initViews();

        initViewPager();
//        InitSpeechUtility();//初始化语音合成对象
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        quickNews = (QuickNews)getApplication();
        quickNews.setImageMode(true);
    }
    private void InitSpeechUtility()
    {//初始化语音合成对象
        SpeechUtility.createUtility(this, "appid=" + "59b3804b");
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.main_activity_title));
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });


        searchButton = (ImageButton) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });


        mMaterialMenu = new MaterialMenuIconToolbar(this, Color.BLACK, MaterialMenuDrawable.Stroke.THIN) {
            @Override
            public int getToolbarViewId() {
                return R.id.toolbar;
            }
        };
        mMaterialMenu.setNeverDrawTouch(true);

        mTintManager.setStatusBarTintEnabled(false);

        //设置侧滑菜单的监听
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View view, float v) {
                mMaterialMenu.setTransformationOffset(MaterialMenuDrawable.AnimationState.BURGER_ARROW,
                        v);
            }

            @Override
            public void onDrawerOpened(View view) {
                mMaterialMenu.animatePressedState(intToState(1));
            }

            @Override
            public void onDrawerClosed(View view) {
                mMaterialMenu.animatePressedState(intToState(0));
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });

        mContent = (ViewGroup) findViewById(R.id.content);
        mDrawer = (ViewGroup) findViewById(R.id.drawer);
        mMainPage = (ViewGroup) findViewById(R.id.main_page);
        //因为导航栏透明，要让出顶部和底部空间
        if (isNavBarTransparent()) {
            mMainPage.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight());
            mDrawer.setPadding(0, 0, 0, getNavigationBarHeight());
        }

        //侧边栏
        mHeaderImage = (ImageView) findViewById(R.id.header_img);
        mHeaderImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //resume the click
            }
        });
        //分享
        mShareButton = findViewById(R.id.drawer_item_share);
        mShareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String imageUrl = MainActivity.this.getResources().getString(R.string.app_icon_url);
                showShare(MainActivity.this, MainActivity.this.getResources().getString(R.string.share_app_string),
                        "分享应用", null,
                        imageUrl,
                        "https://cloud.tsinghua.edu.cn/f/f92ad8cab4b64e9185ce/"
                        );
            }
        });
        //Favorite
        mFavoriteButton = findViewById(R.id.drawer_item_favorite);
        mFavoriteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });
        //Category Management
        mCategoryManagement = findViewById(R.id.drawer_category_management);
        mCategoryManagement.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CategoryManagementActivity.class);
                startActivity(intent);
            }
        });

        //文字图片模式
//        mImageButton = findViewById(R.id.drawer_item_image);
//        mImageButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                quickNews.setImageMode(!quickNews.getImageMode());
//                if (quickNews.getImageMode()){
//                    Toast.makeText(MainActivity.this,"图文模式",Toast.LENGTH_LONG).show();
//                }
//                else {
//                    Toast.makeText(MainActivity.this,"文本模式",Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });



        //关于
        mAboutButton = findViewById(R.id.drawer_item_about);
        mAboutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
        //设置
        mAppSetting = (ViewGroup) findViewById(R.id.drawer_item_clear_cache);
        mAppSetting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsItemBiz biz = new NewsItemBiz(MainActivity.this);
                biz.clearCache();
                Toast.makeText(MainActivity.this,getResources().getText(R.string.delete_success),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initViewPager() {

        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mFragmentList = new ArrayList<NewsListFragment>();

        getFragmenList(mFragmentList, true);

    }

    private void getFragmenList(List<NewsListFragment> fragmentList, boolean forced) {
        int total = mNewsTypes.size();
        LoadNewsTypesTask loadDataTask = new LoadNewsTypesTask(fragmentList, forced);
        loadDataTask.execute(0);
    }

    /**
     * 加载新闻列表的任务
     */
    class LoadNewsTypesTask extends AsyncTask<Integer, Integer, List<NewsType>> {

        private List<NewsListFragment> mFragmentList;
        private boolean mIsForced;

        public LoadNewsTypesTask(List<NewsListFragment> fragmentList, boolean forced) {
            super();
            mFragmentList = fragmentList;
            mIsForced = forced;
        }

        /**
         * 得到当前页码的新闻列表
         *
         * @param currentPage 当前页码
         * @return 当前页码的新闻列表, 出错返回null
         */
        @Override
        protected List<NewsType> doInBackground(Integer... currentPage) {

            NewsTypeBiz mNewsTypeBiz = new NewsTypeBiz(MainActivity.this);
            try {
                boolean netAvailable = HttpUtils.IsNetAvailable(MainActivity.this);
                Log.i("test-net-available", "MainActivity-doInBackground: netAvailable:" + netAvailable);
                if (!netAvailable){
                    return mNewsTypeBiz.getNewsTypeCache();
                }
                List<NewsType> newsTypes = mNewsTypeBiz.getNewsTypes(true);
                if(newsTypes == null) {
                    return mNewsTypeBiz.getNewsTypeCache();
                }
                return newsTypes;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("ASDNET", "neterror :" + e);
                return mNewsTypeBiz.getNewsTypeCache();
            }

        }

        /**
         * 得到新闻列表后将其加载
         *
         * @param newsTypes 得到的新闻列表
         */
        @Override
        protected void onPostExecute(List<NewsType> newsTypes) {
            if (newsTypes == null) {
                Toast.makeText(MainActivity.this, "获取新闻频道失败，请检查网络稍后再试"
                        , Toast.LENGTH_SHORT).show();
                return;
            }
            mNewsTypes.addAll(newsTypes);
            for(NewsType newsType : newsTypes) {
                NewsListFragment fragment = NewsListFragment.newInstance(newsType,"NotSearchType");
                mFragmentList.add(fragment);
                Log.i(getClass().getName(), "Add Type: " + newsType.getShowType());
            }

            MyViewPagerAdapter adapter = new MyViewPagerAdapter(getSupportFragmentManager(), mFragmentList);
            mViewPager.setAdapter(adapter);
            mViewPager.setCurrentItem(0);
            mTabs.setAllCaps(false);
            mTabs.setViewPager(mViewPager);
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
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    /**
     * 根据滑动方向设置ToolBar的显隐
     *
     * @param scrollState 滑动方向
     */
    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (scrollState == ScrollState.UP) {
            if (toolbarIsShown()) {
                hideToolbar();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (toolbarIsHidden()) {
                showToolbar();
            }
        }
    }


    private boolean toolbarIsShown() {
        return mToolbar.getTranslationY() == 0;
    }

    private boolean toolbarIsHidden() {
        return mToolbar.getTranslationY() == -mToolbar.getHeight();
    }

    private void showToolbar() {
        moveToolbar(0);
    }


    private void hideToolbar() {
        moveToolbar(-mToolbar.getHeight());
    }


    /**
     * 将toolbar移动到某个位置
     *
     * @param toTranslationY 移动到的Y轴位置
     */
    private void moveToolbar(float toTranslationY) {
        if (mToolbar.getTranslationY() == toTranslationY) {
            return;
        }
        //利用动画过渡移动的过程
        final ValueAnimator animator = ValueAnimator.ofFloat(mToolbar.getTranslationY(), toTranslationY).
                setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float translationY = (Float) animator.getAnimatedValue();
                mToolbar.setTranslationY(translationY);
                mContent.setTranslationY(translationY);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mContent.getLayoutParams();
                lp.height = (int) (getScreenHeight() - translationY - getStatusBarHeight()
                        - lp.topMargin);
                if (CURRENT_VERSION >= VERSION_KITKAT && VERSION_LOLLIPOP > CURRENT_VERSION) {
                    lp.height -= getNavigationBarHeight();
                }
                Log.i("TEST", "after" + Float.toString(mToolbar.getHeight()));
                mContent.requestLayout();
            }
        });
        animator.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 实现点击两次退出程序
     */
    private void exit() {
        if (mIsExit) {
            finish();
            System.exit(0);
        } else {
            mIsExit = true;
            Toast.makeText(getApplicationContext(), R.string.click_to_exit, Toast.LENGTH_SHORT).show();
            //两秒内不点击back则重置mIsExit
            mExitHandler.sendEmptyMessageDelayed(0, TIME_TO_EXIT);
        }
    }
}
