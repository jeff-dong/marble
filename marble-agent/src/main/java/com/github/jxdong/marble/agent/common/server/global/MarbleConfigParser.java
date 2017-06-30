package com.github.jxdong.marble.agent.common.server.global;

import com.github.jxdong.marble.agent.common.util.ClogWrapper;
import com.github.jxdong.marble.agent.common.util.ClogWrapperFactory;
import com.github.jxdong.marble.agent.common.util.PropertyUtils;
import com.github.jxdong.marble.agent.common.util.StringUtils;
import com.google.common.base.Throwables;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Marble 配置解析
 *
 * @author <a href="djx_19881022@163.com">jeff</a>
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
