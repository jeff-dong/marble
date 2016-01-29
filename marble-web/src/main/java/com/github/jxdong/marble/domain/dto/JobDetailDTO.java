package com.github.jxdong.marble.domain.dto;

import com.github.jxdong.marble.domain.model.AppDetail;
import com.github.jxdong.marble.domain.model.SchedulerDetail;
import com.github.jxdong.marble.domain.model.enums.JobStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import java.util.Date;
import java.util.List;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/11/12 12:40
 */
public class JobDetailDTO extends BaseDTO{

    private AppDetail app;
    private SchedulerDetail scheduler;
    private String name;
    private String description;
    private String status;
    private String cronExpress;
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

    public JobDetailDTO(){

    }

    public JobDetailDTO(String name, String description, String cronExpress, Date nextFireTime, Date prevFireTime, Date startTime, Date endTime) {
        this.name = name;
        this.description = description;
        this.cronExpress = cronExpress;
        this.nextFireTime = nextFireTime;
        this.prevFireTime = prevFireTime;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    private JobDetail qJob;
    private List<Trigger> qTriggers;

    public JobDetail getqJob() {
        return qJob;
    }

    public void setqJob(JobDetail qJob) {
        this.qJob = qJob;
    }

    public List<Trigger> getqTriggers() {
        return qTriggers;
    }

    public void setqTriggers(List<Trigger> qTriggers) {
        this.qTriggers = qTriggers;
    }

    public AppDetail getApp() {
        return app;
    }

    public void setApp(AppDetail app) {
        this.app = app;
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

    public String getStatusDesc(){
        return JobStatusEnum.getItemByCode(status).getDesc();
    }

    public String getCronExpress() {
        return cronExpress;
    }

    public void setCronExpress(String cronExpress) {
        this.cronExpress = cronExpress;
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
