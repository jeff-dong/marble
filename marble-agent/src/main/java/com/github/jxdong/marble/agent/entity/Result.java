package com.github.jxdong.marble.agent.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.github.jxdong.marble.agent.common.util.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/8/26 14:48
 */
public class Result implements Serializable {

    //0成功，其它失败
    private int resultCode;
    private String resultMsg;

    private Map<String ,Object> otherInfo = new ConcurrentHashMap<>();

    public Result() {
    }

    public void putData(String key, Object value){
        if(this.otherInfo == null){
            this.otherInfo = new ConcurrentHashMap<>();
        }
        if(StringUtils.isNotBlank(key) && value!=null){
            this.otherInfo.put(key, value);
        }
    }

    public static Result SUCCESS(){
        Result result = new Result();
        result.setResultCode(0);
        result.setResultMsg("成功");
        return result;
    }

    public static Result SUCCESS(String msg){
        Result result = new Result();
        result.setResultCode(0);
        result.setResultMsg(msg);
        return result;
    }

    public static Result PROCESSING(){
        Result result = new Result();
        result.setResultCode(10);
        result.setResultMsg("处理中");
        return result;
    }


    public static Result SUCCESS(Map<String ,Object> data){
        Result result = new Result();
        result.setResultCode(0);
        result.setResultMsg("成功");
        result.setOtherInfo(data);
        return result;
    }

    public static Result FAILURE(String errorMsg){
        return FAILURE(errorMsg, null);
    }

    public static Result FAILURE(int code, String errorMsg){
        Result result = new Result();
        result.setResultCode(code);
        result.setResultMsg(errorMsg);
        return result;
    }

    public static Result FAILURE(String errorMsg, Map<String ,Object> data){
        Result result = new Result();
        result.setResultCode(20);
        result.setResultMsg(errorMsg);
        result.setOtherInfo(data);
        return result;
    }
    public int getResultCode() {
        return resultCode;
    }

    public boolean isSuccess(){
        return this.resultCode == 0;
    }

    public Map<String, Object> getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(Map<String, Object> otherInfo) {
        this.otherInfo = otherInfo;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    @JSONField(serialize = false)
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
