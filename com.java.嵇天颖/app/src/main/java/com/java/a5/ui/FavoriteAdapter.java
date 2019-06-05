package com.java.a5.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.java.a5.R;
import com.java.a5.bean.FavoriteItem;

import java.util.List;

/**
 * Created by wuhanghang on 16-9-3.
 */

public class FavoriteAdapter extends BaseAdapter {

    private Context context;
    List<FavoriteItem> items;//适配器的数据源


    public FavoriteAdapter(Context context,List<FavoriteItem> list){
        this.context = context;
        this.items = list;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FavoriteAdapter.ViewHolder viewHolder;
        if(convertView==null){
            viewHolder = new FavoriteAdapter.ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_favorite, null);
            viewHolder.title = (TextView) convertView.findViewById(R.id.favoriteTitle);
            viewHolder.origin = (TextView) convertView.findViewById(R.id.favoriteOrigin);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (FavoriteAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(items.get(position).getTitle());
        viewHolder.origin.setText(items.get(position).getOrigin());

        return convertView;
    }

    class ViewHolder {
        TextView title;
        TextView origin;
    }
}

