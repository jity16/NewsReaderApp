package com.java.a5.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * APP通用工具
 */
public class CommonUtils {
    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
