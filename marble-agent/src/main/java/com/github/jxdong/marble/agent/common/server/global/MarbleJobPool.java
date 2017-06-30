package com.github.jxdong.marble.agent.common.server.global;

import com.github.jxdong.marble.agent.common.util.ClogWrapper;
import com.github.jxdong.marble.agent.common.util.ClogWrapperFactory;
import com.github.jxdong.marble.agent.entity.Result;
import com.github.jxdong.marble.agent.common.server.netty.client.NettyClientManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Marble JOB pool
 *
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2017/3/31 20:15
 */
public class MarbleJobPool {
    private static ClogWrapper logger = ClogWrapperFactory.getClogWrapper(MarbleJobPool.class);

    private Map<String, MarbleJob> processingJobMap = new ConcurrentHashMap<>();
    //默认配置
    private static final int TPOOL_SIZE = 100;//存储的job数

    public MarbleJobPool addProcessingJob(String reqNo, boolean isSync, String marbleServerIp,Integer marbleServerPort){
        processingJobMap.put(reqNo, new MarbleJob(isSync, marbleServerIp, marbleServerPort));
        return this;
    }

    public MarbleJobPool removeProcessingJob(String reqNo){
        processingJobMap.remove(reqNo);
        return this;
    }

    public Map<String, MarbleJob> getProcessingJobMap() {
        return processingJobMap;
    }

    private MarbleJobPool() {
    }

    //单例
    private static class SingletonHolder {
        private static final MarbleJobPool CONFIG_HELPER = new MarbleJobPool();
    }

    public void destroy(){
        Map<String, Object> data = new HashMap<>();
        data.put("EXEC_RESULT", Result.FAILURE("执行失败, 原因: 服务关闭,执行终止" ));
        if (processingJobMap != null && processingJobMap.size() > 0) {
            Iterator<Map.Entry<String, MarbleJob>> it = processingJobMap.entrySet().iterator();
            while(it.hasNext()) {
                try{
                    Map.Entry<String, MarbleJobPool.MarbleJob> entry = it.next();
                    NettyClientManager.getInstance().serviceInvoke(entry.getKey(), entry.getValue().getMarbleServerIp(), Integer.valueOf(entry.getValue().getMarbleServerPort()), data);
                    it.remove();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static MarbleJobPool getInstance() {
        return MarbleJobPool.SingletonHolder.CONFIG_HELPER;
    }


    public class MarbleJob {
        private boolean isSync;
        private String marbleServerIp;
        private Integer marbleServerPort;

        public MarbleJob(boolean isSync, String marbleServerIp,Integer marbleServerPort){
            this.isSync = isSync;
            this.marbleServerIp = marbleServerIp;
            this.marbleServerPort = marbleServerPort;
        }

        public boolean isSync() {
            return isSync;
        }

        public String getMarbleServerIp() {
            return marbleServerIp;
        }

        public Integer getMarbleServerPort() {
            return marbleServerPort;
        }
    }
}
