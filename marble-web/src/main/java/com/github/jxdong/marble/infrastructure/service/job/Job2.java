package com.github.jxdong.marble.infrastructure.service.job;

import com.github.jxdong.marble.common.util.ClogWrapper;
import com.github.jxdong.marble.common.util.ClogWrapperFactory;
import com.github.jxdong.marble.agent.entity.Result;
import com.github.jxdong.marble.agent.common.server.MarbleJob;
import org.springframework.stereotype.Component;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2016/8/26 9:53
 */
@Component
public class Job2 extends MarbleJob {
    private ClogWrapper logger = ClogWrapperFactory.getClogWrapper(Job2.class);

    @Override
    public Result executeSync(String param) {
        logger.error("Job2开始执行...{}", this.getClass().hashCode());
        try {
            int i=0;
           // while (i<1000) {
                i++;
                if(Thread.interrupted()){
                    return Result.FAILURE("被打断");
                }
                Thread.sleep(1000 * 10);
                logger.info("JOB2-[{}]-{}-------", Thread.currentThread().getName(), i);
          //  }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.FAILURE("哈哈哈我失败了");
    }


}
