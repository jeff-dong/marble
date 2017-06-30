package com.github.jxdong.marble.common.util;

import java.util.List;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
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
