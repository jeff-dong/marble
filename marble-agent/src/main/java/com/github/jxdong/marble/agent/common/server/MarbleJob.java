package com.github.jxdong.marble.agent.common.server;

import com.github.jxdong.marble.agent.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/10 11:27
 */
public abstract class MarbleJob {
    private static final Logger logger = LoggerFactory.getLogger(MarbleJob.class);

    //异步，不等待结果
    public void execute(String param) throws Exception{
        logger.info("JOB-{} 执行： {}", this.getClass().getName(), param);
    }

    //同步，等待返回结果
    public Result executeSync(String param) throws Exception{
        return Result.SUCCESS();
    }

    //中断后处理
    public void afterInterruptTreatment(){
        logger.info("JOB-{} 中断后处理", this.getClass().getName());

    }

}
