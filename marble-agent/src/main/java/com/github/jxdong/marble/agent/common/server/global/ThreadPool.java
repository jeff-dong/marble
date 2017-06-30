package com.github.jxdong.marble.agent.common.server.global;


import com.github.jxdong.marble.agent.common.server.MarbleJob;
import com.github.jxdong.marble.agent.common.util.ClogWrapper;
import com.github.jxdong.marble.agent.common.util.ClogWrapperFactory;
import com.github.jxdong.marble.agent.common.util.StringUtils;
import com.github.jxdong.marble.agent.entity.Result;
import com.google.common.base.Throwables;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
    private static ClogWrapper logger = ClogWrapperFactory.getClogWrapper(ThreadPool.class);
    //固定数量的线程池
    static private ThreadPool threadFixedPool = new ThreadPool();
    private Multimap<String, Object> threadMultimap = Multimaps.synchronizedMultimap(HashMultimap.<String, Object>create());
    //multimap的单个key的最大容量
    private static final int THREADMULTIMAP_SIZE = 50;
    private ExecutorService executor;

    static public ThreadPool getFixedInstance() {
        //从配置文件读取配置的线程池大小，然后设置corePoolSize 和 maximumPoolSize
        return threadFixedPool;
    }

    private ThreadPool() {
        MarbleConfigParser.ThreadPoolConfig tpConfig = MarbleConfigParser.getInstance().parseTPConfig();
        try{
            executor = new ThreadPoolExecutor(
                    tpConfig.getCoreSize(),
                    tpConfig.getMaxSize(),
                    0, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(tpConfig.getBlockQueueSize()),
                    tpConfig.getRejectPolicy()
            );
        }catch (Exception e){
            logger.MARK("MARBLE_START").POOL("POOL").error("Create ThreadPool exception, detail:{}", e);
        }

        logger.MARK("MARBLE_START").POOL("POOL").info("init the thread pool with params:{}", tpConfig);
    }

    public boolean queueContainThread(MarbleThread mt){
       return  ((ThreadPoolExecutor) ThreadPool.getFixedInstance().getExecutorService()).getQueue().contains(mt);
    }

    public boolean queueContainThreadFeature(MarbleThreadFeature mt){
        return  ((ThreadPoolExecutor) ThreadPool.getFixedInstance().getExecutorService()).getQueue().contains(mt);
    }

    public ExecutorService getExecutorService(){
        return executor;
    }
    public void execute(String reqNo, Runnable r) {
        logger.POOL("POOL").REQNO(reqNo).info("ThreadPool [async] 当前活跃线程数： {}，队列等待数：{}", activeThreadCount(), queueThreadCount());
        executor.execute(r);
    }

    public String getPoolDescInfo(){
        MarbleConfigParser.ThreadPoolConfig tpConfig = MarbleConfigParser.getInstance().parseTPConfig();
        StringBuffer sb = new StringBuffer();
        sb.append("核心线程数:").append(tpConfig.getCoreSize()).append(";队列长度:").append(tpConfig.getBlockQueueSize()).append(";异常策略:").append(tpConfig.getRejectPolicy().getClass().getSimpleName());
        return sb.toString();
    }
    //同步执行，等待返回结果
    public Result executeWithResult(String reqNo, String className, final MarbleJob marbleJob, final String  param, Long maxWaitTime) throws Exception {
        //"REQ_NO=" + reqNo + ",THREAD_POOL=" + className
        logger.POOL("POOL").REQNO(reqNo).info("ThreadPool [sync] 当前活跃线程数： {}", activeThreadCount());
        MarbleThreadFeature<Result> marbleThread ;
        /*
        FutureTask<Result> futureTask = new FutureTask<>(new Callable<Result>() {
            @Override
            public Result call() throws Exception {
                return marbleJob.executeSync(param);
            }
        });
        executor.submit(futureTask);
        logger.REQNO(reqNo).info("put the thread into ThreadMultiMap, className:{}", reqNo, className);

        ThreadPool.getFixedInstance().multimapPut(className, futureTask);
        return futureTask.get(maxWaitTime == null ? 10 : maxWaitTime, TimeUnit.MINUTES);
        */
        marbleThread = new MarbleThreadFeature<>(marbleJob, param);
        executor.submit(marbleThread);
        logger.REQNO(reqNo).info("put the thread into ThreadMultiMap, className:{}", reqNo, className);

        ThreadPool.getFixedInstance().multimapPut(className, marbleThread);
        return marbleThread.get(maxWaitTime == null ? 10 : maxWaitTime, TimeUnit.MINUTES);

    }

