package com.github.jxdong.marble.agent.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2016/1/14 17:31
 */
public class MarbleResponse implements Serializable {

    //原请求流水号
    private String requestNo;
    //响应时间
    private Date responseTime;
    //响应码
    private String responseCode;
    //响应描述
    private String resonseMsg;
    //响应结果信息
    private String responseInfo;

    public boolean isSuccess(){
        return "0000".equals(responseCode);
    }

    public MarbleResponse(String requestNo, ResultCodeEnum resultCodeEnum, String detail) {
        this.requestNo = requestNo;
        this.responseTime = new Date();
        this.responseCode = resultCodeEnum.getCode();
        this.resonseMsg = resultCodeEnum.getDesc();
        this.responseInfo = detail;
    }

    public MarbleResponse(ResultCodeEnum resultCodeEnum, String detail) {
        this.responseTime = new Date();
        this.responseCode = resultCodeEnum.getCode();
        this.resonseMsg = resultCodeEnum.getDesc();
        this.responseInfo = detail;
    }

    public MarbleResponse(Date responseTime, String responseCode, String resonseMsg, String responseInfo) {
        this.responseTime = responseTime;
        this.responseCode = responseCode;
        this.resonseMsg = resonseMsg;
        this.responseInfo = responseInfo;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public Date getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResonseMsg() {
        return resonseMsg;
    }

    public void setResonseMsg(String resonseMsg) {
        this.resonseMsg = resonseMsg;
    }

    public String getResponseInfo() {
        return responseInfo;
    }

    public void setResponseInfo(String responseInfo) {
        this.responseInfo = responseInfo;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}