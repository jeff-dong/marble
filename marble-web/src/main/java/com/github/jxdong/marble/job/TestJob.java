package com.github.jxdong.marble.job;

import com.github.jxdong.marble.server.MarbleJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2016/1/14 19:04
 */
@Component
public class TestJob extends MarbleJob{
    private static final Logger logger = LoggerFactory.getLogger(TestJob.class);

    @Override
    public void execute(String param) {
       logger.info("开始执行： {}", param);
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("执行结束");
    }
}
