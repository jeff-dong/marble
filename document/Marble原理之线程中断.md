> 本章节依赖于【Marble使用】，阅读本章节前请保证已经充分了解Marble。
中断特性从Marble-Agent 2.0.5开始支持。

###线程中断使用
1. 引入marble-agent jar包
``` xml
<dependency>
            <groupId>com.xxx.xxx</groupId>
            <artifactId>marble-agent</artifactId>
            <version>2.0.5</version>
</dependency>
```
2. JOB执行代码适当位置添加中断标志, 下面给出示例代码
``` java
@Component("job1")
public class Job1 extends MarbleJob {
    private ClogWrapper logger = ClogWrapperFactory.getClogWrapper(Job1.class);

    @Override
    public void execute(String param) throws Exception {
        logger.info("JOB1开始执行 ...");
        int i = 0;
        while (true) {
            i++;
            //1、用中断状态码进行判断
            if (Thread.interrupted()) {
                logger.info("JOB1-[{}]-[{}]被打断啦", param, Thread.currentThread().getName());
                return;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                //2、捕获终端异常后return结束
                return;
            }
            logger.info("JOB1-[{}]-[{}]-{}-------", param, Thread.currentThread().getName(), i);
        }
    }
}
```
3. Marble OFFLINE进行线程中断

3.1 手动调度线程中断

![Marble-线程中断1.jpg](https://github.com/jeff-dong/marble/blob/master/document/resource/4678905-b2f772e124219867.jpg)

3.2 选择要中断的服务器进行终端尝试
![Marble-线程中断(选择服务器).jpg](https://github.com/jeff-dong/marble/blob/master/document/resource/4678905-b192bb1edf275c81)

3.3 查看中断日志（同步JOB）
![Marble-线程中断(log).jpg](https://github.com/jeff-dong/marble/blob/master/document/resource/4678905-5d008fef53039e9b)

###中断实现及原理

#### Java的线程中断
Java的线程中断机制是一种协作机制，线程中断并不能立即停掉线程执行，相反，可能线程永远都不会响应。
java的线程中断模型只是通过修改线程的中断标志（interrupt）进行中断通知，不会有其它额外操作，因此线程是否最终中断取决于线程的执行逻辑。因此，如果想让线程按照自己的想法中断，要代码中事先进行中断的“埋点”处理。
>有人可能会想到Thread的stop方法进行中断，由于此方法可能造成不可预知的结果，已经被抛弃

#### Marble进行线程中断实现

##### 需求收集
1. 以JOB为维度进行线程中断；
2. 尽量做到实时响应；
3. 存在集群中多台机器，要支持指定某台机器中的线程中断；
4. 允许多次中断尝试；
5. 中断请求不能依赖于JOB当前状态。可能已经停止调度的JOB也要手动中断执行中的线程；
6. 透明和扩展不同JOB的中断（提供用户中断的"后处理"扩展）；

##### 需求分析及实现
【以JOB为维度进行线程中断】
> Marble的JOB标志为 schedulerName-appId-jobName组成，目前Marble每个JOB调度时间和频率都是个性化，目前调度完成就销毁。但要做到任何时间进行执行中的线程中断就要：
1.1 存储JOB的运行线程，随时准备中断；
1.2 在缓存的JOB数量/时间和性能间做权衡，不能过多也不能过少；
1.3 制定缓存已满时的抛弃策略，避免缓存被占满新的线程永远无法中断；
1.4 要同步JOB和异步JOB透明处理（感觉不出差异）；

实现：
Marble的线程池中定义支持并发的MAP进行JOB维度的线程缓存，此外指定每个JOB下缓存的线程数量。如下：
``` java
public class ThreadPool {
    ...
    private Multimap<String, Object> threadMultimap = Multimaps.synchronizedMultimap(HashMultimap.<String, Object>create());
    //multimap的单个key的最大容量
    private static final int THREADMULTIMAP_SIZE = 50;
    ...
}
```
Marble-Agent在同步/异步JOB生成新的线程对象时进行放入MAP缓存，如果缓存（50个）已满采用如下策略进行处理：
1. 尝试清理当前map中的非活跃线程；
2. 尝试清理当前map中已经完成的线程（同步线程有效）；
3. 如果还未清理出空间，移除最久的线程；
``` java
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
```
此外，为了能在JVM关闭时进行线程中断，添加JVM hook进行中断调用处理（包括线程池的销毁）。
除此之外，还有个小问题，由于线程池使用的是有界的阻塞队列，此种情况下，线程中断时可能有的线程存在于阻塞队列中，单纯的中断无效，对于此类情况，要首先判断阻塞队列中是否存在要中断的线程，存在的话进行队列的移除操作。

【尽量做到实时响应】
只能通过用户在具体的线程逻辑中进行埋点处理，Marble在框架层面除了及时把用户的中断请求送达之外，没有其它措施。

【存在集群中多台机器，要支持指定某台机器中的线程中断】
Marble OFFLINE的中断页面支持机器的选择，用户进行选择后，Marble会有针对性的进行机器的中断RPC发送。

【允许多次中断尝试】
OFFLINE未对中断次数进行限制，目前支持多次中断请求发送。

【中断请求不能依赖于JOB当前状态】
考虑到用户对历史线程的中断请求，Marble未把中断操作绑定在JOB状态上，任何JOB都可以进行终端尝试。

【透明扩展不同JOB的中断】
Marble目前支持同步和异步JOB，两类JOB的中断处理并不一致，比如同步job的中断是通过FeatureTask的cancel实现，异步JOB是通过Thread的interrupt实现，此外线程被中断后Marble希望能更进一步提供一个统一的“后处理”操作给用户自己实现，比如用户可能需要在线程被中断后进行一些后续的log记录等。

为了代码层面一致透明，且友好的实现“后处理”的封装，Marble使用了代理模式，在Thread和FeatureTask上添加了一层“代理类”，由代理进行具体的中断操作。
同步JOB代理类：
``` java

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
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
        //中断后处理
        if(marbleJob != null){
            marbleJob.afterInterruptTreatment();
        }
    }

}
```
异步JOB代理类：
``` java

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
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
        //中断后处理
        if (marbleJob != null) {
            marbleJob.afterInterruptTreatment();
        }
    }
}
```