package com.github.jxdong.marble.controller;

import com.github.jxdong.common.util.StringUtils;
import com.github.jxdong.marble.domain.dto.CronExpressDTO;
import com.github.jxdong.marble.domain.dto.JobDetailDTO;
import com.github.jxdong.marble.domain.dto.SchedulerDetailDTO;
import com.github.jxdong.marble.domain.dto.ServerDetailDTO;
import com.github.jxdong.marble.domain.model.*;
import com.github.jxdong.marble.domain.model.enums.MisfireInstructionEnum;
import com.github.jxdong.marble.domain.repositories.SchedRepository;
import com.github.jxdong.marble.global.util.AuthorityUtil;
import com.github.jxdong.marble.global.util.DTOConvert;
import com.github.jxdong.marble.global.util.DateUtil;
import com.github.jxdong.marble.infrastructure.service.ThriftManager;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.util.*;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/6/26 8:40
 */
@Controller @RequestMapping("/app")
public class SchedulerController extends BasicController{
    private static Logger logger = LoggerFactory.getLogger(SchedulerController.class);

    @Autowired
    private SchedRepository schedRepository;

    /**
     * 页面跳转 - scheduler
     * @return view
     */
    @RequestMapping("{appCode}/scheduler")
    public ModelAndView pageForward(@PathVariable("appCode") String appCode) throws Exception{
        if(StringUtils.isBlank(appCode)){
            return errorModelAndView(Response.ResultCodeEnum.INVALID_ARGUMENTS, "App Code cannot be empty");
        }
        //校验权限
        AuthorityUtil.getInstance().validateAuthority(appCode);
        Map<String, Object> data = new HashMap<>();
        data.put("APP_CODE",appCode);
        return modelAndView("scheduler", data);
    }

