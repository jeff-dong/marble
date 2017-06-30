package com.github.jxdong.marble.domain.model;

import com.github.jxdong.marble.common.util.ArrayUtils;
import com.github.jxdong.marble.common.util.StringUtils;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 计划任务信息
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/11 13:03
 */
public class SchedulerDetail extends Entity{
    private static Logger logger = LoggerFactory.getLogger(SchedulerDetail.class);

    private AppDetail appDetail;
    private List<ServerDetail> serverDetails;
    private String name;
    private String description;
    private int status;
    private List<JobDetail> jobs;

    //辅助信息 - Quartz的Scheduler对象
    private Scheduler scheduler;

    public SchedulerDetail(){

    }

    public boolean validateParamForInsert(){
        return !(appDetail == null || StringUtils.isBlank(appDetail.getCode()) || StringUtils.isBlank(name) || ArrayUtils.listIsBlank(serverDetails));
    }

    public SchedulerDetail(String name) {
        this.name = name;
    }

    public void shutdown(boolean waitJobtoComplete){
        try{
            if(this.getScheduler() != null && !this.getScheduler().isShutdown()){
                this.getScheduler().shutdown(waitJobtoComplete);
            }
        }catch (Exception e){
            logger.error("shutdown scheduler exception. Scheduler: {}, detail: ", scheduler, e);
        }

    }

    public boolean isLegal(){
        return (StringUtils.isNotBlank(name) && appDetail != null && StringUtils.isNotBlank(appDetail.getCode()) && ArrayUtils.listIsNotBlank(serverDetails));
    }

    public AppDetail getAppDetail() {
        return appDetail;
    }

    public void setAppDetail(AppDetail appDetail) {
        this.appDetail = appDetail;
    }

    public List<ServerDetail> getServerDetails() {
        return serverDetails;
    }

    public void setServerDetails(List<ServerDetail> serverDetails) {
        this.serverDetails = serverDetails;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<JobDetail> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobDetail> jobs) {
        this.jobs = jobs;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}
