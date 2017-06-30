package com.github.jxdong.marble.infrastructure.service;

import com.github.jxdong.marble.common.util.ArrayUtils;
import com.github.jxdong.marble.common.util.CommonUtil;
import com.github.jxdong.marble.common.util.JacksonUtil;
import com.github.jxdong.marble.common.util.StringUtils;
import com.github.jxdong.marble.domain.model.MarbleJobProxy;
import com.github.jxdong.marble.domain.model.Result;
import com.github.jxdong.marble.domain.model.enums.MisfireInstructionEnum;
import com.github.jxdong.marble.global.listener.MarbleJobListener;
import com.github.jxdong.marble.domain.model.JobBasicInfo;
import com.github.jxdong.marble.agent.common.server.thrift.ThriftConnectInfo;
import org.quartz.*;
import org.quartz.JobDetail;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

import static org.quartz.CronScheduleBuilder.cronSchedule;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/12/22 20:05
 */
@Component
public class QuartzManager {
    private static Logger logger = LoggerFactory.getLogger(QuartzManager.class);

    @Autowired
    private Scheduler marbleScheduler;

    /**
     * Job查询。 如果schedName为空，查询appCode下的所有jobs。如果jobName为空，查询appSode+schedName下所有jobs
     *
     * @param appCode   appCode
     * @param schedName scheduler Name
     * @param jobName   jobName
     * @return List
     */
    public List<com.github.jxdong.marble.domain.model.JobDetail> queryJobs(String appCode, String schedName, String jobName) {
        if (StringUtils.isBlank(appCode)) {
            return null;
        }
        List<com.github.jxdong.marble.domain.model.JobDetail> jobs = new ArrayList<>();
        try {
            //查询某个group下的jobs
            Set<JobKey> jobKeySet = new HashSet<>();
            //根据jobName精确查询
            if (StringUtils.isNotBlank(schedName) && StringUtils.isNotBlank(jobName)) {
                jobKeySet.add(new JobKey(jobName, genJobGroupName(appCode, schedName)));
            } else if (StringUtils.isNotBlank(schedName)) {
                //查询appCode + scheduler Name下的所有Jobs
                jobKeySet = marbleScheduler.getJobKeys(GroupMatcher.<JobKey>groupEquals(genJobGroupName(appCode, schedName)));
            } else {
                //查询appCode下的所有Jobs
                jobKeySet = marbleScheduler.getJobKeys(GroupMatcher.<JobKey>jobGroupStartsWith(appCode + "-"));
            }
            if (jobKeySet != null && jobKeySet.size() > 0) {
                for (JobKey jobKey : jobKeySet) {
                    JobDetail jobDetail = marbleScheduler.getJobDetail(jobKey);
                    if (jobDetail != null) {
                        //查询triggers
                        List<? extends Trigger> triggers = marbleScheduler.getTriggersOfJob(jobKey);
                        Trigger trigger = ArrayUtils.listIsNotBlank(triggers) ? triggers.get(0) : null;//取第一个
                        if (trigger != null) {
                            com.github.jxdong.marble.domain.model.JobDetail jobDetailT = new com.github.jxdong.marble.domain.model.JobDetail(jobDetail, trigger);
                            Trigger.TriggerState state = marbleScheduler.getTriggerState(trigger.getKey());
                            jobDetailT.setStatus(state.name());
                            jobs.add(jobDetailT);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("query jobs exception. ", e);
        }

        return jobs;
    }

    /**
     * 当前Scheduler中添加job
     *
     * @param appCode     应用code
     * @param schedName   scheduler name
     * @param jobName     jobName
     * @param cronExpress cronExpress
     * @return result
     */
    @Transactional
    public Result addJob(String appCode,
                         String schedName,
                         String jobName,
                         String jobDesc,
                         String cronExpress,
                         int misFireStrategy,
                         String param,
                         List<ThriftConnectInfo.Server> serverList,
                         String marbleVersion,
                         boolean isSync,
                         Long maxWaitTime) {
        Result result = validateArguments(appCode, schedName, jobName);
        if (!result.isSuccess()) {
            return result;
        }
        if (!CronExpression.isValidExpression(cronExpress)) {
            return Result.FAILURE("Cron表达式非法");
        }
        if (!CommonUtil.listIsNotBlank(serverList)) {
            return Result.FAILURE("服务器信息为空");
        }
        if (!MisfireInstructionEnum.containItem(misFireStrategy)) {
            return Result.FAILURE("MisFire策略不合法");
        }
        if(isSync && (maxWaitTime==null || maxWaitTime <= 0)){
            return Result.FAILURE("同步类型的JOB必须填写'最大等待时间'");
        }

        try {
            //创建Job Detail
            String jobGroupName = genJobGroupName(appCode, schedName);
            JobDetail jobDetail = marbleScheduler.getJobDetail(new JobKey(jobName, jobGroupName));
            if (jobDetail != null) {
                logger.warn("Create Job ignored. Because the job with key({}) has exist.", jobGroupName + "-" + jobName);
            } else {
                jobDetail = JobBuilder.newJob(MarbleJobProxy.class).withIdentity(jobName, jobGroupName).withDescription(jobDesc).storeDurably(true).build();
            }
            //Job将连接信息填入Data Map
            jobDetail.getJobDataMap().put("THRIFT_CONNECT_INFO", JacksonUtil.obj2json(new ThriftConnectInfo(appCode, schedName, jobName, serverList)));
            jobDetail.getJobDataMap().put("JOB_INFO", JacksonUtil.obj2json(new JobBasicInfo(appCode, schedName, jobName, jobDesc, cronExpress, param, marbleVersion, isSync, maxWaitTime)));

            //创建Trigger
            String triggerGroupName = genTriggerGroupName(appCode, schedName);
            Trigger trigger = marbleScheduler.getTrigger(new TriggerKey(triggerGroupName, genTriggerNameForJob(jobName)));
            if (trigger != null) {
                logger.warn("Create Job Trigger ignored. Because the trigger with key({}) has exist.", triggerGroupName + "-" + genTriggerNameForJob(jobName));
            } else {
                trigger = TriggerBuilder.newTrigger().
                        withIdentity(genTriggerNameForJob(jobName), triggerGroupName).
                        withSchedule(genCronScheduleBuilder(cronExpress, misFireStrategy)).build();
            }

            //绑定Job与Trigger
            marbleScheduler.scheduleJob(jobDetail, trigger);
            //暂停job
            marbleScheduler.pauseJob(jobDetail.getKey());
            //marbleScheduler.getListenerManager().addJobListener(new MarbleJobListener(), KeyMatcher.keyEquals(jobDetail.getKey()));
            return Result.SUCCESS();
        } catch (Exception e) {
            logger.error("Create Job Detail exception. ", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.FAILURE(e.getMessage());
        }
    }

    //根据cron表达式+misfire策略生成builder
    private CronScheduleBuilder genCronScheduleBuilder(String cronExpress, int misFireStrategy){
        CronScheduleBuilder cronScheduleBuilder;
        switch (misFireStrategy) {
            case 1:
                cronScheduleBuilder = cronSchedule(cronExpress).withMisfireHandlingInstructionFireAndProceed();
                break;
            case -1:
                cronScheduleBuilder = cronSchedule(cronExpress).withMisfireHandlingInstructionIgnoreMisfires();
                break;
            default:
                cronScheduleBuilder = cronSchedule(cronExpress).withMisfireHandlingInstructionDoNothing();
                break;
        }
        return cronScheduleBuilder;
    }
    /**
     * 删除Job
     * 如果appCode+schedulerName+jobName都不空，根据三者查询job
     * 如果appCode+schedulerName不空，根据两者查询job
     * 如果appCode不空根据appCode查询Job
     *
     * @param appCode   appCode
     * @param schedName schedName
     * @param jobName   jobName
     * @return Result
     */
    @Transactional
    public Result removeJob(String appCode, String schedName, String jobName) {
        if(StringUtils.isBlank(appCode)){
            return Result.FAILURE("App Code 不能为空");
        }
        Set<JobKey> jobKeySet = new HashSet<>();

        try {
            logger.info("Deleting jobs under appCode-{}, schedulerName-{}, jobName-{}", appCode, schedName, jobName);
            //根据appCode + schedulerName + jobName查找
            if (StringUtils.isNotBlank(schedName) && StringUtils.isNotBlank(jobName)) {
                jobKeySet.add(new JobKey(jobName, genJobGroupName(appCode, schedName)));
                //根据appCode + schedulerName查找
            } else if(StringUtils.isNotBlank(schedName)){
                jobKeySet = marbleScheduler.getJobKeys(GroupMatcher.<JobKey>groupEquals(genJobGroupName(appCode, schedName)));
            } else{
                jobKeySet = marbleScheduler.getJobKeys(GroupMatcher.jobGroupStartsWith(appCode + "-"));
            }

            if (jobKeySet != null && jobKeySet.size() > 0) {
                for (JobKey jobKey : jobKeySet) {
                    marbleScheduler.deleteJob(jobKey);
                    logger.info("Delete job({}) from scheduler.", jobKey);
                }
            }
        } catch (Exception e) {
            logger.error("Remove Jobs under appCode-{}, schedName-{}, jobName-{} exception. ", appCode, schedName, jobName, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.FAILURE("删除Jobs异常: " + e.getMessage());
        }
        return Result.SUCCESS();
    }

    /**
     * 暂停job
     *
     * @param appCode   应用code
     * @param schedName 计划任务name
     * @param jobName   job name
     * @return Result
     */
    public Result pauseJob(String appCode, String schedName, String jobName) {
        Result result = validateArguments(appCode, schedName, jobName);
        if (!result.isSuccess()) {
            return result;
        }

        //暂停Job
        try {
            marbleScheduler.pauseJob(new JobKey(jobName, genJobGroupName(appCode, schedName)));
            return Result.SUCCESS();
        } catch (SchedulerException e) {
            logger.error("Pause job exception.", e);
            return Result.FAILURE(e.getMessage());
        }
    }

    /**
     * 暂停AppCode下的所有job
     *
     * @param appCode 应用code
     * @return Result
     */
    public Result pauseJob(String appCode) {
        if (StringUtils.isBlank(appCode)) {
            return Result.FAILURE("App Code 不能为空");
        }

        //暂停Job
        try {
            logger.info("Pausing jobs under appCode-{}", appCode);
            //根据AppCode查找所有job. Group Name以 appCode+‘-’开头
            Set<JobKey> jobKeySet = marbleScheduler.getJobKeys(GroupMatcher.jobGroupStartsWith(appCode + "-"));
            if (jobKeySet != null && jobKeySet.size() > 0) {
                for (JobKey jobKey : jobKeySet) {
                    marbleScheduler.pauseJob(jobKey);
                    logger.info("Pause job({})", jobKey);
                }
            }
            return Result.SUCCESS();
        } catch (SchedulerException e) {
            logger.error("Pause job exception.", e);
            return Result.FAILURE(e.getMessage());
        }
    }

    /**
     * 重新开始AppCode下的所有Job
     *
     * @param appCode 应用code
     * @return Result
     */
    public Result resumeJob(String appCode) {
        if (StringUtils.isBlank(appCode)) {
            return Result.FAILURE("App Code 不能为空");
        }

        //恢复Job
        try {
            logger.info("Resuming jobs under appCode-{}", appCode);
            //根据AppCode查找所有job. Group Name以 appCode+‘-’开头
            Set<JobKey> jobKeySet = marbleScheduler.getJobKeys(GroupMatcher.jobGroupStartsWith(appCode + "-"));
            if (jobKeySet != null && jobKeySet.size() > 0) {
                for (JobKey jobKey : jobKeySet) {
                    marbleScheduler.resumeJob(jobKey);
                    logger.info("Resume job({})", jobKey);
                }
            }
            return Result.SUCCESS();
        } catch (SchedulerException e) {
            logger.error("Resume job exception.", e);
            return Result.FAILURE(e.getMessage());
        }
    }

    /**
     * 重新开始Job
     *
     * @param appCode   应用code
     * @param schedName 计划任务name
     * @param jobName   job name
     * @return Result
     */
    public Result resumeJob(String appCode, String schedName, String jobName) {
        Result result = validateArguments(appCode, schedName, jobName);
        if (!result.isSuccess()) {
            return result;
        }

        //重启ob
        try {
            JobKey jobKey = new JobKey(jobName, genJobGroupName(appCode, schedName));
            marbleScheduler.resumeJob(jobKey);
            logger.info("Resume job-{}", jobKey);
            return Result.SUCCESS();
        } catch (SchedulerException e) {
            logger.error("Resume job exception.", e);
            return Result.FAILURE(e.getMessage());
        }
    }

    /**
     * 关闭Scheduler
     *
     * @param waitForJobsToComplete 是否等待job完成，默认不等待
     * @return Result
     */
    public Result shutdownSched(Boolean waitForJobsToComplete) {
        try {
            if (marbleScheduler != null && !marbleScheduler.isShutdown()) {
                marbleScheduler.shutdown(waitForJobsToComplete == null ? true : waitForJobsToComplete);
            }
            return Result.SUCCESS();
        } catch (Exception e) {
            logger.error("Shutdown scheduler exception.", e);
            return Result.FAILURE(e.getMessage());
        }
    }

    /**
     * 启动Scheduler
     *
     * @param delaySeconds 延迟启动秒数
     * @return Result
     */
    public Result startSched(Integer delaySeconds) {
        try {
            if (marbleScheduler != null && !marbleScheduler.isStarted()) {
                if (delaySeconds != null && delaySeconds > 0) {
                    marbleScheduler.startDelayed(delaySeconds);
                } else {
                    marbleScheduler.getListenerManager().addJobListener(new MarbleJobListener());
                    marbleScheduler.start();
                }
            }
            return Result.SUCCESS();
        } catch (Exception e) {
            logger.error("Start scheduler exception.", e);
            return Result.FAILURE(e.getMessage());
        }
    }

    //修改app下的所有job的marble版本号信息
    @Transactional
    public Result modifyJobMarbleVersion(String appCode, String newMarbleVersion) {
        if(StringUtils.isBlank(appCode) || StringUtils.isBlank(newMarbleVersion)){
            return Result.FAILURE("参数非法");
        }
        logger.info("Modify marble version of jobs under appCode-{}", appCode);
        //根据AppCode查找所有job. Group Name以 appCode+‘-’开头
        try {
            marbleScheduler.pauseJobs(GroupMatcher.jobGroupStartsWith(appCode + "-"));
            Set<JobKey> jobKeySet = marbleScheduler.getJobKeys(GroupMatcher.jobGroupStartsWith(appCode + "-"));
            if (jobKeySet != null && jobKeySet.size() > 0) {
                for (JobKey jobKey : jobKeySet) {
                    JobDetail oldJobDetail = marbleScheduler.getJobDetail(jobKey);
                    updateJobInfo2DataMap(oldJobDetail.getJobDataMap(), null, null, newMarbleVersion);
                    System.out.println("33" + oldJobDetail.getJobDataMap().get("JOB_INFO"));
                    marbleScheduler.addJob(oldJobDetail, true);
                }
            }
        } catch (SchedulerException e) {
            logger.error("Modify marble version of jobs under appCode-{} exception.", appCode, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.FAILURE(e.getMessage());
        }

        return Result.SUCCESS();
    }

    /**
     * 修改job的信息。
     * 如果传入的参数为null，表示不要改，有值表示要修改。为空字符串，表示要置空
     *
     * @param appCode   appCode
     * @param schedName scheduler name
     * @param jobName   job name
     * @param newDesc   新的描述信息
     * @param newCron   新的表达式
     * @param newParam  新的Job执行参数
     * @return Result
     */
    @Transactional
    public Result modifyJob(String appCode, String schedName, String jobName, String newDesc, String newCron, int newMisFireStrategy, String newParam, String newMarbleVersion) {
        Result result = validateArguments(appCode, schedName, jobName);
        if (!result.isSuccess()) {
            return result;
        }
        if (StringUtils.isNotBlank(newCron) && !CronExpression.isValidExpression(newCron)) {
            return Result.FAILURE("Cron表达式非法");
        }

        try {
            JobDetail oldJobDetail = marbleScheduler.getJobDetail(new JobKey(jobName, genJobGroupName(appCode, schedName)));
            if (oldJobDetail == null) {
                return Result.FAILURE("Job (" + jobName + ") 不存在");
            }
            //暂停旧的job
            marbleScheduler.pauseJob(oldJobDetail.getKey());

            //新建Job，覆盖之前存在的job
            JobDetail newJobDetail = JobBuilder.newJob(MarbleJobProxy.class).
                    withIdentity(jobName, genJobGroupName(appCode, schedName)).
                    withDescription(newDesc).storeDurably(true).build();//更新描述信息
            if (oldJobDetail.getJobDataMap() != null) {
                for (JobDataMap.Entry entry : oldJobDetail.getJobDataMap().entrySet()) {
                    newJobDetail.getJobDataMap().put((String) entry.getKey(), entry.getValue());
                }
            } else {
                logger.warn("The JobDataMap is empty. Can not update param.");
            }
            logger.info("Update the description of job({}) from {} to {}", newJobDetail.getKey(), oldJobDetail.getDescription(), newDesc);


            //更新Cron表达式
            if (newCron != null || newMisFireStrategy != 0) {
                TriggerKey triggerKey = new TriggerKey(genTriggerNameForJob(jobName), genTriggerGroupName(appCode, schedName));
                Trigger oldTrigger = marbleScheduler.getTrigger(triggerKey);
                if (oldTrigger == null || !(oldTrigger instanceof CronTrigger)) {
                    logger.warn("Can not find the cron trigger with key: {}", triggerKey);
                } else {
                    if ((newCron!=null && !newCron.equals(((CronTrigger) oldTrigger).getCronExpression())) || newMisFireStrategy != oldTrigger.getMisfireInstruction()) {
                        Trigger newTrigger = TriggerBuilder.newTrigger().
                                withIdentity(genTriggerNameForJob(jobName), genTriggerGroupName(appCode, schedName)).
                                withSchedule(genCronScheduleBuilder(newCron, (newMisFireStrategy!=0?newMisFireStrategy:oldTrigger.getMisfireInstruction()))).build();

                        marbleScheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
                        //marbleScheduler.pauseJob(newJobDetail.getKey());
                        logger.info("Update the cron express of job({}) from {} to {}", newJobDetail.getKey(), ((CronTrigger) oldTrigger).getCronExpression(), newCron);
                    }
                }
            }

            //更新DataMap中的Cron表达式+Param参数
            updateJobInfo2DataMap(newJobDetail.getJobDataMap(), newCron, newParam, newMarbleVersion);
            //用新的Job覆盖旧的实现更新操作
            marbleScheduler.addJob(newJobDetail, true);
            //暂停job
            marbleScheduler.pauseJob(newJobDetail.getKey());
            return Result.SUCCESS();
        } catch (Exception e) {
            logger.error("Remove job exception.", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.FAILURE("更新Cron异常：" + e.getMessage());
        }
    }

    //更新JobDataMap中的job-info信息
    private void updateJobInfo2DataMap(JobDataMap jobDataMap, String newCron, String newParam, String newMarbleVersion){
        if(jobDataMap != null){
            if(jobDataMap.get("JOB_INFO") != null){
                JobBasicInfo jobBasicInfo = JacksonUtil.json2pojo(jobDataMap.get("JOB_INFO").toString(), JobBasicInfo.class);
                if (jobBasicInfo != null) {
                    //更新job参数
                    if(!StringUtils.isStringEqual(newParam,jobBasicInfo.getParam())){
                        logger.info("Update the param of job({}) from {} to {}",jobBasicInfo.getJobName(), jobBasicInfo.getParam(), newParam);
                        jobBasicInfo.setParam(newParam);
                    }

                    //更新Cron表达式
                    if(StringUtils.isNotBlank(newCron) && !newCron.equals(jobBasicInfo.getJobCronExpress()) && CronExpression.isValidExpression(newCron)){
                        logger.info("Update the Cron Express of job({}) from {} to {}",jobBasicInfo.getJobName(), jobBasicInfo.getJobCronExpress(), newCron);
                        jobBasicInfo.setJobCronExpress(newCron);
                    }

                    //更新Marble版本号
                    if(StringUtils.isNotBlank(newMarbleVersion) && !newMarbleVersion.equals(jobBasicInfo.getMarbleVersion())){
                        logger.info("Update the MarbleVersion of job({}) from {} to {}",jobBasicInfo.getJobName(), jobBasicInfo.getMarbleVersion(), newMarbleVersion);
                        jobBasicInfo.setMarbleVersion(newMarbleVersion);
                    }
                    try {
                        jobDataMap.put("JOB_INFO", JacksonUtil.obj2json(jobBasicInfo));
                    }catch (Exception e){
                        logger.error("Update the JOB-INFO of data map exception. ", e);
                    }
                }
            }

        }
    }
    /**
     * 删除AppCode下所有Job的连接信息（以IP区分）。如果connectInfo为空，则意味着删除
     *
     * @param appCode 应用code
     * @param ip      要删除的IP
     * @return Result
     */
    public Result removeJobConnectInfo(String appCode, String ip) {
        if (StringUtils.isBlank(appCode) || StringUtils.isBlank(ip)) {
            return Result.FAILURE("参数非法：AppCode和IP都不能为空");
        }
        try {
            //根据AppCode查找所有job. Group Name以 appCode+‘-’开头
            Set<JobKey> jobKeySet = marbleScheduler.getJobKeys(GroupMatcher.jobGroupStartsWith(appCode + "-"));
            if (jobKeySet != null && jobKeySet.size() > 0) {
                for (JobKey jobKey : jobKeySet) {
                    JobDetail jobDetail = marbleScheduler.getJobDetail(jobKey);
                    if (jobDetail != null) {
                        JobDataMap dataMap = jobDetail.getJobDataMap();
                        if (dataMap != null && dataMap.get("THRIFT_CONNECT_INFO") != null) {
                            ThriftConnectInfo connectInfo = JacksonUtil.json2pojo(dataMap.get("THRIFT_CONNECT_INFO").toString(), ThriftConnectInfo.class);
                            if (connectInfo != null && ArrayUtils.listIsNotBlank(connectInfo.getServerInfo())) {
                                Iterator<ThriftConnectInfo.Server> iterator = connectInfo.getServerInfo().iterator();
                                while (iterator.hasNext()) {
                                    if (ip.equals(iterator.next().getIp())) {
                                        iterator.remove();
                                        logger.info("Delete the server({}) from job({})", ip, jobDetail.getKey());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return Result.SUCCESS();
        } catch (Exception e) {
            logger.error("Remove job exception.", e);
            return Result.FAILURE("删除server信息异常：" + e.getMessage());
        }
    }

    //单例模式实现
    private static class SigletonHolder {
        private static final QuartzManager instance = new QuartzManager();
    }

    public static QuartzManager getInstance() {
        return SigletonHolder.instance;
    }

    public QuartzManager setScheduler(Scheduler scheduler) {
        this.marbleScheduler = scheduler;
        return this;
    }

    //生成Job的groupName
    public static String genJobGroupName(String appCode, String schedName) {
        return appCode + "-" + schedName;
    }

    //生成Trigger的groupName
    public static String genTriggerGroupName(String appCode, String schedName) {
        return appCode + "-" + schedName;
    }

    //生成Trigger的name,根据Job Name
    private static String genTriggerNameForJob(String jobName) {
        return jobName + "-trigger";
    }

    //校验参数
    private Result validateArguments(String appCode, String schedName, String jobName) {
        if (StringUtils.isBlank(appCode)) {
            return Result.FAILURE("'App Code' 不能为空");
        }
        if (StringUtils.isBlank(schedName)) {
            return Result.FAILURE("'Scheduler Name' 不能为空");
        }
        if (StringUtils.isBlank(jobName)) {
            return Result.FAILURE("'Job Name' 不能为空");
        }

        return Result.SUCCESS();
    }
}
