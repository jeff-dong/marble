package com.github.jxdong.marble.agent.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2016/1/14 17:31
 */
public class MarbleRequest implements Serializable {

    //请求流水号
    private String requestNo;
    //请求时间
    private Date requestTime;

    //是否需要响应，默认不需要
    private boolean needResponse = false;

    private Map<String, Object> data;

    public MarbleRequest(String requestNo, Map<String, Object> data,boolean needResponse) {
        this.needResponse = needResponse;
        this.requestNo = requestNo;
        this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }
    public boolean isNeedResponse() {
        return needResponse;
    }

    public void setNeedResponse(boolean needResponse) {
        this.needResponse = needResponse;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

