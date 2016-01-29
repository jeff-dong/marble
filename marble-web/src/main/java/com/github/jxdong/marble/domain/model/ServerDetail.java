package com.github.jxdong.marble.domain.model;

import com.github.jxdong.common.util.StringUtils;

/**
 * 主机信息
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/11/11 13:01
 */
public class ServerDetail extends Entity{

    private String appCode;
    private String group;
    private String name;
    private String ip;
    private String description;
    private int status;
    private int port;

    private String schedName;

    public boolean validateParamForInsert(){
        //参数校验
        return !(StringUtils.isBlank(this.getAppCode()) ||
                StringUtils.isBlank(this.getGroup()) ||
                StringUtils.isBlank(this.getName()) ||
                StringUtils.isBlank(this.getIp()));
    }

    public String getSchedName() {
        return schedName;
    }

    public void setSchedName(String schedName) {
        this.schedName = schedName;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
