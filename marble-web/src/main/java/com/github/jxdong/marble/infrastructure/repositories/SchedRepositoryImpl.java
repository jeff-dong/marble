package com.github.jxdong.marble.infrastructure.repositories;

import com.github.jxdong.common.util.ArrayUtils;
import com.github.jxdong.common.util.CommonUtil;
import com.github.jxdong.common.util.StringUtils;
import com.github.jxdong.marble.domain.model.*;
import com.github.jxdong.marble.domain.model.enums.MisfireInstructionEnum;
import com.github.jxdong.marble.domain.model.enums.SchedStatusEnum;
import com.github.jxdong.marble.domain.repositories.SchedRepository;
import com.github.jxdong.marble.global.util.SqlErrorUtil;
import com.github.jxdong.marble.infrastructure.repositories.mapper.mysql.AppMapper;
import com.github.jxdong.marble.infrastructure.repositories.mapper.mysql.SchedMapper;
import com.github.jxdong.marble.infrastructure.service.QuartzManager;
import com.github.jxdong.marble.server.thrift.ThriftConnectInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/11/14 14:18
 */
@Repository("schedRepository")
public class SchedRepositoryImpl implements SchedRepository {
    private static Logger logger = LoggerFactory.getLogger(SchedRepositoryImpl.class);
    @Autowired
    private SchedMapper schedMapper;
    @Autowired
    private AppMapper appMapper;
    @Autowired
    private QuartzManager quartzManager;

    @Override
    public List<SchedulerDetail> querySchedByAppCode(String appCode) {
        if (StringUtils.isBlank(appCode)) {
            logger.error("Illegal arguments. AppCode cannot be empty");
            return null;
        }
        try {
            List<SchedulerDetail> schedulerDetails = schedMapper.selectAppSched(appCode, null);
            if (CommonUtil.listIsNotBlank(schedulerDetails)) {
                for (SchedulerDetail sd : schedulerDetails) {
                    sd.setServerDetails(schedMapper.selectSchedServer(appCode, sd.getName(), null, 0));
                }
            }
            return schedulerDetails;
        } catch (Exception e) {
            logger.error("query scheduler by appCode exception, detail info: ", e);
        }
        return null;
    }

    @Override
    public List<ServerDetail> querySchedServer(String appCode, String schedName) {
        try {
            return schedMapper.selectSchedServer(appCode, schedName, null, 0);
        } catch (Exception e) {
            logger.error("query sched server exception, detail info: ", e);
        }
        return null;
    }

    @Override
    public List<JobDetail> queryJob(String appCode, String schedName, String jobName) {
        try {
            return quartzManager.queryJobs(appCode, schedName, jobName);
        } catch (Exception e) {
            logger.error("query jobs({}) exception, detail info: ", jobName, e);
        }
        return null;
    }

    @Override
    public Result startJob(String appCode, String schedName, String jobName) {
        if (StringUtils.isBlank(appCode) || StringUtils.isBlank(schedName) || StringUtils.isBlank(jobName)) {
            return Result.FAILURE("Illegal arguments");
        }
        return quartzManager.resumeJob(appCode, schedName, jobName);
    }

    @Override
    public Result pauseJob(String appCode, String schedName, String jobName) {
        if (StringUtils.isBlank(appCode) || StringUtils.isBlank(schedName) || StringUtils.isBlank(jobName)) {
            return Result.FAILURE("Illegal arguments");
        }
        return quartzManager.pauseJob(appCode, schedName, jobName);
    }

    @Override
    public Result deleteJob(String appCode, String schedName, String jobName) {
        if(StringUtils.isNotBlank(appCode) && StringUtils.isNotBlank(schedName) && StringUtils.isNotBlank(jobName)){
            return quartzManager.removeJob(appCode, schedName, jobName);
        }else{
            return Result.FAILURE("参数非法");
        }
    }

    @Override
    public Result updateJob(JobDetail jobDetail) {
        if (jobDetail == null || jobDetail.getApp() == null ||
                jobDetail.getScheduler() == null ||
                StringUtils.isBlank(jobDetail.getApp().getCode()) ||
                StringUtils.isBlank(jobDetail.getScheduler().getName()) ||
                StringUtils.isBlank(jobDetail.getName()) ||
                !MisfireInstructionEnum.containItem(jobDetail.getMisfireStrategy())) {
            logger.error("Update Job Detail failed. Illegal arguments. JobDetail: ", jobDetail);
            return Result.FAILURE("参数非法");
        }
        String appCode = jobDetail.getApp().getCode();
        String schedName = jobDetail.getScheduler().getName();

        return quartzManager.modifyJob(appCode,
                schedName,
                jobDetail.getName(),
                jobDetail.getDescription(),
                jobDetail.getCronExpress(),
                jobDetail.getMisfireStrategy(),
                jobDetail.getParam(), null);

    }


