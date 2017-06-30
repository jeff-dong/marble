package com.github.jxdong.marble.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.Map;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/8/26 14:48
 */
public class Result {

    //0成功，其它失败
    private int resultCode;
    private String resultMsg;

    private Map<String ,Object> otherInfo;

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
        result.setOtherInfo(data);
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

    @JsonIgnore
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
