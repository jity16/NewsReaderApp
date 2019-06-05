package com.java.a5.ui.fragments;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.java.a5.R;
import com.java.a5.bean.NewsItem;
import com.java.a5.bean.NewsType;
import com.java.a5.ui.MyRecyclerAdapter;
import com.java.a5.ui.NewsContentActivity;
import com.java.a5.ui.RecyclerItemClickListener;
import com.java.a5.utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class NewsListFragment extends BaseFragment {

    private static final String ARG_NEWS_TYPE = "newsType";//"科技"
    private static final String ARG_NEWS_URL_TYPE = "newsUrlType"; //"1"
    private static final String NEWS_ITEM_IS_SEARCH = "NotSearchType";

    private String keywords;

    //新闻类型
    private String mNewsType;
    private String mNewsUrlType;

    private ObservableRecyclerView mRecyclerView;
    private MyRecyclerAdapter mAdapter;
    private ObservableRecyclerView.LayoutManager mLayoutManager;

    //当前页码
    private int mCurrentPage;

    private PtrFrameLayout frame;
    private MaterialHeader header;

    //是否为第一次加载数据
    private boolean mIsFirstLoad = true;
    private List<NewsItem> mNewsItems = new ArrayList<NewsItem>();

    public NewsListFragment() {
        // Required empty public constructor
    }

    public static NewsListFragment newInstance(NewsType newsType, String keyword) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NEWS_TYPE, newsType.getShowType());
        args.putString(ARG_NEWS_URL_TYPE, newsType.getUrlType());
        args.putSerializable(NEWS_ITEM_IS_SEARCH, keyword);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        init(view);

        return view;
    }

    private void init(View view) {
        Activity parentActivity = getActivity();

        frame = (PtrFrameLayout) view.findViewById(R.id.ptr_frame);
        header = new MaterialHeader(parentActivity.getBaseContext());

        header.setPadding(0,20,0,20);
        header.setPtrFrameLayout(frame);


        frame.setLoadingMinTime(1000);
        frame.setDurationToCloseHeader(300);
        frame.setHeaderView(header);
        frame.addPtrUIHandler(header);

        frame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View view, View view2) {
                return mRecyclerView.getCurrentScrollY() == 0;
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout ptrFrameLayout) {
                //mRecyclerView.removeAllViews();
                mAdapter = new MyRecyclerAdapter(getActivity());
                mRecyclerView.setAdapter(mAdapter);
                mCurrentPage += 10;
                getNewsList(mAdapter, mCurrentPage, true);
                //ptrFrameLayout.refreshComplete();
            }
        });


        mRecyclerView = (ObservableRecyclerView) view.findViewById(R.id.my_recycler_view);

        mLayoutManager = new LinearLayoutManager(parentActivity);

        mRecyclerView.setScrollViewCallbacks((ObservableScrollViewCallbacks)parentActivity);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        NewsItem item = mAdapter.getmNewsList().get(position);

                        //打开显示新闻内容的Activity,把新闻的url作为参数传过去
                        Intent startActivityIntent = new Intent(getActivity(),NewsContentActivity.class);

                        view.setDrawingCacheEnabled(true);
                        view.setPressed(false);
                        view.refreshDrawableState();
                        Bitmap bitmap = view.getDrawingCache();
                        ActivityOptions options = ActivityOptions.makeThumbnailScaleUpAnimation(
                                view, bitmap, 0, 0);

                        Bundle urlBundle = new Bundle();
                        urlBundle.putString("news_URL",item.getSourceUrl());
                        urlBundle.putString("news_Title", item.getTitle());
                        urlBundle.putString("news_ID", item.getNews_id());
                        urlBundle.putString("news_Author", item.getOrigin());
                        urlBundle.putString("news_Picture", item.getImgsUrl());
                        urlBundle.putString("newsClassTag", item.getType());

                        //urlBundle.putString("newsClassTag",item.getType());
                        startActivityIntent.putExtra("key",urlBundle);
                        ActivityCompat.startActivity(getActivity(), startActivityIntent, options.toBundle());
                        mAdapter = new MyRecyclerAdapter(getActivity());
                        mRecyclerView.setAdapter(mAdapter);
                        getNewsList(mAdapter, mCurrentPage, true);
                    }
                })
            );
        //设置adapter
        mAdapter = new MyRecyclerAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        //得到数据
        getNewsList(mAdapter, mCurrentPage, true);

