package com.java.a5.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.java.a5.QuickNews;
import com.java.a5.R;
import com.java.a5.bean.FavoriteItem;
import com.java.a5.bean.NewsItemSeen;
import com.java.a5.dao.FavoriteItemDao;
import com.java.a5.dao.NewsItemSeenDao;
import com.java.a5.ui.widget.GestureFrameLayout;
import com.java.a5.utils.HttpUtils;

import org.json.JSONObject;

import java.sql.SQLException;

public class NewsContentActivity extends BaseActivity {


    //文字模式
    QuickNews quickNews;

    private int mActionBarSize;

    private int mToolbarColor;


    private String newsForSpeech;
    private String jsonStr;
    private JSONObject json;
    private GestureFrameLayout gestureFrameLayout;  //滑动返回
    private WebView mWebView;
    private TextView mTextView;
    private FavoriteItemDao mFavoriteItemDao;
    private NewsItemSeenDao mNewsItemSeenDao;

    //作语音合成用:
    private final String TAG = "NewsContentActivity";
    private SpeechSynthesizer mTts;
    private String voicer = "xiaoyan";
    private String[] mCloudVoicersEntries;
    private String[] mCloudVoicersValue ;

    // 缓冲进度
    private int mPercentForBuffering = 0;
    // 播放进度
    private int mPercentForPlaying = 0;
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    private Toast mToast;
    private SharedPreferences mSharedPreferences;
    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        quickNews = (QuickNews)getApplication();

        //将看过的新闻加入数据库
        mNewsItemSeenDao = new NewsItemSeenDao(NewsContentActivity.this);
        String newsId = this.getIntent().getBundleExtra("key").getString("news_ID");
        String title = this.getIntent().getBundleExtra("key").getString("news_Title");
        String origin = this.getIntent().getBundleExtra("key").getString("news_Author");
        String sourceUrl =  this.getIntent().getBundleExtra("key").getString("news_URL");
        String newsClaggTag =  this.getIntent().getBundleExtra("key").getString("newsClassTag");
        NewsItemSeen newsItemSeen = new NewsItemSeen();
        newsItemSeen.setNewsId(newsId);
        newsItemSeen.setTitle(title);
        //newsItemSeen.setOrigin(origin);
        newsItemSeen.setSourceUrl(sourceUrl);
        newsItemSeen.setNewsClassTag(newsClaggTag);
        mNewsItemSeenDao.createOrUpdate(newsItemSeen);



        SpeechUtility.createUtility(NewsContentActivity.this, SpeechConstant.APPID + "=59b3804b");
        initTtsObject();//初始化语音合成对象
        Log.d("initTts","Null"+String.valueOf((mTts==null)));

        setParamsOfTts();//设置语音合成对象参数

