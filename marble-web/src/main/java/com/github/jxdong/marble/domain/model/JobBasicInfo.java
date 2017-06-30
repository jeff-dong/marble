package com.github.jxdong.marble.domain.model;

import com.github.jxdong.marble.common.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/17 13:48
 */
public class JobBasicInfo implements Serializable{

    private String appCode;
    private String schedName;
    private String jobName;
    private String jobDesc;
    private String jobCronExpress;
    private String param;
    private String serverIp;
    private String marbleVersion;
    private int serverPort;

    //执行流水号
    private String executeReqNumber;
    //最长等待时间
    private Long maxWaitTime;
    //是否同步JOB
    @JsonProperty("isSynchronous")
    private boolean isSynchronous;

    public JobBasicInfo() {
    }

    public JobBasicInfo(String appCode, String schedName, String jobName, String jobDesc, String jobCronExpress, String param, String marbleVersion, boolean isSunc, Long maxWaitTime) {
        this.appCode = appCode;
        this.schedName = schedName;
        this.jobName = jobName;
        this.jobDesc = jobDesc;
        this.jobCronExpress = jobCronExpress;
        this.param = param;
        this.marbleVersion = marbleVersion;
        this.isSynchronous = isSunc;
        this.maxWaitTime = maxWaitTime;
    }

    public JobBasicInfo(String appCode, String schedName, String jobName, String jobDesc, String jobCronExpress, String param, String serverIp, int serverPort) {
        this.appCode = appCode;
        this.schedName = schedName;
        this.jobName = jobName;
        this.jobDesc = jobDesc;
        this.jobCronExpress = jobCronExpress;
        this.param = param;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public Long getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(Long maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }
    @JsonIgnore
    public boolean isSynchronous() {
        return isSynchronous;
    }

    public void setIsSynchronous(boolean isSynchronous) {
        this.isSynchronous = isSynchronous;
    }

    public String getExecuteReqNumber() {
        return executeReqNumber;
    }

    public void setExecuteReqNumber(String executeReqNumber) {
        this.executeReqNumber = executeReqNumber;
    }

    public String getMarbleVersion() {
        return marbleVersion;
    }

    public void setMarbleVersion(String marbleVersion) {
        this.marbleVersion = marbleVersion;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

//    public JobExecutionLog convert2JobLog(JobExecutionException e){
//        JobExecStatusEnum statusEnum = JobExecStatusEnum.SUCCESS;
//        String detailMsg = null;
//        if(e != null){
//            statusEnum = JobExecStatusEnum.FAILURE;
//            detailMsg = e.getMessage();
//            if(detailMsg.length() > 450){
//                detailMsg = detailMsg.substring(0, 450) + " ...";
//            }
//        }
//        return new JobExecutionLog(executeReqNumber, appCode, schedName, jobName,jobCronExpress, StringUtils.safeString(serverIp)+":"+serverPort, statusEnum, detailMsg);
//    }
    @JsonIgnore
    public String getLogName(){
        return StringUtils.safeString(appCode) + "-" + StringUtils.safeString(schedName) + "-" +StringUtils.safeString(jobName) + (executeReqNumber!=null?"("+executeReqNumber+")":"");
    }

    @JsonIgnore
    public String getServiceName(){
        return StringUtils.safeString(schedName) + "-" + StringUtils.safeString(appCode) + "-" +StringUtils.safeString(jobName);
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
