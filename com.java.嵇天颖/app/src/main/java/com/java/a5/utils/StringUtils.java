package com.java.a5.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    /**
     * 利用正则表达式提取字符串中的数字
     * @param str 被提取的字符串
     * @return 提取的数字
     */
    public static int getIntFromString(String str){
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return Integer.parseInt(m.replaceAll("").trim());
    }

    /**
     * 利用正则表达式提取字符串中的日期
     * @param str 被提取的字符串
     * @return 提取的日期，格式为YYYY-MM-DD
     */
    public static String getDateFromString(String str){
        String regEx="[0-9]{4}-[0-9]{2}-[0-9]{2}";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        if (m.find()){
            return m.group();
        }
        return null;
    }
    //without replacing space
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
//            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static String replaceNme(String str) {
        String dest = "";
        if (str!=null) {
//            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Pattern p = Pattern.compile("newsClassTag");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("category");
        }
        return dest;
    }

    public static String replaceBlankAndSpace(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
    public static String getRightJsonSyntax(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\"imgs\":\\[\\{");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("\"imgs\":\\{");

            p = Pattern.compile("\\}\\],\"locale_category");
            m = p.matcher(dest);
            dest = m.replaceAll("\\},\"locale_category");
        }
        return dest;
    }
}
