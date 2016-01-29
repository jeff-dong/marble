package com.github.jxdong.marble.domain.model;

import com.github.jxdong.common.util.JacksonUtil;
import com.github.jxdong.common.util.StringUtils;
import com.github.jxdong.marble.server.thrift.ThriftConnectInfo;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.Trigger;

import java.util.Date;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/11/11 13:07
 */
public class JobDetail extends Entity {

    private AppDetail app;
    private SchedulerDetail scheduler;
    private String name;
    private String description;
    private String status;
    private String cronExpress;
    private String triggerDesc;
    private String param;
    private Date nextFireTime;
    private Date prevFireTime;
    private Date startTime;
    private Date endTime;
    private int misfireStrategy;

    private String className;
    private ThriftConnectInfo connectInfo;

    public boolean validateParamForInsert(){
        return !(app==null || StringUtils.isBlank(app.getCode()) ||
                scheduler == null || StringUtils.isBlank(scheduler.getName()) ||
                StringUtils.isBlank(name) || StringUtils.isBlank(cronExpress) || !CronExpression.isValidExpression(cronExpress));
    }

    public boolean validateParamForUpdate(){
        return !(app==null || StringUtils.isBlank(app.getCode()) ||
                scheduler == null || StringUtils.isBlank(scheduler.getName()) ||
                StringUtils.isBlank(name));
    }

    public JobDetail(){

    }

    public JobDetail(org.quartz.JobDetail qJob, Trigger trigger){
        if(qJob != null){
            this.name = qJob.getKey().getName();
            this.description = qJob.getDescription();
            this.className = qJob.getJobClass().getName();
            //DataMap数据提取
            JobDataMap dataMap = qJob.getJobDataMap();
            if(dataMap != null ){
                if(dataMap.get("THRIFT_CONNECT_INFO")!=null){
                    this.connectInfo = JacksonUtil.json2pojo(dataMap.get("THRIFT_CONNECT_INFO").toString(), ThriftConnectInfo.class);
                }
                if(dataMap.get("JOB_INFO")!=null){
                    JobBasicInfo jobBasicInfo = JacksonUtil.json2pojo(dataMap.get("JOB_INFO").toString(), JobBasicInfo.class);
                    if(jobBasicInfo != null){
                        this.param = jobBasicInfo.getParam();
                    }
                }
            }
            if(trigger != null){
                this.triggerDesc = trigger.getDescription();
                this.nextFireTime = trigger.getNextFireTime();
                this.prevFireTime = trigger.getPreviousFireTime();
                this.startTime = trigger.getStartTime();
                this.endTime = trigger.getEndTime();
                if(trigger instanceof CronTrigger){
                    this.cronExpress =  ((CronTrigger) trigger).getCronExpression();
                }
                this.misfireStrategy = trigger.getMisfireInstruction();
            }
        }
    }

    public String getClassName() {
        return className;
    }

    public ThriftConnectInfo getConnectInfo() {
        return connectInfo;
    }

    public void setConnectInfo(ThriftConnectInfo connectInfo) {
        this.connectInfo = connectInfo;
    }

    public String getTriggerDesc() {
        return triggerDesc;
    }

    public void setTriggerDesc(String triggerDesc) {
        this.triggerDesc = triggerDesc;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
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
