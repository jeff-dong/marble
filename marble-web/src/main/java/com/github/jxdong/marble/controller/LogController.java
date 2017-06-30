package com.github.jxdong.marble.controller;

import com.github.jxdong.marble.common.util.JacksonUtil;
import com.github.jxdong.marble.common.util.StringUtils;
import com.github.jxdong.marble.domain.dto.JobExecutionLogDTO;
import com.github.jxdong.marble.domain.model.JobExecutionLog;
import com.github.jxdong.marble.domain.model.JobLogRequest;
import com.github.jxdong.marble.domain.model.Response;
import com.github.jxdong.marble.domain.model.Result;
import com.github.jxdong.marble.global.util.AuthorityUtil;
import com.github.jxdong.marble.global.util.DTOConvert;
import com.github.jxdong.marble.infrastructure.service.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/6/26 8:40
 */
@Controller
@RequestMapping("/log")
public class LogController extends BasicController {
    private static Logger logger = LoggerFactory.getLogger(LogController.class);

    @Autowired
    private LogManager logManager;

    @RequestMapping(value = "job/query", method = RequestMethod.GET)
    @ResponseBody
    public Response query(String jsonParam) {
        JobLogRequest request = JacksonUtil.json2pojo(jsonParam, JobLogRequest.class);
        if (request == null) {
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }
        List<JobExecutionLog> jobExecutionLogs  = new ArrayList<>();
        try{
            if(StringUtils.isNotBlank(request.getPrimaryKey())){
                jobExecutionLogs.add(logManager.queryJobExecutionLogByReqNo(request.getPrimaryKey()));
                request.getPage().setTotalRecord(1);
            }else{
                jobExecutionLogs = logManager.queryJobExecutionLog(
                        request.getAppCode(),
                        request.getSchedName(),
                        request.getJobName(),
                        request.getServerInfo(),
                        request.getBeginDate(),
                        request.getEndDate(),
                        request.getReqResultCode(),
                        request.getExecResultCode(),
                        request.getOrderColumn(),
                        request.getOrderDir(),
                        request.getPage());
            }

        }catch (Exception e){
            logger.error("Query logs exception, detail: ", e);
        }
        return new Response(DTOConvert.entity2DTO(jobExecutionLogs, JobExecutionLogDTO.class),request.getPage());
    }

    //删除日志记录
    @RequestMapping(value = "job/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public Response deleteJobLogs(String appCode, String schedName, String jobName, boolean delProcessing) throws Exception{
        if(StringUtils.isBlank(appCode) || StringUtils.isBlank(schedName)){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }
        AuthorityUtil.getInstance().validateAuthority(appCode);
        Result result = logManager.deleteJobExecutionLog(appCode, schedName, jobName, delProcessing);
        return Response.resultResponse(result);
    }

    //更新日志执行状态
    @RequestMapping(value = "job/execStatus/update", method = RequestMethod.PATCH)
    @ResponseBody
    public Response updateExecStatus(@RequestParam(value="pk", required=true) String requestNo,  @RequestParam(value="value", required=true) int newStatus) throws Exception{
        if(StringUtils.isBlank(requestNo) || newStatus <0){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }
        String appCode = "";
        try{
            JobExecutionLog jobExecutionLog = logManager.queryJobExecutionLogByReqNo(requestNo);
            if(jobExecutionLog==null){
                return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS, "找不到记录");
            }
            appCode = jobExecutionLog.getAppCode();
        }catch (Exception e){
            logger.error("Update job exec status exception, detail: ", e);
        }
        AuthorityUtil.getInstance().validateAuthority(appCode);
        Result result = logManager.updateExecuteResult(requestNo, null, null, newStatus, null, null);

        logger.info("Employee({}) 将[requestNo={}]执行状态修改为[{}]， 结果：{}", loginedAccount.getEmployee(), requestNo, newStatus, result);
        return Response.resultResponse(result);
    }

    //TODO 临时
    @RequestMapping(value = "job/temp/quartzDelete", method = RequestMethod.DELETE)
    @ResponseBody
    public Response deleteQuartzData() throws Exception{
        loginedAccount = AuthorityUtil.getInstance().getLoginedAccount();
        if("A00001".equalsIgnoreCase(loginedAccount.getEmployee())){
            logManager.clearQuartzDBData();
            return Response.SUCCESS;
        }else{
            return Response.FAILURE(Response.ResultCodeEnum.PERMISSION_DENIED);
        }
    }
}
