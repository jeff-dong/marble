package com.github.jxdong.marble.global.listener;

import com.github.jxdong.common.util.JacksonUtil;
import com.github.jxdong.marble.domain.model.MarbleJobInfo;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    }

    private MarbleJobInfo getJobInfo(JobExecutionContext jec){
        if(jec != null){
            Object jobInfoObject = jec.getMergedJobDataMap().get("JOB_INFO");
            if(jobInfoObject != null){
                return JacksonUtil.json2pojo(jobInfoObject.toString(), MarbleJobInfo.class);
            }
        }
        logger.error("cannot get the Job Basic Info from MergedJobDataMap");
        return new MarbleJobInfo();
    }
}
