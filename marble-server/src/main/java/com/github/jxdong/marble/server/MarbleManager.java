package com.github.jxdong.marble.server;

import com.github.jxdong.common.util.StringUtils;
import com.github.jxdong.marble.server.netty.NettyServer;
import com.github.jxdong.marble.server.spring.JobBeanConfig;
import com.github.jxdong.marble.server.spring.MarbleSchedulerBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/11/10 14:05
 */
public class MarbleManager {
    private static final Logger logger = LoggerFactory.getLogger(MarbleManager.class);

    private ApplicationContext applicationContext;
    private Map<String, MarbleSchedulerBean> schedulerMap = new HashMap<>();
    //存储key与marble job的映射关系
    private Map<String, MarbleJob> marbleJobMap = new HashMap<>();

    //private TSimpleServer server = null;
    //信号量，用来多线程互斥
    private boolean serverStarted = false;

    //多线程下的单例
    private MarbleManager() {
    }

    private static class SigletonHolder {
        private static final MarbleManager MARBLE_HELPER = new MarbleManager();
    }

    public static MarbleManager getInstance() {
        return SigletonHolder.MARBLE_HELPER;
    }

    public void registerScheduler(String name, MarbleSchedulerBean schedulerBean) {
        if (name != null && schedulerBean != null) {
            schedulerMap.put(name, schedulerBean);
        }
    }

    @SuppressWarnings({"unchecked"})
    public synchronized void startNettyServer() {
        if (this.schedulerMap == null || schedulerMap.size() == 0 || serverStarted) {
            logger.error("Illegal arguments. Detail: ", schedulerMap);
            return;
        }
        serverStarted = true;
        try {

            ExecutorService executor = Executors.newFixedThreadPool(schedulerMap.size());
            for (final Map.Entry<String, MarbleSchedulerBean> entry : schedulerMap.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                final MarbleSchedulerBean schedulerBean = entry.getValue();
                if (schedulerBean.getPort() <= 0 || schedulerBean.getPort() >= 65535) {
                    logger.error("Illegal socket port of this scheduler, ignore. Scheduler Info: ", schedulerBean);
                    continue;
                }

                List<JobBeanConfig> jobs = schedulerBean.getJobs();
                if (jobs == null || jobs.size() == 0) {
                    logger.error("No jobs found in this scheduler, ignore. Scheduler Info: ", schedulerBean);
                    continue;
                }
                //遍历Job放入marbleJobMap
                for (JobBeanConfig jobBeanConfig : jobs) {
                    if (jobBeanConfig != null && jobBeanConfig.getRef() != null) {
                        String jobRegName = entry.getKey() + "-" + jobBeanConfig.getName();
                        logger.info("Netty: Register Job({}) on port({})", jobRegName, schedulerBean.getPort());
                        marbleJobMap.put(jobRegName, jobBeanConfig.getRef());
                    }
                }
                //开启新线程执行
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        NettyServer.getInstance().run(schedulerBean.getPort());
                    }
                });
                executor.shutdown();
            }
        } catch (Exception e) {
            logger.error("Run service exception.", e);
        }
    }

    public void stopServer() {
        try {
            NettyServer.getInstance().stop();
            serverStarted = false;
            logger.info("stop the server successfully");
        } catch (Exception e) {
            logger.error("failed to stop server. error: ", e);
        }
    }

    public MarbleManager setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return this;
    }

    public MarbleJob getMarbleJobByKey(String key) {
        if (StringUtils.isNotBlank(key)) {
            return marbleJobMap.get(key);
        }
        return null;
    }
}
