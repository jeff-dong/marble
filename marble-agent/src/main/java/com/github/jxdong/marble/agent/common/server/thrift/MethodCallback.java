package com.github.jxdong.marble.agent.common.server.thrift;

import org.apache.thrift.async.AsyncMethodCallback;

public class MethodCallback implements AsyncMethodCallback {
    Object response = null; 

    public Object getResult() { 
        // 返回结果值
        return this.response; 
    } 

    // 处理服务返回的结果值
    @Override 
    public void onComplete(Object response) { 
        this.response = response; 
    }

    @Override
    public void onError(Exception e) {

    }

 }