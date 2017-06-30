package com.github.jxdong.marble.domain.model;

import com.github.jxdong.marble.common.util.StringUtils;
import com.github.jxdong.marble.domain.model.enums.JobExecStatusEnum;
import com.github.jxdong.marble.domain.model.enums.JobReqStatusEnum;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.Date;

/**
 * JOB执行日志
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/17 14:24
 */
public class JobExecutionLog extends Entity{

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
    private Date beginTime;
    private Date endTime;
    private String otherInfo;

    public JobExecutionLog(){

    }
    public JobExecutionLog(String requestNo, String appCode, String schedName, String jobName, String jobCronExpress, String serverInfo, JobExecStatusEnum statusEnum, String detail, String otherInfo) {
       this.requestNo = requestNo;
        this.appCode = appCode;
        this.schedName = schedName;
        this.jobName = jobName;
        this.jobCronExpress = jobCronExpress;
        this.serverInfo = serverInfo;
        this.reqResultCode = statusEnum.getCode();
        this.reqResultMsg = statusEnum.getDesc() + (StringUtils.isNotBlank(detail)?". Detail Info: "+detail:"");
        this.otherInfo = otherInfo;
    }

    public JobExecutionLog(String requestNo, String appCode, String schedName, String jobName, String jobCronExpress, String serverInfo, JobReqStatusEnum reqStatusEnum, String detail,
                           String otherInfo,
                           int execResultCode,
                           String execResultMsg) {
        this.requestNo = requestNo;
        this.appCode = appCode;
        this.schedName = schedName;
        this.jobName = jobName;
        this.jobCronExpress = jobCronExpress;
        this.serverInfo = serverInfo;
        this.reqResultCode = reqStatusEnum.getCode();
        this.reqResultMsg = reqStatusEnum.getDesc() + (StringUtils.isNotBlank(detail)?". Detail Info: "+detail:"");
        this.otherInfo = otherInfo;
        this.execResultCode = execResultCode;
        this.execResultMsg = execResultMsg;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
