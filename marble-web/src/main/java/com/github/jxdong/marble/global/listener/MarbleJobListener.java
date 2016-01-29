package com.github.jxdong.marble.global.listener;

import com.github.jxdong.common.util.JacksonUtil;
import com.github.jxdong.marble.domain.model.JobBasicInfo;
import com.github.jxdong.marble.global.util.SpringContextUtil;
import com.github.jxdong.marble.infrastructure.service.LogManager;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/11/17 10:16
 */

public class MarbleJobListener implements JobListener{
    private static final Logger logger = LoggerFactory.getLogger(MarbleJobListener.class);

    @Override
    public String getName() {
        return "Marble Job Listener1";
    }
    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {

        //logger.info("Job即将被执行.{}", jobExecutionContext);
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {
        logger.info("Job执行被拒绝.{}", jobExecutionContext);
    }

    @Override
    public void jobWasExecuted(final JobExecutionContext jobExecutionContext, final JobExecutionException e) {
        //logger.info("Job执行完成。{}", jobExecutionContext);
        final JobBasicInfo jobBasicInfo = getJobInfo(jobExecutionContext);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try{
            //记录日志
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    LogManager logManager = (LogManager) SpringContextUtil.getBean("logManager", LogManager.class);
                    logManager.addJobExecutionLog(jobBasicInfo.convert2JobLog(e));
                }
            });
        }catch (Exception ex){
            logger.error("Marble Job Listener exception. detail: ", ex);
        }finally {
            executor.shutdown();
        }

    }

    private JobBasicInfo getJobInfo(JobExecutionContext jec){
        if(jec != null){
            Object jobInfoObject = jec.getMergedJobDataMap().get("JOB_INFO");
            if(jobInfoObject != null){
                return JacksonUtil.json2pojo(jobInfoObject.toString(), JobBasicInfo.class);
            }
        }
        logger.error("cannot get the Job Basic Info from MergedJobDataMap");
        return new JobBasicInfo();
    }
}
