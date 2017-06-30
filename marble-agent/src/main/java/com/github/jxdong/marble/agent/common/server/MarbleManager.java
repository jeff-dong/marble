package com.github.jxdong.marble.agent.common.server;

import com.github.jxdong.marble.agent.common.server.netty.server.NettyServer;
import com.github.jxdong.marble.agent.common.server.spring.JobBeanConfig;
import com.github.jxdong.marble.agent.common.util.StringUtils;
import com.github.jxdong.marble.agent.common.util.ClogWrapper;
import com.github.jxdong.marble.agent.common.util.ClogWrapperFactory;
import com.github.jxdong.marble.agent.common.server.spring.MarbleSchedulerBean;
import com.google.common.base.Throwables;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/10 14:05
 */
public class MarbleManager {
    private static final ClogWrapper logger = ClogWrapperFactory.getClogWrapper(MarbleManager.class);

    private ApplicationContext applicationContext;
    private Map<String, MarbleSchedulerBean> schedulerMap = new ConcurrentHashMap<>();
    //存储key与marble job的映射关系
    private Map<String, MarbleJob> marbleJobMap = new ConcurrentHashMap<>();

    //private TSimpleServer server = null;
    //信号量，用来多线程互斥
    private volatile boolean serverStarted = false;

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
        if (this.schedulerMap == null || schedulerMap.size() == 0) {
            logger.MARK("MARBLE_START").error("Start Netty Server failed. Illegal arguments. Detail: {}", schedulerMap);
            return;
        }
        if(serverStarted){
            logger.MARK("MARBLE_START").info("Netty has been started, ignore. schedulerMap:{}", schedulerMap);
            return;
        }
        serverStarted = true;
        ExecutorService executor = null;
        try {

            executor = Executors.newFixedThreadPool(schedulerMap.size());
            for (final Map.Entry<String, MarbleSchedulerBean> entry : schedulerMap.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                final MarbleSchedulerBean schedulerBean = entry.getValue();
                if (schedulerBean.getPort() <= 0 || schedulerBean.getPort() >= 65535) {
                    logger.MARK("MARBLE_START").error("Illegal socket port of this scheduler, ignore. Scheduler Info: ", schedulerBean);
                    continue;
                }

                List<JobBeanConfig> jobs = schedulerBean.getJobs();
                if (jobs == null || jobs.size() == 0) {
                    logger.MARK("MARBLE_START").error("No jobs found in this scheduler, ignore. Scheduler Info: ", schedulerBean);
                    continue;
                }
                //遍历Job放入marbleJobMap
                for (JobBeanConfig jobBeanConfig : jobs) {
                    if (jobBeanConfig != null && jobBeanConfig.getRef() != null) {
                        String jobRegName = entry.getKey() + "-" + jobBeanConfig.getName();
                        logger.MARK("MARBLE_START").info("Netty: Register Job({}) on port({})", jobRegName, schedulerBean.getPort());
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
            }
            executor.shutdown();
        } catch (Exception e) {
            logger.MARK("MARBLE_START").error("Run service exception.Detail:{}", Throwables.getStackTraceAsString(e));
        }finally {
            if(executor != null){
                executor.shutdown();
            }
        }
    }

    public void stopServer() {
        try {
            NettyServer.getInstance().stop();
            serverStarted = false;
            logger.info("stop the server successfully");
        } catch (Exception e) {
            logger.error("failed to stop server. error: {}", Throwables.getStackTraceAsString(e));
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
