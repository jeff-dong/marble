package com.github.group.marble1.service;

import com.github.jxdong.marble.domain.model.JobExecutionLog;
import com.github.jxdong.marble.domain.model.enums.JobExecStatusEnum;
import com.github.jxdong.marble.global.util.DateUtil;
import com.github.jxdong.marble.infrastructure.service.LogManager;
import com.github.jxdong.marble.infrastructure.service.netty.WebNettyServerManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/16 17:06
 */
public class MarbleTest {

    @Autowired
    private LogManager logManager;

    @Test
    public void testJob(){
        System.out.println("开始：" + DateUtil.formateDate(new Date()));
        ExecutorService executor = Executors.newFixedThreadPool(1);
        for(int i=0;i<10;i++){
            System.out.println("执行第" + i + "条");
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    logManager.addJobExecutionLog(new JobExecutionLog("reqNo","appCode", "schedName", "jobName", "jobCronExpress", "serverInfo", JobExecStatusEnum.SUCCESS, null, ""));
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        executor.shutdown();
        System.out.println("结束："+DateUtil.formateDate(new Date()));
    }


    public static void main(String args[]) {
        WebNettyServerManager.getInstance().run(9001);
    }

}
