package com.github.jxdong.marble.infrastructure.service;

import com.github.jxdong.marble.domain.model.Result;
import com.github.jxdong.marble.agent.entity.ClassInfo;

import java.util.Map;
import java.util.Set;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2016/1/15 12:46
 */
public interface RPCClientManager {

    Result checkConnectivity(String host, int port);

    Result serviceInvoke(String requestNo, String host, int port, final Set<ClassInfo> classInfoSet, boolean isSync, Long maxWaitTime);

    //通用的服务调用方法
    Result serviceInvoke(String requestNo, String host, int port, final Map<String, Object> data);

}
