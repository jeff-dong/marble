package com.github.jxdong.marble.server.spring;

import java.util.List;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/8/20 16:34
 */
public class SchedulerBeanConfig {

    private String id;
    // private String name;
    private String host;
    private int appCode;
    private int port;
    private List<JobBeanConfig> jobs;

    public int getAppCode() {
        return appCode;
    }

    public void setAppCode(int appCode) {
        this.appCode = appCode;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<JobBeanConfig> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobBeanConfig> jobs) {
        this.jobs = jobs;
    }
}
