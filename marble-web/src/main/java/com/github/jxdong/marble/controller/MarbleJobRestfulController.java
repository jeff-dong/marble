package com.github.jxdong.marble.controller;

import com.github.jxdong.marble.domain.dto.JobExecResponse;
import com.github.jxdong.marble.domain.model.JobDetail;
import com.github.jxdong.marble.domain.model.JobExecutionLog;
import com.github.jxdong.marble.domain.model.Result;
import com.github.jxdong.marble.domain.model.enums.JobExecStatusEnum;
import com.github.jxdong.marble.domain.model.enums.JobReqStatusEnum;
import com.github.jxdong.marble.domain.repositories.SchedRepository;
import com.github.jxdong.marble.agent.entity.ClassInfo;
import com.github.jxdong.marble.infrastructure.service.LogManager;
import com.github.jxdong.marble.infrastructure.service.RPCClientFactory;
import com.github.jxdong.marble.common.util.ArrayUtils;
import com.github.jxdong.marble.common.util.ClogWrapper;
import com.github.jxdong.marble.common.util.ClogWrapperFactory;
import com.github.jxdong.marble.common.util.StringUtils;
import com.github.jxdong.marble.agent.common.server.thrift.ThriftConnectInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Marble JOB 对外提供restful服务
 *
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2017/06/22 13:05
 */
@RestController
@RequestMapping("/restful/api/job")
public class MarbleJobRestfulController extends BasicController {
    private ClogWrapper logger = ClogWrapperFactory.getClogWrapper(MarbleJobRestfulController.class);
    @Autowired
    private SchedRepository schedRepository;
    @Autowired
    private LogManager logManager;

    //JOB 启动
    @RequestMapping(value = "/exec/{app}-{sched}-{job}", method = RequestMethod.PUT)
    @ResponseBody
    public JobExecResponse marbleJobExec(@PathVariable("app") String app, @PathVariable("sched") String sched, @PathVariable("job") String job, @RequestParam(value = "token") String token) {
        String MARK = "RESTFUL_JOB_EXEC";
        logger.MARK(MARK).info("[JOB EXEC] received the request to exec job: {}-{}-{}", app, sched, job);
        //拼死字符串进行简单校验
        if(!"hwdfjk2bkas4jfd".equalsIgnoreCase(token)){
            return JobExecResponse.FAILURE(null, null, null,1010, "请求未被授权");
        }
        if (StringUtils.isBlank(app) || StringUtils.isBlank(sched) || StringUtils.isBlank(job)) {
            return JobExecResponse.FAILURE(app, sched, job,1010, "参数不合法: JobKey必填");
        }

        //根据参数查询JOB信息
        List<JobDetail> jobDetails = schedRepository.queryJob(app, sched, job);
        if (ArrayUtils.listIsBlank(jobDetails)) {
            return JobExecResponse.FAILURE(app, sched, job,1010, "查询不到任何JOB信息");
        }
        JobDetail jobDetail = jobDetails.get(0);

        if (jobDetail.getConnectInfo() == null) {
            return JobExecResponse.FAILURE(app, sched, job,1000, "JOB没有配置服务器信息");
        }
        ThriftConnectInfo.Server server = jobDetail.getConnectInfo().getOneRandomServer();
        if (server == null || StringUtils.isBlank(server.getIp()) || server.getPort() <= 0) {
            return JobExecResponse.FAILURE(app, sched, job,1000, "获取JOB服务器信息失败");
        }
        //生成UUID
        String requestNo = StringUtils.genUUID();

        // 调度执行一次
        Set<ClassInfo> classInfoSet = new HashSet<>();
        classInfoSet.add(new ClassInfo(sched + "-" + app + "-" + job, "execute", jobDetail.getParam()));
        jobDetail.getConnectInfo().getOneRandomServer();
        //根据得到的Marble-Version动态选择调用方式
        logger.MARK(MARK).info("[JOB EXEC] begin to send 'exec job' request by Netty");
        Result result = RPCClientFactory.getClientManager(requestNo, "2.0.0").serviceInvoke(requestNo, server.getIp(), server.getPort(), classInfoSet, jobDetail.isSynchronous(), jobDetail.getMaxWaitTime());
        logger.MARK(MARK).info("[JOB EXEC] send 'exec job' request by Netty end, request result:{}", result);
        if (!result.isSuccess()) {
            return JobExecResponse.FAILURE(app, sched, job,1000, "JOB RPC未成功："+result.getResultMsg());
        }
        Result dbResult = logManager.addJobExecutionLog(new JobExecutionLog(
                requestNo, app, sched, job, jobDetail.getCronExpress(), server.getIp() + ":" + server.getPort(),
                JobReqStatusEnum.SUCCESS, "", jobDetail.isSynchronous() ? "[API]同步JOB (最长等待" + StringUtils.safeString(jobDetail.getMaxWaitTime()) + "分钟)" : "[API]异步JOB",
                jobDetail.isSynchronous()? JobExecStatusEnum.REQUESTING.getCode():0,null
        ));
        logger.MARK(MARK).info("[JOB EXEC] persist 'exec job' request to DB, result:{}", dbResult);

        return JobExecResponse.SYNC_SUCCESS(app, sched, job, jobDetail.isSynchronous(), requestNo);
    }

    //JOB 根据流水号查询JOB执行状态
    @RequestMapping(value = "/exec/log/{transno}", method = RequestMethod.GET)
    @ResponseBody
    public JobExecResponse queryMableJobExecLog(@PathVariable("transno") String transno) {
        String MARK = "RESTFUL_JOB_LOG";
        logger.MARK(MARK).info("[JOB EXEC] received the request to query job log by transNO: {}", transno);

        if (StringUtils.isBlank(transno)) {
            return JobExecResponse.FAILURE(null,null,null,1010, "参数不合法: transNo必填");
        }

        //根据transNo查询日志信息
        JobExecutionLog jobExecutionLog = logManager.queryJobExecutionLogByReqNo(transno);
        if(jobExecutionLog==null){
            return JobExecResponse.FAILURE(null,null,null,1000, "未查询到任何记录");
        }
        //根据参数查询JOB信息
        List<JobDetail> jobDetails = schedRepository.queryJob(jobExecutionLog.getAppCode(), jobExecutionLog.getSchedName(), jobExecutionLog.getJobName());
        if (ArrayUtils.listIsBlank(jobDetails)) {
            return JobExecResponse.FAILURE(null, null, null,1000, "查询不到任何JOB信息");
        }

        return JobExecResponse.SUCCESS(jobExecutionLog, jobDetails.get(0).isSynchronous());
    }
}