//        监听list滑动事件
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItemPositon = ((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition();
                int lastVisibleItemPositon = ((LinearLayoutManager)mLayoutManager).findLastVisibleItemPosition();
               // mAdapter.bindViewHolderData(firstVisibleItemPositon, lastVisibleItemPositon);
 //               int totalItem = mLayoutManager.getItemCount();
//                当剩下2个item时加载下一页
//                TODO 服务器有问题，type的更新以及max_news_id都需要进行调试
//                if(lastVisibleItem > totalItem - 2 && dy > 0){
//                    int loadPage= mNewsItems.get(mNewsItems.size()-1).getPageNumber() + 1;
//                    if (mCurrentPage < loadPage) {
//                        mCurrentPage = loadPage;
//                        getNewsList(mAdapter, mCurrentPage, false);
//                    }
//                }
            }
        });
}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNewsType = getArguments().getString(ARG_NEWS_TYPE);
            mNewsUrlType = getArguments().getString(ARG_NEWS_URL_TYPE);
            keywords = getArguments().getString(NEWS_ITEM_IS_SEARCH);
        }
        mCurrentPage = 0;
    }


    /**
     * 获取某一页的数据
     * @param adapter
     * @param currentPage 页码
     * @param forced      是否强制刷新
     */
    private void getNewsList(MyRecyclerAdapter adapter,int currentPage,boolean forced) {
        LoadNewsListTask loadDataTask = new LoadNewsListTask(adapter,mNewsType,mNewsUrlType,forced, keywords);
        loadDataTask.execute(currentPage);
    }



    /**
     * 加载新闻列表的任务
     *
     */
    class LoadNewsListTask extends AsyncTask<Integer, Integer,List<NewsItem> >{

        private MyRecyclerAdapter mAdapter;
        private boolean mIsForced;
        private String mNewsType;
        private String mNewsUrltype;
        private boolean netAvailable;
        private String keywords;

        public LoadNewsListTask(MyRecyclerAdapter adapter,String newsType, String newsUrltype, boolean forced, String keyword) {
            super();
            mAdapter = adapter;
            mIsForced = forced;
            mNewsType = newsType;
            mNewsUrltype = newsUrltype;
            keywords = keyword;
        }

        /**
         *得到当前页码的新闻列表
         * @param currentPage 当前页码
         * @return 当前页码的新闻列表,出错返回null
         */
        @Override
        protected List<NewsItem> doInBackground(Integer... currentPage) {

            try {

                netAvailable = HttpUtils.IsNetAvailable(getActivity());
                //netAvailable = false;
                Log.i("test-net-available", "doInBackground: netAvailable:" + netAvailable);
                if (!netAvailable){
                    //Toast.makeText(getActivity(),"没有网络，即将载入缓存..."
                           // ,Toast.LENGTH_LONG).show();
                    Log.i("NetInformation","65156156165156");
                    //if (keywords.equals("NotSearchType"))
                    List<NewsItem> newsItems = new ArrayList<NewsItem>();
                    newsItems = mNewsItemBiz.getNewsItemCache(mNewsUrltype, currentPage[0]);;
                    Log.i("newsItemInformation",newsItems.toString());
                    return newsItems;
//                    {
//                        List<NewsItem> newsItems = new ArrayList<NewsItem>();
//                        newsItems = mNewsItemBiz.getNewsItemCache(mNewsType, currentPage[0]);
//                        Log.i("newsitemseenyeah",newsItems.toString());
//                        return mNewsItemBiz.getNewsItemCache(mNewsType, currentPage[0]);
//                    }

                    //else return null;
                }
                else {
                    if (keywords.equals("NotSearchType"))
                        return mNewsItemBiz.getNewsItems(mNewsUrltype, currentPage[0]);
                    else
                        return mNewsItemBiz.getNewsItemsByKey(keywords, currentPage[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("ASDNET","neterror :"+e);
                return null;
            }

        }

        /**
         * 得到新闻列表后将其加载
         * @param newsItems 得到的新闻列表
         */
        @Override
        protected void onPostExecute(List<NewsItem> newsItems) {
            if (newsItems == null) {
                Toast.makeText(getActivity(),"获取失败，请检查网络重试0.0",Toast.LENGTH_SHORT).show();
                return;
            }
            if(mIsForced){
                mAdapter.getmNewsList().clear();
            }
            //Log.i("Iteminfomation",String.valueOf(newsItems.size()));
            mAdapter.addNews(newsItems);
            //mAdapter.setNews(newsItems);
            //mAdapter.setNewsItemDao(mNewsItemBiz.getmNewsItemDao());
            mAdapter.notifyDataSetChanged();
            frame.refreshComplete();
            if(netAvailable)
                Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.net_avaiable),Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(),"无网络连接，获取缓存成功",Toast.LENGTH_SHORT).show();
//            mNewsItems.addAll(newsItems);
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
    public String getNewsShowType() {

        if (mNewsType == null && getArguments() != null) {
            mNewsType = getArguments().getString(ARG_NEWS_TYPE);
        }
        return mNewsType;
    }

}
