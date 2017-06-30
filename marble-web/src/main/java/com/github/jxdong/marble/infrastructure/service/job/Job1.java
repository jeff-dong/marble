package com.github.jxdong.marble.infrastructure.service.job;

import com.github.jxdong.marble.common.util.ClogWrapper;
import com.github.jxdong.marble.common.util.ClogWrapperFactory;
import com.github.jxdong.marble.agent.common.server.MarbleJob;
import org.springframework.stereotype.Component;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2016/8/26 9:53
 */
@Component("job1")
public class Job1 extends MarbleJob {
    private ClogWrapper logger = ClogWrapperFactory.getClogWrapper(Job1.class);

    @Override
    public void execute(String param) throws Exception {
        logger.info("JOB1开始执行 ...");
        int i = 0;
        while (i<10000) {//true
            i++;
            //用中断状态码进行判断
            if (Thread.interrupted()) {
                logger.info("JOB1-[{}]-[{}]被打断啦", param, Thread.currentThread().getName());
                return;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // 被中断后return结束
                return;
            }
            logger.info("JOB1-[{}]-[{}]-{}-------", param, Thread.currentThread().getName(), i);
        }

    }
}
