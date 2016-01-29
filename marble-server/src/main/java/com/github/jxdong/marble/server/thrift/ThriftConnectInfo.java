package com.github.jxdong.marble.server.thrift;

import com.github.jxdong.common.util.ArrayUtils;
import com.github.jxdong.common.util.CommonUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/11/11 16:13
 */
public class ThriftConnectInfo implements Serializable{

    private String serviceName;
    private List<Server> serverInfo;
    private Server execServer;

    public ThriftConnectInfo(){

    }

    public ThriftConnectInfo(String appCode, String schedName, String jobName, List<Server> serverInfo) {
        this.serviceName = schedName + "-"+appCode+"-" + jobName;
        this.serverInfo = serverInfo;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @JsonIgnore
    public Server getOneRandomServer(){
        execServer = null;
        if(ArrayUtils.listIsNotBlank(serverInfo)){
            Integer randomIndex[] = CommonUtil.randomCommon(0,serverInfo.size(), 1);
            if(randomIndex != null){
                execServer = serverInfo.get(randomIndex[0]);
            }
        }
       return execServer;
    }

    public Server getExecServer() {
        return execServer;
    }

    public void setExecServer(Server execServer) {
        this.execServer = execServer;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<Server> getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(List<Server> serverInfo) {
        this.serverInfo = serverInfo;
    }

    public static class Server implements Serializable{
        private String ip;
        private int port;

        public Server(){
        }

        public Server(String ip, int port){
            this.ip = ip;
            this.port = port;
        }
        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }

}


