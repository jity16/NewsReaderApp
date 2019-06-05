package com.java.a5.ui;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.java.a5.R;
import com.mob.MobSDK;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import cn.sharesdk.onekeyshare.OnekeyShare;


/**
 *基础Activity
 */
public class BaseActivity extends ActionBarActivity {

    protected Toolbar mToolbar;
    SystemBarTintManager mTintManager;


    protected final int CURRENT_VERSION = Build.VERSION.SDK_INT;
    protected final int VERSION_KITKAT = Build.VERSION_CODES.KITKAT;
    protected final int VERSION_LOLLIPOP = Build.VERSION_CODES.LOLLIPOP;
//    protected NewsItemBiz mNewsItemBiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mNewsItemBiz = new NewsItemBiz(this);

        //使用tintManager设置状态栏的颜色
        mTintManager= new SystemBarTintManager(this);
        // enable status bar tint
        //mTintManager.setStatusBarTintEnabled(true);
        if (isNavBarTransparent()) {
            mTintManager.setStatusBarTintEnabled(true);
            // 有虚拟按键时
            if (isHasNavigationBar()) {
                mTintManager.setNavigationBarTintEnabled(true);
            }else{
                mTintManager.setNavigationBarTintEnabled(false);
            }
        }

        // set a custom tint color for all system bars
        mTintManager.setTintColor(getResources().getColor(R.color.dark_primary_color));

//        SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 根据int得出对应的图标状态
     * @param state
     * @return 图标状态，默认为汉堡型
     */
   protected MaterialMenuDrawable.IconState intToState(int state){
       switch (state){
           case 0:
               return MaterialMenuDrawable.IconState.BURGER;
           case 1:
               return MaterialMenuDrawable.IconState.ARROW;
           case 2:
               return MaterialMenuDrawable.IconState.X;
           case 3:
               return MaterialMenuDrawable.IconState.CHECK;
       }

       return MaterialMenuDrawable.IconState.BURGER;
   }

    /**
     * 是否将导航栏以及状态栏设为透明（API大于19小与21)
     * @return
     */
    protected boolean isNavBarTransparent(){
        return CURRENT_VERSION >= VERSION_KITKAT && VERSION_LOLLIPOP > CURRENT_VERSION;
    }

    /**
     * 获得当前系统版本号
     * @return
     */
   protected int getVersionNumber(){
       return Build.VERSION.SDK_INT;
   }

    protected static int dp2px(float dpValue){
        return (int)(dpValue * Resources.getSystem().getDisplayMetrics().density);
    }

    protected static int px2dp(float pxValue){
        return (int)(pxValue / Resources.getSystem().getDisplayMetrics().density);
    }

    protected int getScreenWidth(){
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    protected int getScreenHeight(){
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.no_anim,R.anim.activity_slide_out);
//        );
    }


    /**
     * 获取状态栏的高度
     * @return
     */
    protected int getStatusBarHeight(){
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height","dimen","android");
        if (resourceId>0){
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取navigation bar的高度
     * @return
     */
    protected int getNavigationBarHeight(){
        int result = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height","dimen","android");
        if (resourceId>0){
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 检查是否有虚拟按键
     * @return
     */
    protected boolean isHasNavigationBar(){
        //通过是否有物理按键来确定是否有虚拟按键
        boolean hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        return !(hasBackKey && hasMenuKey);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void replaceFragment(int viewId, android.app.Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(viewId, fragment).commit();
    }

    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    /**
     * 分享
     */
    protected void showShare(Context context,String text, String title, String imagePath, String imageUrl, String url) {
        MobSDK.init(this);

        String appHomePage = "https://cloud.tsinghua.edu.cn/f/10102e5727614979ac22/";
        String shareText = text != "" ? text : "\n分享自News";

        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字
        //oks.setNotification(R.drawable.ic_news, getString(R.string.app_name));

        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle(title);
        // titleUrl QQ和QQ空间跳转链接
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        Log.d("inshowshare1", url+" "+ title+" "+text);
        //设置图片链接
        if(imageUrl == null)
            oks.setImagePath(imagePath);//确保SDcard下面存在此张图片
        if(imagePath == null)
            oks.setImageUrl(imageUrl);
        // url仅在微信（包括好友和朋友圈）中使用
        //oks.setUrl(url);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url在微信、微博，Facebook等平台中使用
        oks.setUrl(url);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(appHomePage);
        // 启动分享GUI
        oks.show(this);
    }

}
