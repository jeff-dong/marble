package com.github.jxdong.marble.common.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/6/11 14:52
 */
public class CommonUtil {

    /**
     * 判断list是否为空
     * @param list list
     * @return boolean
     */
    public static boolean listIsNotBlank(List list){
        if(list != null && list.size() >0){
            return true;
        }
        return false;
    }

    public static String[] removeArrayBlank(String[] originalArray){
        List<String> newArray = new ArrayList<String>();
        for(String str : originalArray){
            if(StringUtils.isNotBlank(str)){
                newArray.add(str);
            }
        }
        return newArray.toArray(new String[0]);
    }

    public static boolean dateEqual(Date date1, Date date2){
        boolean isEqual = false;
        if(date1 == null && date2 == null){
            return true;
        }
        if(date1 == null || date2 == null){
            return false;
        }else{
            return (date1.compareTo(date2) == 0);
        }
    }

    public static String object2Str(Object obj){
        if(obj == null){
            return null;
        }else{
            return String.valueOf(obj);
        }
    }

    public static String generateUUID(){
        UUID id = UUID.randomUUID();
        return id.toString();
    }

    /**
     * 随机指定范围内N个不重复的数
     * @param min 指定范围最小值
     * @param max 指定范围最大值
     * @param n 随机数个数
     */
    public static Integer[] randomCommon(int min, int max, int n){
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        Integer[] result = new Integer[n];
        int count = 0;
        while(count < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            //看是否重复
            if(result.length >0){
                for (int j = 0; j < n; j++) {
                    if(result[j] == null){
                        continue;
                    }
                    if(num == result[j]){
                        flag = false;
                        break;
                    }
                }
            }
            if(flag){
                result[count] = num;
                count++;
            }
        }
        return result;
    }

    /**
     * 得到混淆的安全字符串，比如要混淆2/3位的字符，传参2,3
     * 举例：
     *  传参： origStr = "12345"; numerator = 1; denominator =3; (混淆12345的三分之一个字符)
     *  预期结果：1*****5
     * @param numerator   分子
     * @param denominator 分母
     * @return
     */
    public static String confuseString(String origStr, int numerator, int denominator) {
        StringBuffer safeStr = new StringBuffer("*****");
        if (StringUtils.isNotBlank(origStr) && denominator > numerator && numerator>0) {
            //转化为BigDicemal便于精确计算
            int strLength = origStr.length();
            if (true) {//strLength > denominator
                int startLength = (int) Math.floor(new BigDecimal(String.valueOf(strLength * (denominator - numerator))).divide(new BigDecimal(String.valueOf(denominator * 2)), 2, BigDecimal.ROUND_HALF_UP).doubleValue());
                int endLength = (int) Math.ceil(
                        new BigDecimal(String.valueOf(strLength * numerator)).divide(new BigDecimal(String.valueOf(denominator)),2,BigDecimal.ROUND_HALF_UP).doubleValue()
                );
                safeStr.insert(0, origStr.substring(0, startLength));
                safeStr.append(origStr.substring((startLength + endLength), strLength));
            }
        }
        return safeStr.toString();
    }

}
