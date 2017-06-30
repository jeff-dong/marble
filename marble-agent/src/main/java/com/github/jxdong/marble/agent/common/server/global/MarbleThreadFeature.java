package com.github.jxdong.marble.agent.common.server.global;

import com.github.jxdong.marble.agent.common.util.ClogWrapper;
import com.github.jxdong.marble.agent.common.util.ClogWrapperFactory;
import com.github.jxdong.marble.agent.entity.Result;
import com.github.jxdong.marble.agent.common.server.MarbleJob;

import java.util.concurrent.*;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2017/4/19 16:31
 */
public class MarbleThreadFeature<V> implements RunnableFuture<V> {

    private ClogWrapper logger = ClogWrapperFactory.getClogWrapper(MarbleThreadFeature.class);
    private MarbleJob marbleJob;
    private String param;
    private FutureTask<Result> futureTask;


    public MarbleThreadFeature(final MarbleJob marbleJob, final String param) {
        super();
        this.marbleJob = marbleJob;
        this.param = param;
        futureTask = new FutureTask<>(new Callable<Result>() {
            @Override
            public Result call() throws Exception {
                return marbleJob.executeSync(param);
            }
        });
    }


    @Override
    public void run() {
        futureTask.run();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return futureTask.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return futureTask.isCancelled();
    }

    @Override
    public boolean isDone() {
        return futureTask.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return (V) futureTask.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return (V) futureTask.get(timeout, unit);
    }

    public void stop(String operator) {
        if (futureTask != null && !futureTask.isCancelled()) {
            logger.info("Thread-feature[{}] is interrupted", futureTask.getClass().getName());
            futureTask.cancel(true);
        }else if(marbleJob != null){
            boolean removeResult = ((ThreadPoolExecutor) ThreadPool.getFixedInstance().getExecutorService()).getQueue().remove(marbleJob);
            logger.info("Hanging MarbleJob[{}] is removed from the queue success?{}", marbleJob.getClass().getSimpleName(),removeResult);
        }
        if(marbleJob != null){
            marbleJob.afterInterruptTreatment();
        }
    }

}
