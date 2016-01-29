package com.github.jxdong.marble.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2016/1/14 17:31
 */
public class MarbleRequest implements Serializable{

    //请求时间
    private Date requestTime;
    //要调用的class对象列表
    private Set<ClassInfo> classes;
    //是否需要响应，默认不需要
    private boolean needResponse = false;

    public MarbleRequest(Set<ClassInfo> classes, boolean needResponse) {
        this.requestTime = new Date();
        this.classes = classes;
        this.needResponse = needResponse;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Set<ClassInfo> getClasses() {
        return classes;
    }

    public void setClasses(Set<ClassInfo> classes) {
        this.classes = classes;
    }

    public boolean isNeedResponse() {
        return needResponse;
    }

    public void setNeedResponse(boolean needResponse) {
        this.needResponse = needResponse;
    }

    @Override
    public String toString() {
        return "MarbleRequest{" +
                "requestTime=" + (requestTime==null?"":requestTime.toString()) +
                ", classes=" + (classes==null?"":classes.toString()) +
                ", needResponse=" + needResponse +
                '}';
    }
}

