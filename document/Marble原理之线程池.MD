> 本章节依赖于【Marble使用】，阅读本章节前请保证已经充分了解Marble

## 线程池概述
由于Marble属于框架性项目，用户接入Marble不关心Marble的实现机制。因此Marble在做相关处理时对资源的消耗要可控，不能因为Marble的原因导致接入的应用不可用（比如资源耗尽）。
此外，Marble-Agent每次收到RPC调度为了不阻塞都会新开线程进行JOB执行，对线程的使用非常频繁，因此必须使用同一的线程池进行Marble的资源使用收口。

对于线程池 Java已经做了很好的封装，大部分的使用场景都能覆盖，枚举如下：
1. newCachedThreadPool创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程；
2. newFixedThreadPool 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待；
3. newScheduledThreadPool 创建一个定长线程池，支持定时及周期性任务执行；
4. newSingleThreadExecutor 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行；

线程池new线程的流程：
![Paste_Image.png](https://github.com/jeff-dong/marble/blob/master/document/resource/4678905-666cb2fd65393956.png)

##Marble线程池

###线程池定义
由于Marble线程池一个很大的作用是为了控制资源使用，给Marble资源占用设定上限，Java本身提供的线程池虽然有最大线程数设置，但阻塞队列用的都是无界的，不适合做资源限定使用。因此，Marble对java线程池做了定制化。

使用有界阻塞队列
``` java
 executor = new ThreadPoolExecutor(
                tpConfig.getMaxSize(),
                tpConfig.getCoreSize(), 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(tpConfig.getBlockQueueSize()),
                tpConfig.getRejectPolicy()
        );
```

### 线程池自配置支持
为了方便用户进行线程池自配置，Marble提供配置文件的方式支持用户自定义线程池配置，配置方式为：在项目根目录下建立文件marble-config.properties 。文件中进行参数赋值，如下：
``` xml
#线程池最大线程数
tpool_max_size=5
#线程池核心线程数
tpool_core_size=5
#线程池阻塞有界队列长度
tpool_bq_size=3
#线程池满后的处理策略。1-AbortPolicy(抛出RejectedExecutionException异常); 2-CallerRunsPolicy; 3-DiscardOldestPolicy 4-DiscardPolicy(不抛出异常)
tpool_reject_policy=1
```
Marble会首先在根目录下查找此配置文件，找不到会用默认配置。
tpool_max_size=20
tpool_core_size=20
tpool_bq_size=5
tpool_reject_policy=1

Marble的配置解析类如下：
``` java

/**
 * Marble 配置解析
 *
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2017/3/31 20:15
 */
public class MarbleConfigParser {
    private static ClogWrapper logger = ClogWrapperFactory.getClogWrapper(MarbleConfigParser.class);
    private static final String CONFIG = "marble-config.properties";
    private static Properties prop = new Properties();
    //默认配置
    private static final int TPOOL_MAX_SIZE = 20;//线程池最大线程数
    private static final int TPOOL_CORE_SIZE = 20;//线程池核心线程数
    private static final int TPOOL_BQ_SIZE = 5;//线程池阻塞队列大小
    private static final int TPOOL_REJECT_POLICY = 1;//线程池满的处理策略. 1-AbortPolicy(抛出RejectedExecutionException异常）; 2-CallerRunsPolicy; 3-DiscardOldestPolicy 4-DiscardPolicy

    private MarbleConfigParser() {
        try {
            InputStream stream = PropertyUtils.class.getClassLoader().getResourceAsStream(CONFIG);
            if (stream == null) {
                logger.MARK("PARSE_CONFIG").warn("no marbleConfig.properties.xml is exist in the root directory of classpath, so default the config will be used.");
                return;
            }
            prop.load(stream);
        } catch (Exception e) {
            logger.MARK("PARSE_CONFIG").error("parse the marbleConfig.properties.xml in the root directory exception, detail: {}", Throwables.getStackTraceAsString(e));
        }
    }

    //解析出thread pool配置
    ThreadPoolConfig parseTPConfig() {
        ThreadPoolConfig tpConfig = null;
        try {
            Integer tpms = getInteger(prop, "tpool_max_size");
            Integer tpcs = getInteger(prop, "tpool_core_size");
            Integer tpqs = getInteger(prop, "tpool_bq_size");
            Integer tprp = getInteger(prop, "tpool_reject_policy");

            //修正参数
            tpcs = (tpcs == null || tpcs < 0 || tpcs > 500) ? TPOOL_CORE_SIZE : tpcs;
            tpms = (tpms == null || tpms < tpqs) ? tpcs : tpms;
            tpqs = (tpqs == null || tpqs < 0 || tpqs > 100) ? TPOOL_BQ_SIZE : tpqs;
            tprp = (tprp == null || tprp > 4) ? 1 : tprp;

            RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
            switch (tprp) {
                case 1:
                    handler = new ThreadPoolExecutor.AbortPolicy();
                    break;
                case 2:
                    handler = new ThreadPoolExecutor.CallerRunsPolicy();
                    break;
                case 3:
                    handler = new ThreadPoolExecutor.DiscardOldestPolicy();
                    break;
                case 4:
                    handler = new ThreadPoolExecutor.DiscardPolicy();
                    break;
            }
            tpConfig = new ThreadPoolConfig(tpms,tpcs,tpqs,handler);
        } catch (Exception e) {
            logger.MARK("PARSE_CONFIG").error("parse the thread-pool config from marbleConfig.properties.xml exception, detail: {}", Throwables.getStackTraceAsString(e));
        }
        if (tpConfig == null) {
            tpConfig = new ThreadPoolConfig(TPOOL_MAX_SIZE,TPOOL_CORE_SIZE, TPOOL_BQ_SIZE, new ThreadPoolExecutor.DiscardPolicy());
        }
        return tpConfig;
    }


    private Integer getInteger(Properties prop, String key) {
        Integer result = null;
        try {
            String value = prop.getProperty(key);
            if (value != null && value.trim().length() > 0) {
                result = Integer.parseInt(value);
            }
        } catch (Exception e) {
        }
        return result;
    }

    //单例
    private static class SingletonHolder {
        private static final MarbleConfigParser CONFIG_HELPER = new MarbleConfigParser();
    }

    public static MarbleConfigParser getInstance() {
        return MarbleConfigParser.SingletonHolder.CONFIG_HELPER;
    }


    //线程池配置
    class ThreadPoolConfig {
        private int maxSize;//线程池最大线程数
        private int coreSize;//线程池核心线程数
        private int blockQueueSize;//线程池阻塞队列大小
        private RejectedExecutionHandler rejectPolicy;//线程池拒绝策略

        ThreadPoolConfig(int maxSize, int coreSize, int blockQueueSize, RejectedExecutionHandler rejectPolicy) {
            this.maxSize = maxSize;
            this.coreSize = coreSize;
            this.blockQueueSize = blockQueueSize;
            this.rejectPolicy = rejectPolicy;
        }

        int getCoreSize() {
            return coreSize;
        }

        int getBlockQueueSize() {
            return blockQueueSize;
        }

        public int getMaxSize() {
            return maxSize;
        }

        RejectedExecutionHandler getRejectPolicy() {
            return rejectPolicy;
        }

        @Override
        public String toString() {
            return "ThreadPoolConfig{" +
                    "maxSize=" + StringUtils.safeString(maxSize) +
                    ", coreSize=" + StringUtils.safeString(coreSize) +
                    ", blockQueueSize=" + StringUtils.safeString(blockQueueSize) +
                    ", rejectPolicy=" + StringUtils.safeString(rejectPolicy.getClass().getSimpleName()) +
                    '}';
        }
    }
}
````
#### 线程池使用示例
以如下线程池配置为例：
tpool_max_size=5
tpool_core_size=5
tpool_bq_size=3
tpool_reject_policy=1

下图中同一台机器（10.2.37.137）连续收到11次Marble调度 >
* 第1~5次Marble-Agent成功从线程池中启动了5个线程进行执行;
* 第6~8次调用，核心线程数已满，有界阻塞队列开始进行填充;
* 第9~10次调用有界阻塞队列已被填满，最大线程数也已满，由于采用了 拒绝策略Abort，直接拒绝了10~11次的调度请求;
* 手动进行了“线程中断”调用;
* 第11次又成功执行;

![Paste_Image.png](https://github.com/jeff-dong/marble/blob/master/document/resource/4678905-b9143c5aeb827d80.png)
