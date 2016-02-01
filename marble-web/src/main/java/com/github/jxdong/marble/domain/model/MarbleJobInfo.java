package com.github.jxdong.marble.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jxdong.common.util.ArrayUtils;
import com.github.jxdong.common.util.CommonUtil;
import com.github.jxdong.common.util.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/11/17 13:48
 */
@JsonIgnoreProperties({"serviceName"})
public class MarbleJobInfo implements Serializable{

    //JOB基本信息
    private String appCode;
    private String schedName;
    private String jobName;
    private String jobDesc;
    private String jobCronExpress;
    private String jobParam;
    private String marbleVersion;

    private List<MarbleServerInfo> serverInfoList;

    public MarbleJobInfo(String appCode, String schedName, String jobName, String jobDesc, String jobCronExpress, String jobParam, String marbleVersion, List<MarbleServerInfo> serverInfoList) {
        this.appCode = appCode;
        this.schedName = schedName;
        this.jobName = jobName;
        this.jobDesc = jobDesc;
        this.jobCronExpress = jobCronExpress;
        this.jobParam = jobParam;
        this.marbleVersion = marbleVersion;
        this.serverInfoList = serverInfoList;
    }

    public MarbleJobInfo() {
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getSchedName() {
        return schedName;
    }

    public void setSchedName(String schedName) {
        this.schedName = schedName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getJobCronExpress() {
        return jobCronExpress;
    }

    public void setJobCronExpress(String jobCronExpress) {
        this.jobCronExpress = jobCronExpress;
    }

    public String getJobParam() {
        return jobParam;
    }

    public void setJobParam(String jobParam) {
        this.jobParam = jobParam;
    }

    public String getMarbleVersion() {
        return marbleVersion;
    }

    public void setMarbleVersion(String marbleVersion) {
        this.marbleVersion = marbleVersion;
    }

    public List<MarbleServerInfo> getServerInfoList() {
        return serverInfoList;
    }

    public void setServerInfoList(List<MarbleServerInfo> serverInfoList) {
        this.serverInfoList = serverInfoList;
    }

    //取得服务名称
    @JsonIgnore
    public String getServiceName(){
        if(StringUtils.isNotBlank(schedName) && StringUtils.isNotBlank(appCode) && StringUtils.isNotBlank(jobName)){
            return schedName + "-"+ appCode +"-" + jobName;
        }
        return null;
    }

    /**
     * 获取一个随机的server，用于建立连接
     * @return MarbleServerInfo
     */
    @JsonIgnore
    public MarbleServerInfo getConnectServerRandomly(){
        MarbleServerInfo serverInfo = null;

        if(ArrayUtils.listIsNotBlank(serverInfoList)){
            //取得随机数
            Integer randomIndex[] = CommonUtil.randomCommon(0, serverInfoList.size(), 1);
            if(randomIndex != null){
                serverInfo = serverInfoList.get(randomIndex[0]);
            }
        }

        return serverInfo;
    }
}

