package com.java.a5.utils;

/**
 * Created by wuhanghang on 16-9-2.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    /**
     * sd卡的根目录
     */
    private static String mSdRootPath = Environment.getExternalStorageDirectory().getPath();
    /**
     * 手机的缓存根目录
     */
    private static String mDataRootPath = null;
    /**
     * 保存Image的目录名
     */
    private final static String FOLDER_NAME = "/NewsCacheImage";


    public FileUtils(Context context){
        mDataRootPath = context.getCacheDir().getPath();
    }


    /**
     * 获取储存Image的目录
     * @return
     */
    private String getStorageDirectory(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                mSdRootPath + FOLDER_NAME : mDataRootPath + FOLDER_NAME;
    }

    /**
     * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录
     * @param fileName
     * @param bitmap
     * @throws IOException
     */
    public void savaBitmap(String fileName, Bitmap bitmap) throws IOException{
        if(bitmap == null){
            return;
        }
        String path = getStorageDirectory();
        File folderFile = new File(path);
        if(!folderFile.exists()){
            folderFile.mkdir();
        }
        File file = new File(path + File.separator + fileName);
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
        Log.i("FileUtils", "savaBitmapToFile: " + path + File.separator + fileName);
    }
    public String imageSaveAs(InputStream in, String destFileName) throws IOException {
        String path = getStorageDirectory();
        File folderFile = new File(path);
        if(!folderFile.exists()){
            folderFile.mkdir();
        }
        String fileName = path + File.separator + destFileName;
        File file = new File(fileName);
        if(file.exists())   {
            Log.i("FileUtils", "imageSaveAs: has Exist: " + file.getAbsolutePath());
            return fileName;
        }
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);// 指定要写入的图片
        int n = 0;// 每次读取的字节长度
        byte[] bb = new byte[1024];// 存储每次读取的内容
        while ((n = in.read(bb)) != -1) {
            out.write(bb, 0, n);// 将读取的内容，写入到输出流当中
        }
        out.close();// 关闭输入输出流
        in.close();
        Log.i("FileUtils", "imageSaveAs: " + file.getAbsolutePath());
        return fileName;
    }

    /**
     * 从手机或者sd卡获取Bitmap
     * @param fileName
     * @return
     */
    public Bitmap getBitmap(String fileName){
        return BitmapFactory.decodeFile(getStorageDirectory() + File.separator + fileName);
    }

    /**
     * 判断文件是否存在
     * @param fileName
     * @return
     */
    public boolean isFileExists(String fileName){
        return new File(getStorageDirectory() + File.separator + fileName).exists();
    }

    /**
     * 获取文件的大小
     * @param fileName
     * @return
     */
    public long getFileSize(String fileName) {
        return new File(getStorageDirectory() + File.separator + fileName).length();
    }


    /**
     * 删除SD卡或者手机的缓存图片和目录
     */
    public void deleteFile() {
        File dirFile = new File(getStorageDirectory());
        if(! dirFile.exists()){
            return;
        }
        if (dirFile.isDirectory()) {
            String[] children = dirFile.list();
            for (int i = 0; i < children.length; i++) {
                Log.i("FileUtils", "deleteFile: " + dirFile + File.separator + children[i]);
                new File(dirFile, children[i]).delete();
            }
        }

        dirFile.delete();
    }
    public String getUrlPathName(String url) {
        return url.replaceAll("[^\\w]", "");
    }
}