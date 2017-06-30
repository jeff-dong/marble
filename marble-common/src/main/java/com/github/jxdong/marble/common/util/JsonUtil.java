package com.github.jxdong.marble.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2016/8/10 16:07
 */
public class JsonUtil {


    public static <T> T parseObject(String text, Class<T> clazz) {
        T re = null;
        try{
            re = JSON.parseObject(text, clazz, new Feature[0]);
        }catch (Exception e){}
        return re;
    }

    public static JSONObject parseObject(String str){
        JSONObject jsonObject = null;
        try{
            if(StringUtils.isNotBlank(str)){
                jsonObject = JSON.parseObject(str);
            }
        }catch (Exception e){
        }
        return jsonObject;
    }

    public static JSONArray parseArray(String str){
        JSONArray jsonArray = null;
        try{
            if(StringUtils.isNotBlank(str)){
                jsonArray = JSON.parseArray(str);
            }
        }catch (Exception e){
        }
        return jsonArray;
    }

    public static String toJsonString(Object object){
        String jsonStr = null;
        try{
            if(object !=null){
                jsonStr = JSON.toJSONString(object);
            }
        }catch (Exception e){
        }
        return jsonStr;
    }

    //
    public static String toJsonStringWithExclude(Object object, String ... fields){
        String jsonStr = null;
        try{
            if(object !=null){
                SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
                if(fields != null && fields.length>0){
                    for(String field : fields){
                        filter.getExcludes().add(field);
                    }
                }
                jsonStr = JSON.toJSONString(object, filter);
            }
        }catch (Exception e){
        }
        return jsonStr;

    }
}
