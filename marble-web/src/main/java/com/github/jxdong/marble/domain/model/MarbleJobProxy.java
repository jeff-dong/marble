package com.github.jxdong.marble.domain.model;

import com.github.jxdong.common.util.ArrayUtils;
import com.github.jxdong.common.util.JacksonUtil;
import com.github.jxdong.common.util.StringUtils;
import com.github.jxdong.marble.domain.model.enums.JobExecStatusEnum;
import com.github.jxdong.marble.entity.ClassInfo;
import com.github.jxdong.marble.infrastructure.service.LogService;
import com.github.jxdong.marble.infrastructure.service.RPCClientFactory;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/11/11 14:50
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MarbleJobProxy implements Job {
    private static final Logger logger = LoggerFactory.getLogger(MarbleJobProxy.class);


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            //从DataMap中取得MarbleJobInfo信息
            final MarbleJobInfo marbleJobInfo = JacksonUtil.json2pojo(jobExecutionContext.getMergedJobDataMap().get("MARBLE_JOB_INFO").toString(), MarbleJobInfo.class);
            if (marbleJobInfo != null && ArrayUtils.listIsNotBlank(marbleJobInfo.getServerInfoList()) && StringUtils.isNotBlank(marbleJobInfo.getServiceName())) {
                //随机得到一个server，准备建立连接
                final MarbleServerInfo serverInfo = marbleJobInfo.getConnectServerRandomly();
                if(serverInfo ==null || serverInfo.getPort() <0 || serverInfo.getPort() >65535 || StringUtils.isBlank(serverInfo.getIp())){
                    logger.error("Job execute failed. Generated connect server info is illegal. {} ", serverInfo);
                    return;
                }
                String jobParam = marbleJobInfo.getJobParam();
                String marbleVersion = marbleJobInfo.getMarbleVersion();

                //更新data map
                jobExecutionContext.getMergedJobDataMap().put("MARBLE_JOB_INFO", JacksonUtil.obj2json(marbleJobInfo));

                //与服务端创建连接并执行
                logger.info("{}, MarbleVersion:{}执行. {}", marbleJobInfo.getServiceName(), marbleVersion, serverInfo);

                Set<ClassInfo> classInfoSet = new HashSet<>();
                classInfoSet.add(new ClassInfo(marbleJobInfo.getServiceName(), "execute", jobParam));
                //根据得到的Marble-Version动态选择调用方式
                final Result result = RPCClientFactory.getClientManager(marbleVersion).serviceInvoke(serverInfo.getIp(), serverInfo.getPort(), classInfoSet);

                //日志记录
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        LogService.getInstance().addJobExecutionLog(new JobExecutionLog(marbleJobInfo.getAppCode(),
                                marbleJobInfo.getSchedName(),
                                marbleJobInfo.getJobName(),
                                marbleJobInfo.getJobCronExpress(),
                                StringUtils.safeString(serverInfo.getIp()) + ":" + serverInfo.getPort() ,
                                result.isSuccess() ? JobExecStatusEnum.SUCCESS : JobExecStatusEnum.FAILURE,
                                result.getResultMsg()));
                    }
                });
                executorService.shutdown();

                if(!result.isSuccess()){
                    throw new JobExecutionException(result.getResultMsg());
                }
            }else{
                logger.error("Job execute failed. MarbleJobInfo is illegal. {} ", marbleJobInfo);
            }
        }catch (Exception e) {
            logger.error("Job execute exception. Exception detail: ", e);
            throw new JobExecutionException(e);
        }
    }



}
