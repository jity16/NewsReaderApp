package com.java.a5.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Http辅助工具类
 */
public class HttpUtils {

    private static final int TIMEOUT_IN_MILLIONS = 5000;

    /**
     * 用Get方法返回该链接地址的html数据
     *
     * @param urlStr    URL地址
     * @return  服务器返回的数据
     */
    public static String doGet(String urlStr) throws Exception
    {
        if (urlStr == ""){
            return null;
        }
        URL url;
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try
        {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
            conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            if (conn.getResponseCode() == 200)
            {
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                int len = -1;
                byte[] buf = new byte[128];

                while ((len = is.read(buf)) != -1)
                {
                    baos.write(buf, 0, len);
                }
                baos.flush();
                return baos.toString();
            } else
            {
                throw new RuntimeException(" responseCode is not 200 ... " + urlStr);
            }

        } catch (Exception e)
        {
            e.printStackTrace();

        } finally
        {
            try
            {
                if (is != null)
                    is.close();
            } catch (IOException e)
            {
            }
            try
            {
                if (baos != null)
                    baos.close();
            } catch (IOException e)
            {
            }
            conn.disconnect();
        }

        return null ;
    }

    /**
     * 当前是否有网络连接
     * @return
     */
    public static boolean IsNetAvailable(Context context){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Log.i("CheckedHttps",Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        //return (info != null && info.isConnected());
        return (info != null && info.isAvailable());

    }

}