        initSpeechText(sourceUrl);


//        if (quickNews.getImageMode()) {
//            setContentView(R.layout.activity_news_content);
//            init();
//            //通过bundle获取文章内容的url
//            String mNewsContentUrl = this.getIntent().getBundleExtra("key").getString("news_URL");
//            //mNewsContentUrl = "https://www.baidu.com";
//            mWebView.loadUrl(mNewsContentUrl);
//        }
//        else {
//            setContentView(R.layout.activity_news_content_text);
//            System.out.println("newsForSpeech = " + newsForSpeech);
//            init_textView(newsForSpeech);
//        }
    }

    private void init() {
        mWebView = (WebView) findViewById(R.id.content_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDefaultFontSize(18);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //设置点击网页里面的链接还是在当前的webview里跳转
                Log.i("mymymyurl",url);
                view.loadUrl(url);
                return true;
            }
        });
        mFavoriteItemDao = new FavoriteItemDao(NewsContentActivity.this);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_18dp));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsContentActivity.this.finish();
                String caller = NewsContentActivity.this.getIntent().getBundleExtra("key").getString("caller");
                if(caller != null && caller.equals("FavoriteActivity")) {
                    Intent intent = new Intent(NewsContentActivity.this, FavoriteActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//刷新
                    startActivity(intent);
                }
            }
        });
        mToolbarColor = getResources().getColor(R.color.primary_color);

        mActionBarSize = getActionBarSize();

        gestureFrameLayout = (GestureFrameLayout) findViewById(R.id.news_content_gesture_layout);
        gestureFrameLayout.attachToActivity(this);

        //因为顶栏透明，要让出顶栏和底栏空间
        if (isNavBarTransparent()) {
            gestureFrameLayout.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight());
        }
    }

    private void init_textView(String text){
        mTextView = (TextView) findViewById(R.id.textview);
        mTextView.setText(text);
//        mTextView = new TextView(this);
        mFavoriteItemDao = new FavoriteItemDao(NewsContentActivity.this);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_18dp));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsContentActivity.this.finish();
                String caller = NewsContentActivity.this.getIntent().getBundleExtra("key").getString("caller");
                if(caller != null && caller.equals("FavoriteActivity")) {
                    Intent intent = new Intent(NewsContentActivity.this, FavoriteActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//刷新
                    startActivity(intent);
                }
            }
        });
        mToolbarColor = getResources().getColor(R.color.primary_color);

        mActionBarSize = getActionBarSize();

        gestureFrameLayout = (GestureFrameLayout) findViewById(R.id.news_content_gesture_layout);
        gestureFrameLayout.attachToActivity(this);

        //因为顶栏透明，要让出顶栏和底栏空间
        if (isNavBarTransparent()) {
            gestureFrameLayout.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight());
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();//返回webView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        String newsId = this.getIntent().getBundleExtra("key").getString("news_ID");
        try {
            if(mFavoriteItemDao.searchIsExistByNewsId(newsId))
                getMenuInflater().inflate(R.menu.menu_news_content_favorite, menu);
            else
                getMenuInflater().inflate(R.menu.menu_news_content, menu);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Log.d("in showshare","in showshare");
            String title = this.getIntent().getBundleExtra("key").getString("news_Title");
            String url =  this.getIntent().getBundleExtra("key").getString("news_URL");
            String origin =  this.getIntent().getBundleExtra("key").getString("news_Author");
            String imageUrl =  this.getIntent().getBundleExtra("key").getString("news_Picture");
            if(imageUrl == null)
                imageUrl = getResources().getString(R.string.app_icon_url);
            Log.d("url", url);
            showShare(this, origin , title, null, imageUrl, url);
            return true;
        }
        else if(id == R.id.action_favorite) {
            if(item.getTitle().equals("common")) { //不在收藏列表里面
                item.setTitle("favorite");
                item.setIcon(R.drawable.ic_favorite_red);
                String newsId = this.getIntent().getBundleExtra("key").getString("news_ID");
                String title = this.getIntent().getBundleExtra("key").getString("news_Title");
                String origin = this.getIntent().getBundleExtra("key").getString("news_Author");
                String sourceUrl =  this.getIntent().getBundleExtra("key").getString("news_URL");
                FavoriteItem favoriteItem = new FavoriteItem();
                favoriteItem.setNewsId(newsId);
                favoriteItem.setTitle(title);
                favoriteItem.setOrigin(origin);
                favoriteItem.setSourceUrl(sourceUrl);
                mFavoriteItemDao.createOrUpdate(favoriteItem);
            }
            else if(item.getTitle().equals("favorite")) { //在收藏列表里面
                item.setTitle("common");
                item.setIcon(R.drawable.ic_favorite_white);
                String newsId = this.getIntent().getBundleExtra("key").getString("news_ID");
                try {
                    mFavoriteItemDao.deleteByNewsId(newsId);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (id == R.id.action_speak)
        {
            speak();
        }
        else if (id == R.id.action_shutup)
        {
            mTts.stopSpeaking();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initTtsObject()
    {
        Log.d("initTts","begin");
        mTts = SpeechSynthesizer.createSynthesizer(NewsContentActivity.this, mTtsInitListener);
        Log.d("initTts","Null"+String.valueOf((mTts==null)));
        //Log.d("NewsContent",())
    }
    private void setParamsOfTts()
    {
                 // 清空参数
            mTts.setParameter(SpeechConstant.PARAMS, null);

        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        //  mTts.setParameter(SpeechConstant.ENGINE_MODE,SpeechConstant.)
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
//
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
       // mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        //boolean isSuccess = mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts2.wav");

    }
    private  void speak()
    {
        int code = mTts.startSpeaking(newsForSpeech, mTtsListener);

        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                //上面的语音配置对象为初始化时：
                Toast.makeText(NewsContentActivity.this, "语音组件未安装", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(NewsContentActivity.this, "语音合成失败,错误码: " + code, Toast.LENGTH_LONG).show();
            }
        }
    }
    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码："+code);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            showTip("开始播放");
        }

        @Override
        public void onSpeakPaused() {
            showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
            mPercentForBuffering = percent;
            showTip(String.format(getString(R.string.tts_toast_format),
                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            mPercentForPlaying = percent;
            showTip(String.format(getString(R.string.tts_toast_format),
                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                showTip("播放完成");
            } else if (error != null) {
                showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if( null != mTts ){
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
    }

    private void initSpeechText(final String newsID)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //jsonStr = HttpUtils.doGet("http://166.111.68.66:2042/news/action/query/detail?newsId="+newsID);
                    jsonStr = HttpUtils.doGet(newsID);
//                    json = new JSONObject(jsonStr);
//
//// String responseData = response.body().string();
//                    // Log.d("MainActivity","HELLO");
//                    // Log.d("MainActivity",responseData);
//                    Log.d("jsonStr",json.get("news_Title").toString());
//                    Log.d("jsonStr",json.get("news_Content").toString());
//                    newsForSpeech = json.get("news_Title").toString()+json.get("news_Content").toString();
//                    System.out.println("in init newsForSpeech = " + newsForSpeech);
                    parseSpeechText(newsForSpeech);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
    private void parseSpeechText(String newsText)
    {
        Message message = new Message();
        message.what = 1;
        handler.sendMessage(message);
    }

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (quickNews.getImageMode()) {
                        setContentView(R.layout.activity_news_content);
                        init();
                        mWebView.loadUrl(getIntent().getBundleExtra("key").getString("news_URL"));

                    }
                    else {

                        setContentView(R.layout.activity_news_content_text);
                        init_textView(newsForSpeech);
                    }
                    break;

            }
        }
    };

}
class NewsObjectForSpeech
{
    String news_Title;
    String news_Content;
}