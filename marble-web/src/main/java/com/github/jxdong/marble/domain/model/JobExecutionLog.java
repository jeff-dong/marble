package com.github.jxdong.marble.domain.model;

import com.github.jxdong.common.util.StringUtils;
import com.github.jxdong.marble.domain.model.enums.JobExecStatusEnum;

/**
 * JOB执行日志
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/11/17 14:24
 */
public class JobExecutionLog extends Entity{

    private String appCode;
    private String schedName;
    private String jobName;
    private String jobCronExpress;
    private String serverInfo;
    private int resultCode;
    private String resultMsg;

    public JobExecutionLog(){

    }
    public JobExecutionLog(String appCode, String schedName, String jobName, String jobCronExpress, String serverInfo, JobExecStatusEnum statusEnum, String detail) {
        this.appCode = appCode;
        this.schedName = schedName;
        this.jobName = jobName;
        this.jobCronExpress = jobCronExpress;
        this.serverInfo = serverInfo;
        this.resultCode = statusEnum.getCode();
        this.resultMsg = statusEnum.getDesc() + (StringUtils.isNotBlank(detail)?". Detail Info: "+detail:"");
    }

    public int getResultCode() {
        return resultCode;
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

    public String getJobCronExpress() {
        return jobCronExpress;
    }

    public void setJobCronExpress(String jobCronExpress) {
        this.jobCronExpress = jobCronExpress;
    }

    public String getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
    }

}
