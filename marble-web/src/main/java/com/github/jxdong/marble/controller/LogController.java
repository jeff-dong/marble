package com.github.jxdong.marble.controller;

import com.github.jxdong.common.util.JacksonUtil;
import com.github.jxdong.common.util.StringUtils;
import com.github.jxdong.marble.domain.dto.JobExecutionLogDTO;
import com.github.jxdong.marble.domain.model.JobExecutionLog;
import com.github.jxdong.marble.domain.model.JobLogRequest;
import com.github.jxdong.marble.domain.model.Response;
import com.github.jxdong.marble.domain.model.Result;
import com.github.jxdong.marble.domain.repositories.LogRepository;
import com.github.jxdong.marble.global.util.AuthorityUtil;
import com.github.jxdong.marble.global.util.DTOConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/6/26 8:40
 */
@Controller
@RequestMapping("/log")
public class LogController extends BasicController {
    private static Logger logger = LoggerFactory.getLogger(LogController.class);

    @Autowired
    private LogRepository logRepository;

    @RequestMapping(value = "job/query", method = RequestMethod.GET)
    @ResponseBody
    public Response query(String jsonParam) {
        JobLogRequest request = JacksonUtil.json2pojo(jsonParam, JobLogRequest.class);
        if (request == null) {
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }
        List<JobExecutionLog> jobExecutionLogs = logRepository.queryJobExecutionLog(
                request.getAppCode(),
                request.getSchedName(),
                request.getJobName(),
                request.getServerInfo(),
                request.getBeginDate(),
                request.getEndDate(),
                request.getResultCode(),
                request.getOrderColumn(),
                request.getOrderDir(),
                request.getPage());
        return new Response(DTOConvert.entity2DTO(jobExecutionLogs, JobExecutionLogDTO.class), request.getPage());
    }

    //删除日志记录
    @RequestMapping(value = "job/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public Response deleteJobLogs(String appCode, String schedName, String jobName) throws Exception {
        if (StringUtils.isBlank(appCode) || StringUtils.isBlank(schedName)) {
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }
        AuthorityUtil.getInstance().validateAuthority(appCode);
        Result result = logRepository.deleteJobExecutionLog(appCode, schedName, jobName);
        return Response.resultResponse(result);
    }

    //TODO 临时
    @RequestMapping(value = "job/temp/quartzDelete", method = RequestMethod.DELETE)
    @ResponseBody
    public Response deleteQuartzData() throws Exception {
        loginedAccount = AuthorityUtil.getInstance().getLoginedAccount();
        logRepository.clearQuartzDBData();
        return Response.SUCCESS;
    }
}
