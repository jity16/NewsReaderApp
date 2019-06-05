package com.java.a5;

import android.app.Application;

/**
 * Created by wangmz15 on 2017/9/11.
 */

public class QuickNews  extends Application{
    //文字模式
    public boolean ImageMode;
    public boolean getImageMode(){return ImageMode;}
    public void setImageMode(boolean _ImageMode){
        ImageMode = _ImageMode;
    }

}
