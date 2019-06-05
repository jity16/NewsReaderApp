package com.java.a5.utils;

/**
 * 相关API
 */
public class NewsAPIUtils {

    public static int count = 0;
    public static String TypeUrl = "0:推荐,1:新闻,2:体育,3:博客,4:科技,5:财经,6:军事,7:女性,8:时尚,9:汽车,10:娱乐,11:读书,12:文化,";
    public static String XinWen[]={"news/china/focus15.xml","news/world/focus15.xml","news/society/focus15.xml"};
    public static String TiYu[] = {"roll/sports/hot_roll.xml","news/allnews/sports.xml","sports/global/focus.xml"};
    public static String BoKe[] = {"blog/index/exc.xml","blog/index/cul.xml","blog/index/feel.xml"};
    public static String KeJi[] = {"tech/rollnews.xml","news/allnews/tech.xml","tech/internet/home28.xml"};
    public static String CaiJing[] = {"roll/finance/hot_roll.xml","news/allnews/finance.xml","news/allnews/finance.xml"};
    public static String JunShi[] = {"roll/mil/hot_roll.xml","jczs/focus.xml","jczs/taiwan20.xml"};
    public static String NvXing[] = {"news/allnews/eladies.xml","eladies/marry.xml","eladies/gossip.xml"};
    public static String ShiShang[] ={"fashion/all/news.xml","fashion/style/news.xml","fashion/beauty/news.xml"};
    public static String QiChe[] ={"auto/news/t/index.xml","auto/guide/index.xml","auto/newcar/index.xml"};
    public static String YuLe[] ={"ent/inner.xml","ent/hongkong.xml","ent/star/japan.xml"};
    public static String DuShu[] ={"book/info.xml","book/info.xml","book/info.xml"};
    public static String WenHua[] ={"edu/focus19.xml","edu/exam.xml","edu/gaokao.xml"};

    public static int TuiJian[]={0,0,0,0,0,0,0,0,0,0,0,0};

    public static String Url[][]={XinWen,TiYu,BoKe,KeJi,CaiJing,JunShi,NvXing,ShiShang,QiChe,YuLe,DuShu,WenHua};
    public static String getTypeUrl() {
        return TypeUrl;
        //return "http://assignment.crazz.cn/news/en/category?timestamp=" + System.currentTimeMillis();
    }

//    public static String getNewsUrl(String newsType,int maxNewsID){
        //TODO 为了兼容所有带currentPage的函数调用,这里先进行重定位
//        return "http://assignment.crazz.cn/news/query?locale=en&category=" + newsType + "&max_news_id=" + maxNewsID;
//        return getNewsUrl(newsType);
//    }
    public static String getNewsUrl(String newsType){
        TuiJian[Integer.parseInt(newsType)-1]++;
        count++;
        return "http://rss.sina.com.cn/"+Url[Integer.parseInt(newsType)-1][count % 3];
    }

    public static int[] getTuiNewsUrl(){
        return TuiJian;
    }

    public static String getNewsUrlByKey(String keywords, int currrntpage){
        return "http://166.111.68.66:2042/news/action/query/search?keyword=" + keywords;
    }


}
