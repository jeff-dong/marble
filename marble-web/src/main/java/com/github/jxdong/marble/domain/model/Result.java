package com.github.jxdong.marble.domain.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.Map;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/8/26 14:48
 */
public class Result {

    //0成功，其它失败
    private int resultCode;
    private String resultMsg;

    private Map<String ,Object> data;

    public static Result SUCCESS(){
        Result result = new Result();
        result.setResultCode(0);
        result.setResultMsg("ok");
        return result;
    }

    public static Result SUCCESS(Map<String ,Object> data){
        Result result = new Result();
        result.setResultCode(0);
        result.setResultMsg("ok");
        result.setData(data);
        return result;
    }

    public static Result FAILURE(String errorMsg){
        Result result = new Result();
        result.setResultCode(1);
        result.setResultMsg(errorMsg);
        return result;
    }

    public int getResultCode() {
        return resultCode;
    }

    public boolean isSuccess(){
        if(this.resultCode == 0){
            return true;
        }
        return false;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("resultCode", resultCode).
                append("resultMsg", resultMsg).append("data", data).toString();
    }
}
