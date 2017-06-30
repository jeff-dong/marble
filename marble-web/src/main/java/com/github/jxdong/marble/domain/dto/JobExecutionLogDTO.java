package com.github.jxdong.marble.domain.dto;

import com.github.jxdong.marble.domain.model.enums.JobExecStatusEnum;
import com.github.jxdong.marble.global.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * JOB执行日志
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/17 14:24
 */
public class JobExecutionLogDTO extends BaseDTO{

    private String requestNo;
    private String appCode;
    private String schedName;
    private String jobName;
    private String jobCronExpress;
    private String serverInfo;
    private int reqResultCode;
    private String reqResultMsg;
    private int execResultCode;
    private String execResultMsg;
    private String otherInfo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date beginTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date endTime;

    public JobExecutionLogDTO() {
    }

    public String getConsumingTime(){
        if(beginTime != null && endTime != null){
            return String.valueOf(DateUtil.getBetween(beginTime, endTime, DateUtil.YYYYMMDDHHMMSS, DateUtil.SECOND_RETURN));
        }
        return "";
    }
    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
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

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public int getExecResultCode() {
        return execResultCode;
    }

    public void setExecResultCode(int execResultCode) {
        this.execResultCode = execResultCode;
    }

    public String getExecResultMsg() {
        return execResultMsg;
    }

    public void setExecResultMsg(String execResultMsg) {
        this.execResultMsg = execResultMsg;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    public String getReqStatusDesc(){
        JobExecStatusEnum statusEnum = JobExecStatusEnum.getItemByCode(reqResultCode);
        return statusEnum==null?"未知":statusEnum.getDesc();
    }
    public String getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
    }

    public int getReqResultCode() {
        return reqResultCode;
    }

    public void setReqResultCode(int reqResultCode) {
        this.reqResultCode = reqResultCode;
    }

    public String getReqResultMsg() {
        return reqResultMsg;
    }

    public void setReqResultMsg(String reqResultMsg) {
        this.reqResultMsg = reqResultMsg;
    }
}