//    //线程池目前的剩余容量
//    public int remainingCapacity(){
//        return ((ThreadPoolExecutor)executor).getActiveCount();
//    }

    public void destroy(){
        logger.info("destroy the ThreadPool");
        if(threadMultimap!=null &&threadMultimap.size()>0){
            Iterator<Map.Entry<String, Collection<Object>>> it = threadMultimap.asMap().entrySet().iterator();
            while (it.hasNext()){
                Collection<Object> entryValue = it.next().getValue();
                Iterator itTemp = entryValue.iterator();
                while (itTemp.hasNext()) {
                    try {
                        Object itObject = itTemp.next();
                        if(itObject instanceof MarbleThread){
                            MarbleThread mt = (MarbleThread)itObject ;
                            mt.stop();
                        }else if(itObject instanceof MarbleThreadFeature){
                            MarbleThreadFeature task = (MarbleThreadFeature) itObject;
                            task.stop("SYSTEM");
                        }
                        itTemp.remove();
                    } catch (Exception e) {
                        logger.warn("destroy the ThreadPool, exception, detail:{}", e);
                    }
                }
                it.remove();
            }
        }
        if(executor != null && !executor.isTerminated()){
            executor.shutdownNow();
        }
    }
    //根据className中断对应的线程
    public boolean stopJobThread(String operator, String className) {
        logger.POOL("POOL").info("ThreadPool try to stop the threads of [{}], operator:{}", className, operator);
        if (StringUtils.isNotBlank(className)) {
            Collection collection = ThreadPool.getFixedInstance().getThreadMultimap().get(className);
            if (collection != null && collection.size() > 0) {
                logger.POOL("POOL").info("ThreadPool found {} threads need to interpet", collection.size() );
                Iterator it = collection.iterator();
                while (it.hasNext()) {
                    MarbleThread mt = null;
                    try {
                        Object itObject = it.next();
                        if(itObject instanceof MarbleThread){
                            mt = (MarbleThread)itObject ;
                            mt.stop();
                            logger.POOL("POOL").info("ThreadPool stop one thread({}) of [{}] success,", mt.getThreadName(), className);
                        }else if(itObject instanceof MarbleThreadFeature){
                            MarbleThreadFeature task = (MarbleThreadFeature) itObject;
                            task.stop(operator);
                            logger.POOL("POOL").info("ThreadPool stop MarbleThreadFeature of [{}] success,", className);
                        }
                        it.remove();
                    } catch (Exception e) {
                        logger.POOL("POOL").error("ThreadPool stop one thread({}) of [{}] failed, detail:{}", (mt != null ? mt.getThreadName() : ""), className, Throwables.getStackTraceAsString(e));
                    }
                }
            }else{
                logger.POOL("POOL").info("ThreadPool stop thread({}) end. no thread found", className);
            }
        }
        return true;
    }

    //得到活跃线程数
    private int activeThreadCount() {
        return (executor == null || !(executor instanceof ThreadPoolExecutor)) ?-1:((ThreadPoolExecutor) executor).getActiveCount();
    }

    private int queueThreadCount(){
        return (executor == null || !(executor instanceof ThreadPoolExecutor))? -1:((ThreadPoolExecutor) executor).getQueue().size();
    }

    public ThreadPool multimapPut(String key, Object value) {
        if (StringUtils.isNotBlank(key)) {
            Collection collection = threadMultimap.get(key);
            if (collection != null && collection.size() >= THREADMULTIMAP_SIZE) {
                //替换最久的
                Iterator<Object> it = collection.iterator();
                //首先进行 非活跃线程清理
                while (it.hasNext()) {
                    Object tempObj = it.next();
                    if(tempObj instanceof MarbleThread){
                        MarbleThread mt = (MarbleThread)tempObj;
                        //不活跃删除
                        if(!mt.isThreadAlive()){
                            it.remove();
                        }
                    }else if(tempObj instanceof MarbleThreadFeature){
                        MarbleThreadFeature mf = (MarbleThreadFeature) tempObj;
                        //完成的线程删除
                        if(mf.isDone()){
                            it.remove();
                        }
                    }
                }
                //仍然>最大值，删除最久未使用
                if(collection.size() >= THREADMULTIMAP_SIZE){
                    while (it.hasNext()) {
                        it.next();
                        it.remove();
                        break;
                    }
                }
                threadMultimap.put(key, value);
                return this;
            }
        }
        threadMultimap.put(key, value);
        return this;
    }

    public Multimap<String, Object> getThreadMultimap() {
        return threadMultimap;
    }
}