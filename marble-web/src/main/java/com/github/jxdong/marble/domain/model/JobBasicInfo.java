package com.github.jxdong.marble.domain.model;

import com.github.jxdong.common.util.StringUtils;
import com.github.jxdong.marble.domain.model.enums.JobExecStatusEnum;
import org.quartz.JobExecutionException;

import java.io.Serializable;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
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

    public JobBasicInfo() {
    }

    public JobBasicInfo(String appCode, String schedName, String jobName, String jobDesc, String jobCronExpress, String param, String marbleVersion) {
        this.appCode = appCode;
        this.schedName = schedName;
        this.jobName = jobName;
        this.jobDesc = jobDesc;
        this.jobCronExpress = jobCronExpress;
        this.param = param;
        this.marbleVersion = marbleVersion;
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

    public JobExecutionLog convert2JobLog(JobExecutionException e){
        JobExecStatusEnum statusEnum = JobExecStatusEnum.SUCCESS;
        String detailMsg = null;
        if(e != null){
            statusEnum = JobExecStatusEnum.FAILURE;
            detailMsg = e.getMessage();
            if(detailMsg.length() > 450){
                detailMsg = detailMsg.substring(0, 450) + " ...";
            }
        }
        return new JobExecutionLog(appCode, schedName, jobName,jobCronExpress, StringUtils.safeString(serverIp)+":"+serverPort, statusEnum, detailMsg);
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
}
