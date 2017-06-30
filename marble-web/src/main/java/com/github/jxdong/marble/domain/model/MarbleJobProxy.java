package com.github.jxdong.marble.domain.model;

import com.alibaba.fastjson.JSON;
import com.github.jxdong.marble.agent.entity.ClassInfo;
import com.github.jxdong.marble.common.util.*;
import com.github.jxdong.marble.domain.model.enums.AppStatusEnum;
import com.github.jxdong.marble.domain.model.enums.JobExecStatusEnum;
import com.github.jxdong.marble.domain.model.enums.JobReqStatusEnum;
import com.github.jxdong.marble.domain.repositories.AppRepository;
import com.github.jxdong.marble.global.util.SpringContextUtil;
import com.github.jxdong.marble.infrastructure.service.LogManager;
import com.github.jxdong.marble.infrastructure.service.RPCClientFactory;
import com.google.common.base.Throwables;
import org.quartz.*;

import java.util.*;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/11 14:50
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MarbleJobProxy implements Job {
    private ClogWrapper logger = ClogWrapperFactory.getClogWrapper(MarbleJobProxy.class);

    /**
     * 1、判断是否当前JOB允许执行
     *
     * @param jobExecutionContext job上下文
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //生成UUID
        String requestNo = StringUtils.genUUID();
        logger.REQNO(requestNo).info("Job begin to exec ");

        try {
            //map中添加执行的服务器+端口号信息, 以及请求流水号
            Object jobInfoObject = jobExecutionContext.getMergedJobDataMap().get("JOB_INFO");
            if (jobInfoObject == null) {
                logger.REQNO(requestNo).error("Job execute failed, can not parse out JOB_INFO from the JobExecutionContext object");
                return;
            }
            JobBasicInfo jobBasicInfo = JSON.parseObject(jobInfoObject.toString(), JobBasicInfo.class);//;JacksonUtil.json2pojo(jobInfoObject.toString(), JobBasicInfo.class);
            if (jobBasicInfo == null) {
                logger.REQNO(requestNo).error("Job execute failed, can not convert JOB_INFO to JobBasicInfo object");
                return;
            }
            String serviceName = jobBasicInfo.getServiceName();
            logger.REQNO(requestNo).SERVICE(serviceName).info(" parsed out the JobInfo from JobExecutionContext. JobInfo:{}", jobBasicInfo);

            ServerDetail serverDetail = parseOutOneServer(requestNo, jobBasicInfo);
            if (serverDetail == null || StringUtils.isBlank(serverDetail.getIp())) {
                logger.REQNO(requestNo).SERVICE(serviceName).error("Job execute failed, can not parse out the server info");
                return;
            }

            //执行请求入库
            LogManager logManager = (LogManager) SpringContextUtil.getBean("logManager");
            /*
            判断当前JOB是否需要执行
                1.1 JOB为异步JOB，始终返回true
                1.2 JOB为同步JOB，根据上次执行时间进行判断。如果上次执行结果还未知，且未超出最长等待时间则忽略本次执行。否则执行
             */
            if (!allowJobExec(requestNo, jobBasicInfo)) {
                logger.REQNO(requestNo).SERVICE(serviceName).info("Job execute has been ignored, previous exec is not end yet");
                logManager.addJobExecutionLog(new JobExecutionLog(
                        requestNo,//String requestNo,
                        jobBasicInfo.getAppCode(),// String appCode,
                        jobBasicInfo.getSchedName(),// String schedName,
                        jobBasicInfo.getJobName(),// String jobName,
                        jobBasicInfo.getJobCronExpress(),// String jobCronExpress,
                        StringUtils.safeString(jobBasicInfo.getServerIp()) + ":" + StringUtils.safeString(jobBasicInfo.getServerPort()),// String serverInfo,
                        JobReqStatusEnum.SUCCESS,// JobExecStatusEnum
                        null,// String detail
                        jobBasicInfo.isSynchronous() ? "同步JOB (最长等待" + StringUtils.safeString(jobBasicInfo.getMaxWaitTime()) + "分钟)" : "异步JOB",
                        JobExecStatusEnum.SUCCESS.getCode(),
                        "有正在执行的JOB，忽略本次执行"
                ));
                return;
            }

            String marbleVersion = jobBasicInfo.getMarbleVersion();
            jobBasicInfo.setServerIp(serverDetail.getIp());
            jobBasicInfo.setServerPort(serverDetail.getPort());
            jobBasicInfo.setExecuteReqNumber(requestNo);
            String jobParam = jobBasicInfo.getParam();
            boolean isSync = jobBasicInfo.isSynchronous();
            Long maxWaitTime = jobBasicInfo.getMaxWaitTime();
            //更新data map
            jobExecutionContext.getMergedJobDataMap().put("JOB_INFO", JacksonUtil.obj2json(jobBasicInfo));

            Result insertResult = logManager.addJobExecutionLog(new JobExecutionLog(
                    requestNo,//String requestNo,
                    jobBasicInfo.getAppCode(),// String appCode,
                    jobBasicInfo.getSchedName(),// String schedName,
                    jobBasicInfo.getJobName(),// String jobName,
                    jobBasicInfo.getJobCronExpress(),// String jobCronExpress,
                    StringUtils.safeString(jobBasicInfo.getServerIp()) + ":" + StringUtils.safeString(jobBasicInfo.getServerPort()),// String serverInfo,
                    JobExecStatusEnum.REQUESTING,// JobExecStatusEnum
                    "",// String detail
                    jobBasicInfo.isSynchronous() ? "同步JOB (最长等待" + StringUtils.safeString(jobBasicInfo.getMaxWaitTime()) + "分钟)" : "异步JOB"
            ));
            logger.REQNO(requestNo).SERVICE(serviceName).info("persistence job execute log to DB result: result:{}", insertResult);

            //与服务端创建连接并执行
            logger.REQNO(requestNo).SERVICE(serviceName).info("begin to RPC service, serverInfo:{}, param: {}",serverDetail, jobParam);

            Set<ClassInfo> classInfoSet = new HashSet<>();
            classInfoSet.add(new ClassInfo(jobBasicInfo.getServiceName(), "execute", jobParam));
            //根据得到的Marble-Version动态选择调用方式
            Result result = RPCClientFactory.getClientManager(requestNo, marbleVersion).serviceInvoke(requestNo, serverDetail.getIp(), serverDetail.getPort(), classInfoSet, isSync, maxWaitTime);
            logger.REQNO(requestNo).SERVICE(serviceName).info("RPC service result:{}", result);
            if (!result.isSuccess()) {
                throw new JobExecutionException(result.getResultMsg());
            }
        } catch (Exception e) {
            logger.REQNO(requestNo).error("Job execute exception. Exception detail: {}", Throwables.getStackTraceAsString(e));
            throw new JobExecutionException(e);
        }
    }


    /**
     * 判断当前JOB是否需要执行
     * 1.1 JOB为异步JOB，始终返回true
     * 1.2 JOB为同步JOB，根据上次执行时间进行判断。如果上次执行结果还未知，且未超出最长等待时间则忽略本次执行。否则执行
     *
     * @param jobInfo job信息
     * @return boolean
     */
    private boolean allowJobExec(String requestNo, JobBasicInfo jobInfo) {
        if (jobInfo == null || !jobInfo.isSynchronous()) {
            logger.addTag("REQ_NO", requestNo).info("{} has been allowed to exec, the jobInfo is null or is not sync", jobInfo == null ? "null" : jobInfo.getLogName());
            return true;
        }
        if (jobInfo.getMaxWaitTime() == null || jobInfo.getMaxWaitTime() <= 0) {
            logger.addTag("REQ_NO", requestNo).error("{} has been prevent to exec, because the config-MaxWaitTime({}) is invalid", jobInfo.getLogName(), jobInfo.getMaxWaitTime());
            return true;
        }

        LogManager logManager = (LogManager) SpringContextUtil.getBean("logManager");
        //查询执行结果在请求中的最近的一条记录
        List<JobExecutionLog> execLogs = logManager.queryJobExecutionLog(jobInfo.getAppCode(), jobInfo.getSchedName(), jobInfo.getJobName(), null, null, null, -1,
                JobExecStatusEnum.REQUESTING.getCode(), "createTime", "desc", new Page(1, 1));
        if (ArrayUtils.listIsNotBlank(execLogs)) {
            JobExecutionLog execLog = execLogs.get(0);
            logger.addTag("REQ_NO", requestNo).warn("{} has been allowed to exec, no exec-log has been found", jobInfo.getLogName());

            if (execLog.getBeginTime() != null) {
                long rangeTime = com.github.jxdong.marble.global.util.DateUtil.getBetween(execLog.getBeginTime(), new Date(), com.github.jxdong.marble.global.util.DateUtil.YYYYMMDDHHMMSS, com.github.jxdong.marble.global.util.DateUtil.MINUTE_RETURN);
                logger.addTag("REQ_NO", requestNo).info("{}, prev exec beginTime-{}, currentTime-{}, maxWaitTime-{}. rangeTime-{}(mins)", jobInfo.getLogName(), execLog.getBeginTime(), new Date(), jobInfo.getMaxWaitTime(), rangeTime);
                if (rangeTime < jobInfo.getMaxWaitTime()) {
                    logger.addTag("REQ_NO", requestNo).info("{} this exec request has been ignored", jobInfo.getLogName());
                    return false;
                }
            }
        } else {
            logger.addTag("REQ_NO", requestNo).warn("{} has been allowed to exec, no exec-log has been found", jobInfo.getLogName());
        }

        return true;
    }

    //解析出可用的服务器信息
    private ServerDetail parseOutOneServer(String reqNo, JobBasicInfo jobBasicInfo) {
        String serviceName = jobBasicInfo.getServiceName();
        logger.REQNO(reqNo).SERVICE(serviceName).info("try to parse out the server info");

        ServerDetail targetServerInfo = null;
        List<ServerDetail> serverDetails = new ArrayList<>();
        //查询APP的信息是否包含 app 的SOA应用配置
        AppRepository appRepository = (AppRepository) SpringContextUtil.getBean("appRepositoryImpl");
        List<AppDetail> appDetails = appRepository.queryAppWithSched(jobBasicInfo.getAppCode(), AppStatusEnum.USABLE.getCode(), jobBasicInfo.getSchedName());
        if (appDetails != null && appDetails.size()>0) {
            AppDetail appDetail = appDetails.get(0);
            //尝试获取端口号信息
            if (ArrayUtils.listIsNotBlank(appDetail.getSchedulers())) {
                List<ServerDetail> schedServers = appDetail.getSchedulers().get(0).getServerDetails();
                if (ArrayUtils.listIsNotBlank(schedServers)) {
                    boolean hasAuto = false;
                    int autoPort = 0;
                    //查看是否有AUTO配置
                    for (ServerDetail sd : schedServers) {
                        if ("AUTO".equalsIgnoreCase(sd.getIp()) && sd.getPort() > 0) {
                            hasAuto = true;
                            autoPort = sd.getPort();
                            break;
                        }
                    }
                    //如果存在AUTO尝试判断soa配置.暂时不支持
                    if (hasAuto) {
                        return null;
                    }
                    if (ArrayUtils.listIsBlank(serverDetails)) {
                        serverDetails = schedServers;
                    }
                }else{
                    logger.REQNO(reqNo).SERVICE(serviceName).error("parse out server info failed, cannot get the servers records from DB");
                }
            }else {
                logger.REQNO(reqNo).SERVICE(serviceName).error("parse out server info failed, cannot get the schedulers records from DB");
            }
        }else{
            logger.REQNO(reqNo).SERVICE(serviceName).error("parse out server info failed, cannot get the APP records from DB");
        }
        //随机获取一台服务
        if (ArrayUtils.listIsNotBlank(serverDetails)) {
            Integer randomIndex[] = CommonUtil.randomCommon(0, serverDetails.size(), 1);
            if (randomIndex != null) {
                targetServerInfo = serverDetails.get(randomIndex[0]);
                logger.REQNO(reqNo).SERVICE(serviceName).info("get one random server from server list. serverInfo:P{}", targetServerInfo);
            }
        }
        return targetServerInfo;
    }
}
