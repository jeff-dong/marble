package com.github.jxdong.marble.domain.dto;

import com.github.jxdong.marble.domain.model.JobExecutionLog;
import com.github.jxdong.marble.global.util.DateUtil;

import java.util.Date;

/**
 * Created by ccsa on 22/06/2017.
 */
public class JobExecResponse {

    private String appCode;
    private String schedName;
    private String jobName;
    private Integer resultCode;
    private Integer requestResult;
    private Integer execResult;

    private String resultMsg;
    private String beginTime;
    private String endTime;

    private String jobTransNo;
    //同步标志, true同步 false异步
    private Boolean asyncFlag;

    public JobExecResponse() {
    }

    public JobExecResponse(Integer resultCode, Integer requestResult, Integer execResult, String resultMsg, String beginTime, String endTime, Boolean asyncFlag, String jobTransNo, String appCode, String schedName, String jobName) {
        this.resultCode = resultCode;
        this.requestResult = requestResult;
        this.execResult = execResult;
        this.resultMsg = resultMsg;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.jobTransNo = jobTransNo;
        this.appCode = appCode;
        this.schedName = schedName;
        this.asyncFlag = asyncFlag;
        this.jobName = jobName;
    }

    public static JobExecResponse FAILURE(String appCode, String schedName, String jobName, Integer errorCode, String errorMsg) {
        return new JobExecResponse(errorCode, null, null, errorMsg, null, null,null, null, appCode, schedName, jobName);
    }

    public static JobExecResponse SYNC_SUCCESS(String appCode, String schedName, String jobName, Boolean asyncFlag, String transNo) {
        return new JobExecResponse(0, 0, null, "JOB调度请求成功发送", DateUtil.formateDate(new Date()), null,asyncFlag, transNo, appCode, schedName, jobName);
    }

    public static JobExecResponse SUCCESS(JobExecutionLog log, Boolean asyncFlag) {
        return new JobExecResponse(
                0,
                log.getReqResultCode(),
                log.getExecResultCode(),
                "查询成功",
                DateUtil.formateDate(log.getBeginTime()),
                DateUtil.formateDate(log.getEndTime()),
                asyncFlag,
                log.getRequestNo(),
                log.getAppCode(),
                log.getSchedName(),
                log.getJobName());
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public Integer getRequestResult() {
        return requestResult;
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

    public void setRequestResult(Integer requestResult) {
        this.requestResult = requestResult;
    }

    public Integer getExecResult() {
        return execResult;
    }

    public void setExecResult(Integer execResult) {
        this.execResult = execResult;
    }

    public String getJobTransNo() {
        return jobTransNo;
    }

    public Boolean getAsyncFlag() {
        return asyncFlag;
    }

    public void setJobTransNo(String jobTransNo) {
        this.jobTransNo = jobTransNo;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
