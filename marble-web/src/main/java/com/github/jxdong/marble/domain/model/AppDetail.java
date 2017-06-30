package com.github.jxdong.marble.domain.model;

import java.util.List;

/**
 * 应用信息
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/11 12:58
 */
public class AppDetail extends Entity{

    private String code;
    private String name;
    private String description;
    //应用联系人-员工号
    private String owner;
    //应用状态. 0:不可用；1:可用
    private int status;
    //使用的marble版本号
    private String marbleVersion;
    //SOA服务名
    private String soaServiceName;
    //SOA服务namespace
    private String soaServiceNameSpace;

    //app下的主机信息
    private List<ServerDetail> hosts;
    //app下的scheduler信息
    private List<SchedulerDetail> schedulers;
    public AppDetail(){
    }

    public AppDetail(String code){
        this.code = code;
    }

    public List<SchedulerDetail> getSchedulers() {
        return schedulers;
    }

    public void setSchedulers(List<SchedulerDetail> schedulers) {
        this.schedulers = schedulers;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getSoaServiceName() {
        return soaServiceName;
    }

    public void setSoaServiceName(String soaServiceName) {
        this.soaServiceName = soaServiceName;
    }

    public String getSoaServiceNameSpace() {
        return soaServiceNameSpace;
    }

    public void setSoaServiceNameSpace(String soaServiceNameSpace) {
        this.soaServiceNameSpace = soaServiceNameSpace;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<ServerDetail> getHosts() {
        return hosts;
    }

    public void setHosts(List<ServerDetail> hosts) {
        this.hosts = hosts;
    }

    public String getMarbleVersion() {
        return marbleVersion;
    }

    public void setMarbleVersion(String marbleVersion) {
        this.marbleVersion = marbleVersion;
    }
}
