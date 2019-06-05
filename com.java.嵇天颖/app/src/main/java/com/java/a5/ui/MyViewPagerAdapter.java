package com.java.a5.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.java.a5.ui.fragments.NewsListFragment;

import java.util.List;

/**
 *
 */
public class MyViewPagerAdapter extends FragmentPagerAdapter {

    List<NewsListFragment> mList;
    public MyViewPagerAdapter(FragmentManager fm, List<NewsListFragment> list) {
        super(fm);
        mList = list;
    }

    @Override
    public Fragment getItem(int i) {
        return mList.get(i);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Log.i("viewPageAdapter", "getPageTitle:[" + position + "] " + (CharSequence)(mList.get(position).getNewsShowType()));
        return mList.get(position).getNewsShowType();
    }
}
