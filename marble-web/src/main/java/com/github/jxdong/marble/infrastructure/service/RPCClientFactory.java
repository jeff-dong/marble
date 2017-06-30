package com.github.jxdong.marble.infrastructure.service;

import com.github.jxdong.marble.common.util.ClogWrapper;
import com.github.jxdong.marble.common.util.ClogWrapperFactory;
import com.github.jxdong.marble.common.util.StringUtils;
import com.github.jxdong.marble.infrastructure.service.netty.WebNettyClientManager;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2016/1/15 16:23
 */
public class RPCClientFactory {
    private static ClogWrapper logger = ClogWrapperFactory.getClogWrapper(RPCClientFactory.class);

    public static RPCClientManager getClientManager(String reqNo, String marbleVersion){
        logger.REQNO(reqNo).info("RPCClientFactory, Marble Version: {}", marbleVersion);
        //默认是Thrift
        RPCClientManager manager = ThriftManager.getInstance();

        if(StringUtils.isNotBlank(marbleVersion)){
            String[] versionArray = marbleVersion.split("\\.");
            if(StringUtils.str2Int(versionArray[0],-1) >=2){
                manager = WebNettyClientManager.getInstance();
            }
        }
        return manager;
    }
}
