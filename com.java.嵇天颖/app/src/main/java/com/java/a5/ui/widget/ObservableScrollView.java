package com.java.a5.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 附带监听滑动的ScrollView
 */
public class ObservableScrollView extends ScrollView {

    private OnScrollChangedListener mOnScrollChangedListener;
    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    public void setOnScrollListener(OnScrollChangedListener onScrollChangedListener) {
        this.mOnScrollChangedListener = onScrollChangedListener;
    }


    public interface OnScrollChangedListener{
        public void onScrollChanged(int x, int y, int oldX, int oldY);
    }
}