    /**
     * 1、添加Sched记录
     * 2、添加Sched-server记录；
     *
     * @param schedulerDetail scheduler
     * @return Result
     */
    @Override
    @Transactional
    public Result addAppScheduler(SchedulerDetail schedulerDetail) {
        if (schedulerDetail == null || !schedulerDetail.validateParamForInsert()) {
            return Result.FAILURE("illegal arguments");
        }
        String errorMsg = "Inner Exception";
        try {
            String appCode = schedulerDetail.getAppDetail().getCode();
            //查找DB中是否存在相同的记录
            List<SchedulerDetail> scheds = schedMapper.selectAppSched(appCode, schedulerDetail.getName());
            if (ArrayUtils.listIsNotBlank(scheds)) {
                return Result.FAILURE("The Scheduler with same name (" + schedulerDetail.getName() + ") has exist.");
            }

            List<ServerDetail> serverDetails = appMapper.selectServerBySched(appCode, schedulerDetail.getName());
            //带插入的server list
            List<ServerDetail> servers2Insert = new ArrayList<>();
            //遍历server信息，判断是否server都在DB中存在
            for (ServerDetail sd : schedulerDetail.getServerDetails()) {
                if (sd != null && sd.getId() > 0 && sd.getPort() > 0) {
                    //查找Server
                    ServerDetail serverDetail = appMapper.selectServerById(sd.getId());
                    if (serverDetail != null && appCode.equals(serverDetail.getAppCode())) {
                        boolean hasExist = false;
                        if (ArrayUtils.listIsNotBlank(serverDetails)) {
                            for (ServerDetail tempSd : serverDetails) {
                                if (tempSd != null && serverDetail.getIp().equals(tempSd.getIp()) && sd.getPort() == tempSd.getPort()) {
                                    //已经存在
                                    hasExist = true;
                                    break;
                                }
                            }
                        }
                        if (!hasExist) {
                            serverDetail.setSchedName(schedulerDetail.getName());
                            serverDetail.setPort(sd.getPort());
                            servers2Insert.add(serverDetail);
                        }
                    } else {
                        logger.warn("the server({}) does not exist", sd.getId());
                    }
                }
            }
            if (servers2Insert.size() > 0) {
                //DB中插入新Scheduler记录
                schedulerDetail.setStatus(SchedStatusEnum.USABLE.getCode());
                schedMapper.insertAppSched(schedulerDetail);
                //遍历server list 插入server sched记录
                for (ServerDetail teSD : servers2Insert) {
                    schedMapper.insertServerSched(teSD);
                }
                return Result.SUCCESS();
            } else {
                Result.FAILURE("insert failed. no available server info");
            }
        } catch (DataAccessException e) {
            logger.error("Insert schedulerdata access exception, detail info: ", e);
            errorMsg = SqlErrorUtil.getDataAccessExceptionMsg(e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            logger.error("Insert scheduler exception, detail info: ", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return Result.FAILURE("insert failed. " + errorMsg);
    }

    /**
     * 1、添加Job记录
     * 2、查找缓存是否存在Scheduler，有的话Job放入，否则没有就添加
     *
     * @param jobDetail job
     * @return Result
     */
    @Override
    @Transactional
    public Result addJob(JobDetail jobDetail) {
        if (jobDetail == null || !jobDetail.validateParamForInsert()) {
            return Result.FAILURE("参数非法");
        }
        //查询当前scheduler的server信息
        List<ServerDetail> schedServers = querySchedServer(jobDetail.getApp().getCode(), jobDetail.getScheduler().getName());
        if (!ArrayUtils.listIsNotBlank(schedServers)) {
            return Result.FAILURE("查询不到任何server信息");
        }
        //查询App信息得到MarbleVersion
        AppDetail appDetail = appMapper.selectAppByCode(jobDetail.getApp().getCode());
        if (appDetail == null) {
            return Result.FAILURE("查询不到App信息");
        }
        //拼接ThriftConnectInfo数组
        List<ThriftConnectInfo.Server> serverList = new ArrayList<>();
        for (ServerDetail server : schedServers) {
            serverList.add(new ThriftConnectInfo.Server(server.getIp(), server.getPort()));
        }

        return quartzManager.addJob(
                jobDetail.getApp().getCode(),
                jobDetail.getScheduler().getName(),
                jobDetail.getName(),
                jobDetail.getDescription(),
                jobDetail.getCronExpress(),
                jobDetail.getMisfireStrategy(),
                jobDetail.getParam(),
                serverList,
                appDetail.getMarbleVersion());
    }

    /*
    *删除Scheduler
    * 1、删除scheduler下的所有job
    * 2、查找Scheduler下的server逐个删除；表marble_server_sched
    * 3、查找所有Scheduler记录，逐个删除; 表marble_app_sched
    */
    @Override
    public Result deleteScheduler(String appCode, String schedName) {
        if (StringUtils.isBlank(appCode) || StringUtils.isBlank(schedName)) {
            return Result.FAILURE("参数非法");
        }

        List<SchedulerDetail> schedulers = schedMapper.selectAppSched(appCode, schedName);
        if(schedulers == null || schedulers.size() != 1){
            return Result.FAILURE("找不到唯一的Scheduler: AppCode=("+appCode+"), Name=("+schedName+")");
        }
        try{
            //1、删除计划任务下的所有jobs
            Result result = quartzManager.removeJob(appCode, schedName, null);
            if(!result.isSuccess()){
                throw new Exception(result.getResultMsg());
            }
            //2、删除与server关系
            List<ServerDetail> servers = schedMapper.selectSchedServer(appCode, schedName, null, 0);
            if(ArrayUtils.listIsNotBlank(servers)){
                for(ServerDetail server : servers){
                    if(server != null && server.getId() >0){
                        schedMapper.deleteSchedServerById(server.getId());
                    }
                }
            }
            logger.info("delete scheduler-servers({}) under scheduler end", (servers==null?0:servers.size()));

            //3、删除Scheduler
            for(SchedulerDetail sc : schedulers){
                if(sc != null && sc.getId() >0){
                    schedMapper.deleteSchedById(sc.getId());
                }
            }
            logger.info("delete scheduler({}) from DB end", schedName);
            logger.info("delete scheduler({}) end", schedName);

            return Result.SUCCESS();
        }catch (Exception e){
            logger.error("delete scheduler exception.", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.FAILURE("删除计划任务失败: " + e.getMessage());
        }
    }

}
