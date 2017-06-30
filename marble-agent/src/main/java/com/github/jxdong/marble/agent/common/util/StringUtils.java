package com.github.jxdong.marble.agent.common.util;

import org.apache.commons.lang.StringEscapeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

public class StringUtils {


    public static boolean isStringEqual(String str1, String str2) {
        if(str1 == null && str2 == null){
            return true;
        }
        if(str1 != null && str2 != null){
            return str1.compareTo(str2) == 0;
        }
        return false;
    }

    public static String[] escapeInvalidCharForParam(String[] params) {
        for (int i = 0; i < params.length; i++) {
            params[i] = StringEscapeUtils.escapeJavaScript(params[i]);
            params[i] = StringEscapeUtils.escapeSql(params[i]);
            params[i] = StringEscapeUtils.escapeHtml(params[i]);
        }
        return params;
    }

    /**
     * 判断字符串是否为Null或空白
     *
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        if (str == null || str.length() < 1)
            return true;

        return false;
    }

    public static boolean isNotBlank(String str) {
        return org.apache.commons.lang.StringUtils.isNotBlank(str);
    }

    public static boolean isBlank(String str) {
        return org.apache.commons.lang.StringUtils.isBlank(str);
    }

    /**
     * 判断字符串是否为Null或者全部是空格、Tab的内容
     *
     * @param str
     * @return
     */
    public static boolean isNullOrWhiteSpace(String str) {
        if (str == null || str.length() < 1)
            return true;

        for (int i = 0; i < str.length(); i++) {
            if (!(str.charAt(i) == ' ' && str.charAt(i) == '\t'))
                return false;
        }
        return true;
    }

    /**
     * 把参数 obj 转换为安全字符串：如果 obj = null，则把它转换为空字符串
     */
    public final static String safeString(Object obj) {
        if (obj == null)
            return "";

        return obj.toString();
    }


    /**
     * 把参数 obj 转换为安全字符串：如果 obj = null，则把它转换为空字符串
     */
    public static String safeString(Object obj, int length) {
        String safeStr = safeString(obj);
        return (safeStr.length() > length) ? safeStr.substring(0, length - 3) + "..." : safeStr;
    }

    /**
     * 检查字符串是否符合整数格式
     */
    public final static boolean isStrNumeric(String str) {
        if (str == null || str.trim().length() <= 0) {
            return false;
        }
        return Pattern.compile("^0$|^\\-?[1-9]+[0-9]*$").matcher(str).matches();
    }

    public static Date string2Date(String str) {
        Date date = null;
        if (str != null && str.trim().length() > 0) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                date = sdf.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    public static Date string2Date(String str, String formate) {
        Date date = null;
        if (str != null && str.trim().length() > 0 && formate != null && formate.trim().length() > 0) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(formate);
                date = sdf.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    /**
     * String -> int，如果转换不成功则返回默认值 d
     */
    public static int str2Int(String str, int defaultValue) {
        int returnVal;
        try {
            if (str != null)
                str = str.trim();
            returnVal = Integer.parseInt(str);
        } catch (Exception e) {
            returnVal = defaultValue;
        }
        return returnVal;
    }

    //生成唯一UUID
    public static String genUUID() {

        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }

}
