package com.github.jxdong.common.util;

import java.util.List;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/11/11 14:07
 */
public class ArrayUtils {

    public static boolean listIsNotBlank(List list){
        return (list != null && list.size()>0);
    }

    public static boolean listIsBlank(List list){
        return (list == null || list.size()==0);
    }
}
