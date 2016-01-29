package com.github.jxdong.marble.infrastructure.service;

import com.github.jxdong.marble.domain.model.Result;
import com.github.jxdong.marble.entity.ClassInfo;

import java.util.Set;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2016/1/15 12:46
 */
public interface RPCClientManager {

    Result checkConnectivity(String host, int port);

    Result serviceInvoke(String host, int port, final Set<ClassInfo> classInfoSet);
}
