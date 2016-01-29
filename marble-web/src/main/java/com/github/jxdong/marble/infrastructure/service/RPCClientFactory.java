package com.github.jxdong.marble.infrastructure.service;

import com.github.jxdong.common.util.StringUtils;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2016/1/15 16:23
 */
public class RPCClientFactory {

    public static RPCClientManager getClientManager(String marbleVersion){
        RPCClientManager manager = ThriftManager.getInstance();
        if(StringUtils.isNotBlank(marbleVersion)){
            switch (marbleVersion){
                case "2.0.0":
                    manager = NettyClientManager.getInstance();
                    break;
                default:
                    manager = ThriftManager.getInstance();
                    break;
            }
        }
        return manager;
    }
}
