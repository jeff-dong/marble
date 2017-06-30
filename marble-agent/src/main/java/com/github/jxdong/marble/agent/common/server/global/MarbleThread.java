package com.github.jxdong.marble.agent.common.server.global;

import com.github.jxdong.marble.agent.common.server.MarbleJob;
import com.github.jxdong.marble.agent.common.util.ClogWrapper;
import com.github.jxdong.marble.agent.common.util.ClogWrapperFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2017/4/19 16:31
 */
public class MarbleThread implements Runnable {

    private ClogWrapper logger = ClogWrapperFactory.getClogWrapper(MarbleThread.class);
    private MarbleJob marbleJob;
    private String param;
    private Thread runThread;


    public MarbleThread(MarbleJob marbleJob, String param) {
        super();
        this.marbleJob = marbleJob;
        this.param = param;
    }

    @Override
    public void run() {
        runThread = Thread.currentThread();
        try {
            marbleJob.execute(param);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isThreadAlive() {
        return (runThread != null && runThread.isAlive());
    }

    public String getThreadName() {
        return runThread != null ? runThread.getName() : "";
    }

    public void stop() {
        //首先尝试在阻塞队列中删除
        boolean removeResult = ((ThreadPoolExecutor) ThreadPool.getFixedInstance().getExecutorService()).getQueue().remove(this);
        logger.info("Hanging MarbleJob[{}] is removed from the queue success?{}", this.getClass().getSimpleName(), removeResult);
        if (runThread != null && !runThread.isInterrupted()) {
            logger.info("Thread[{}] is interrupted", runThread.getName());
            runThread.interrupt();
        }
        if (marbleJob != null) {
            marbleJob.afterInterruptTreatment();
        }
    }

}