    /**
     * 计划任务查询
     * @param appCode 应用Code
     * @return Response
     * @throws Exception
     */
    @RequestMapping(value = "scheduler/query", method = RequestMethod.GET)
    @ResponseBody
    public Response querySchedulerByAppCode(String appCode) throws Exception{
        if(StringUtils.isBlank(appCode)){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS, "App Code 不能为空");
        }
        AuthorityUtil.getInstance().validateAuthority(appCode);
        List<SchedulerDetail> schedulerList = schedRepository.querySchedByAppCode(appCode);
        return new Response(DTOConvert.entity2DTO(schedulerList, SchedulerDetailDTO.class));
    }

    /**
     * 计划任务下的Job查询
     * @param appCode 应用code
     * @param schedName 计划任务name
     * @param jobName job的name
     * @return Response
     * @throws Exception
     */
    @RequestMapping(value = "scheduler/job/query", method = RequestMethod.GET)
    @ResponseBody
    public Response querySchedulerJobByName(String appCode, String schedName, String jobName) throws Exception{
        if(StringUtils.isBlank(appCode) || StringUtils.isBlank(schedName)){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS, "`App Code` and `Sched Name` 都不能为空");
        }
        AuthorityUtil.getInstance().validateAuthority(appCode);

        List<JobDetail> jobList = schedRepository.queryJob(appCode, schedName, jobName);
        return new Response(DTOConvert.entity2DTO(jobList, JobDetailDTO.class));
    }

    /**
     * 计划任务下服务器的查询
     * @param appCode 应用Code
     * @param schedName 计划任务name
     * @return Response
     * @throws Exception
     */
    @RequestMapping(value = "scheduler/server/query", method = RequestMethod.GET)
    @ResponseBody
    public Response querySchedulerServer(String appCode, String schedName) throws Exception{
        if(StringUtils.isBlank(appCode) || StringUtils.isBlank(schedName)){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS, "`App Code` and `Sched Name` 都不能为空");
        }
        AuthorityUtil.getInstance().validateAuthority(appCode);

        List<ServerDetail> serverDetails = schedRepository.querySchedServer(appCode, schedName);
        return new Response(DTOConvert.entity2DTO(serverDetails, ServerDetailDTO.class));
    }


    /**
     * 添加新的Scheduler记录
     * @param schedulerDetail 计划任务明细对象
     * @return Response
     * @throws Exception
     */
    @RequestMapping(value = "scheduler/add", method = RequestMethod.PATCH)
    @ResponseBody
    public Response addAppScheduler(@ModelAttribute SchedulerDetail schedulerDetail) throws Exception{
        //参数校验
        if(schedulerDetail == null || !schedulerDetail.validateParamForInsert()){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }
        AuthorityUtil.getInstance().validateAuthority(schedulerDetail.getAppDetail().getCode());

        Result result = schedRepository.addAppScheduler(schedulerDetail);
        return Response.resultResponse(result);
    }

    /**
     * 删除Scheduler记录
     * @param appCode 应用Code
     * @param schedName 计划任务name
     * @return Response
     * @throws Exception
     */
    @RequestMapping(value = "scheduler/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public Response deleteAppScheduler(String appCode, String schedName) throws Exception{
        //参数校验
        if(StringUtils.isBlank(appCode) || StringUtils.isBlank(schedName)){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }
        AuthorityUtil.getInstance().validateAuthority(appCode);

        Result result = schedRepository.deleteScheduler(appCode, schedName);
        return Response.resultResponse(result);
    }

    /**
     * 添加Job记录
     * @param jobDetail job对象
     * @return Response
     * @throws Exception
     */
    @RequestMapping(value = "scheduler/job/add", method = RequestMethod.PATCH)
    @ResponseBody
    public Response addAppSchedulerJob(@ModelAttribute JobDetail jobDetail) throws Exception{
        //参数校验
        if(jobDetail == null || !jobDetail.validateParamForInsert() || !MisfireInstructionEnum.containItem(jobDetail.getMisfireStrategy())){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }

        AuthorityUtil.getInstance().validateAuthority(jobDetail.getApp().getCode());
        Result result = schedRepository.addJob(jobDetail);
        return Response.resultResponse(result);
    }

    /**
     * job更新（描述、Cron表达式、参数）
     * @param jobDetail job对象
     * @return Response
     * @throws Exception
     */
    @RequestMapping(value = "scheduler/job/update", method = RequestMethod.PATCH)
    @ResponseBody
    public Response editSchedJob(@ModelAttribute JobDetail jobDetail) throws Exception{
        //参数校验
        if(jobDetail == null || !jobDetail.validateParamForUpdate()){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }

        AuthorityUtil.getInstance().validateAuthority(jobDetail.getApp().getCode());

        Result result = schedRepository.updateJob(jobDetail);
        return Response.resultResponse(result);
    }

    /**
     * 启动Job
     * @param appCode 应用Code
     * @param schedName 计划任务Name
     * @param jobName Job Name
     * @return Response
     * @throws Exception
     */
    @RequestMapping(value = "scheduler/job/start", method = RequestMethod.PATCH)
    @ResponseBody
    public Response startScheduler(String appCode, String schedName, String jobName) throws Exception{
        AuthorityUtil.getInstance().validateAuthority(appCode);

        Result result = schedRepository.startJob(appCode, schedName, jobName);
        return Response.resultResponse(result);
    }

    @RequestMapping(value = "scheduler/job/stop", method = RequestMethod.PATCH)
    @ResponseBody
    public Response stopScheduler(String appCode, String schedName, String jobName) throws Exception{
        AuthorityUtil.getInstance().validateAuthority(appCode);

        Result result = schedRepository.pauseJob(appCode, schedName, jobName);
        return Response.resultResponse(result);
    }

    /**
     * Job删除
     * @param appCode 应用Code
     * @param schedName 计划任务Name
     * @param jobName job name
     * @return Response
     * @throws Exception
     */
    @RequestMapping(value = "scheduler/job/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public Response deleteSchedJob(String appCode, String schedName, String jobName) throws Exception{
        //参数校验
        if(StringUtils.isBlank(appCode) || StringUtils.isBlank(schedName) || StringUtils.isBlank(jobName)){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }

        AuthorityUtil.getInstance().validateAuthority(appCode);
        Result result = schedRepository.deleteJob(appCode, schedName, jobName);
        return Response.resultResponse(result);
    }

    @RequestMapping(value = "scheduler/server/checkhealth", method = RequestMethod.GET)
    @ResponseBody
    public Response checkHealthOfServer(String hostIp, int port) {
        Result result = ThriftManager.getInstance().checkConnectivity(hostIp, port);
        return Response.resultResponse(result);
    }
    // Result result = ThriftManager.connect2Server(serverInfo.getIp(), serverInfo.getPort(), new String[]{connectInfo.getServiceName()}, jobParam);


    /**
     * 解析Cron表达式含义
     * @param cronExpress cron表达式
     * @param beginTime 开始时间
     * @return Response
     */
    @RequestMapping(value = "scheduler/job/cron/explain", method = RequestMethod.GET)
    @ResponseBody
    public Response explainCronExpress(String cronExpress, String beginTime) {
        if(StringUtils.isBlank(cronExpress)){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }
        CronExpressDTO cronExpressDTO = new CronExpressDTO();
        cronExpressDTO.setCronExpress(cronExpress);
        if(!CronExpression.isValidExpression(cronExpress)){
            cronExpressDTO.setIsValid(false);
            return new Response(cronExpressDTO);
        }
        cronExpressDTO.setIsValid(true);
        try {
            Date beginDate = DateUtil.convertStr2Date(beginTime, "yyyy-MM-dd HH:mm:ss") == null?new Date():DateUtil.convertStr2Date(beginTime, "yyyy-MM-dd HH:mm:ss");
            CronExpression cron = new CronExpression(cronExpress);
            List<String> nextFireTimeList = new ArrayList<>();
            //得到5个执行时间
            for(int i=0; i<5; i++){
                beginDate = cron.getNextValidTimeAfter(beginDate);
                nextFireTimeList.add(DateUtil.formateDate(beginDate));
            }
            cronExpressDTO.setNextFireTimeList(nextFireTimeList);
        } catch (ParseException e) {
            logger.error("explain the cron express({}) exception. ", cronExpress, e);
            return Response.FAILURE(Response.ResultCodeEnum.UNKNOWN_ERROR, e.getMessage());
        }
        return new Response(cronExpressDTO);
    }

}
