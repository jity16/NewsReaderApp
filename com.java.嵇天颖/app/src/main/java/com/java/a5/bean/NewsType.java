package com.java.a5.bean;

import com.j256.ormlite.field.DatabaseField;

public class NewsType implements Comparable{
    @DatabaseField(id = true)
    private String urlType;
    @DatabaseField
    private String showType;
    @DatabaseField
    private int showOrder; //whether user like read this type
    @DatabaseField
    private boolean isExist;    //最新一次中是否存在

    public String getUrlType() {
        return urlType;
    }

    public void setUrlType(String urlType) {
        this.urlType = urlType;
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }


    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }


    public int getShowOrder() {
        return showOrder;
    }

    public void setShowOrder(int showOrder) {
        this.showOrder = showOrder;
    }

    @Override
    public int compareTo(Object o) {
        NewsType newsType=(NewsType)o;
        if(this.showOrder>newsType.getShowOrder()){
            return 1;
        }
        else if(this.showOrder<newsType.getShowOrder()){
            return -1;
        }
        return 0;
    }
}
