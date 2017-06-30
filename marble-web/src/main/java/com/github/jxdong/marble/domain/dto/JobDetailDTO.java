package com.github.jxdong.marble.domain.dto;

import com.github.jxdong.marble.common.util.StringUtils;
import com.github.jxdong.marble.domain.model.SchedulerDetail;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.jxdong.marble.domain.model.JobDetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/12 12:40
 */
public class JobDetailDTO extends BaseDTO{

    //最长等待时间
    private Long maxWaitTime;
    //是否同步JOB
    private boolean isSynchronous;
    private String cronExpress;
    private SchedulerDetail scheduler;
    private String name;
    private String description;
    private String status;
    private String param;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date nextFireTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date prevFireTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date endTime;
    private int misfireStrategy;
    private String otherInfo;

    public JobDetailDTO(){

    }

    public static List<JobDetailDTO> convertJobDetail(List<JobDetail> jobDetails){
        List<JobDetailDTO> jobDetailDTOs = new ArrayList<>();
        if(jobDetails != null){
            for(JobDetail jd : jobDetails){
                JobDetailDTO dto = new JobDetailDTO();
                dto.setName(jd.getName());
                dto.setCronExpress(jd.getCronExpress());
                dto.setDescription(jd.getDescription());
                dto.setIsSynchronous(jd.isSynchronous());
                dto.setParam(jd.getParam());
                dto.setStartTime(jd.getStartTime());
                dto.setEndTime(jd.getEndTime());
                dto.setNextFireTime(jd.getNextFireTime());
                dto.setStatus(jd.getStatus());
                dto.setPrevFireTime(jd.getPrevFireTime());
                dto.setMaxWaitTime(jd.getMaxWaitTime());
                dto.setStatus(jd.getStatus());
                jobDetailDTOs.add(dto);
            }
        }

        return jobDetailDTOs;
    }

    public String getStatusDesc(){
        String statusdesc = status;
        if(StringUtils.isBlank(status)){
            return statusdesc;
        }
        switch (status){
            case "NORMAL": statusdesc = "正常 ["+status+"]"; break;
            case "BLOCKED": statusdesc = "阻塞 ["+status+"]"; break;
            case "ACQUIRED": statusdesc = "执行 ["+status+"]"; break;
            case "PAUSED": statusdesc = "暂停 ["+status+"]"; break;
            case "WAITING": statusdesc = "等待 ["+status+"]"; break;
            case "ERROR": statusdesc = "错误 ["+status+"]"; break;
            default: break;

        }
        return statusdesc;
    }

    public Long getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(Long maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public boolean isSynchronous() {
        return isSynchronous;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public void setIsSynchronous(boolean isSynchronous) {
        this.isSynchronous = isSynchronous;
    }

    public String getCronExpress() {
        return cronExpress;
    }

    public void setCronExpress(String cronExpress) {
        this.cronExpress = cronExpress;
    }

    public SchedulerDetail getScheduler() {
        return scheduler;
    }

    public void setScheduler(SchedulerDetail scheduler) {
        this.scheduler = scheduler;
    }

    public String getName() {
        return name;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public Date getPrevFireTime() {
        return prevFireTime;
    }

    public void setPrevFireTime(Date prevFireTime) {
        this.prevFireTime = prevFireTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getMisfireStrategy() {
        return misfireStrategy;
    }

    public void setMisfireStrategy(int misfireStrategy) {
        this.misfireStrategy = misfireStrategy;
    }
}
