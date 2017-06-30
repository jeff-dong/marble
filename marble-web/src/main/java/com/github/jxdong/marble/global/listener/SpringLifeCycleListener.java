package com.github.jxdong.marble.global.listener;

import com.github.jxdong.marble.common.util.PropertyUtils;
import com.github.jxdong.marble.global.util.SpringContextUtil;
import com.github.jxdong.marble.infrastructure.service.netty.WebNettyClientManager;
import com.github.jxdong.marble.infrastructure.service.netty.WebNettyServerManager;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author tangb
 *         该类和TomcatOnOffMonitor为两种不同应用开启和关闭时执行任务的方法。本应用使用Spring 方法。
 */
@Component
public class SpringLifeCycleListener implements ApplicationListener {
    private static final Logger logger = LoggerFactory.getLogger(SpringLifeCycleListener.class);

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        if (event instanceof ContextStartedEvent) {
            logger.info("The spring starts... , Version: {}", PropertyUtils.getString("Version"));
        } else if (event instanceof ContextRefreshedEvent) {
            if (((ContextRefreshedEvent) event).getApplicationContext().getParent() == null) {
                logger.info("The spring starts successfully. Version: {}", PropertyUtils.getString("Version"));
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        WebNettyServerManager.getInstance().run(PropertyUtils.getMarbleServerPort());
                    }
                });
                executorService.shutdown();
            }
        } else if (event instanceof ContextClosedEvent) {
            logger.info("The spring is closing... , Version: {}", PropertyUtils.getString("Version"));
            closeQuartzSched();
            closeClog();
        }
    }

    private void closeQuartzSched() {
        System.out.println("[Marble Scheduler] 开始关闭");
        //ForkJoinPool.defaultForkJoinWorkerThreadFactory;
        WebNettyClientManager.getInstance().destroy();
        //关闭Scheduler
        Scheduler scheduler = (Scheduler) SpringContextUtil.getBean("marbleScheduler");
        if (scheduler == null) {
            return;
        }
        for (int i = 0; i < 3; i++) {
            try {
                scheduler.shutdown(false);
                if (scheduler.isShutdown()) {
                    break;
                } else {
                    Thread.sleep(300);
                }
            } catch (Exception e) {
                logger.info("关闭Quartz Scheduler异常： ", e);
            }
        }

        System.out.println("[Marble Scheduler] 关闭结束");
    }

    private void closeClog() {
    }
}
