package com.github.jxdong.marble.domain.model;

import com.github.jxdong.common.util.ArrayUtils;
import com.github.jxdong.common.util.JacksonUtil;
import com.github.jxdong.common.util.StringUtils;
import com.github.jxdong.marble.entity.ClassInfo;
import com.github.jxdong.marble.infrastructure.service.RPCClientFactory;
import com.github.jxdong.marble.server.thrift.ThriftConnectInfo;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/11/11 14:50
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MarbleJobProxy implements Job {
    private static final Logger logger = LoggerFactory.getLogger(MarbleJobProxy.class);


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            ThriftConnectInfo connectInfo = JacksonUtil.json2pojo(jobExecutionContext.getMergedJobDataMap().get("THRIFT_CONNECT_INFO").toString(), ThriftConnectInfo.class);
            if (connectInfo != null && ArrayUtils.listIsNotBlank(connectInfo.getServerInfo()) && StringUtils.isNotBlank(connectInfo.getServiceName())) {
                ThriftConnectInfo.Server serverInfo = connectInfo.getOneRandomServer();
                if(serverInfo ==null||serverInfo.getPort() <0 || serverInfo.getPort() >65535 || StringUtils.isBlank(serverInfo.getIp())){
                    logger.error("Job execute failed. Thrift Connect Info is illegal. {} ", connectInfo);
                    return;
                }
                String jobParam = "";
                String marbleVersion="";
                //map中添加执行的服务器+端口号信息
                Object jobInfoObject = jobExecutionContext.getMergedJobDataMap().get("JOB_INFO");
                if(jobInfoObject != null){
                    JobBasicInfo jobBasicInfo = JacksonUtil.json2pojo(jobInfoObject.toString(), JobBasicInfo.class);
                    if(jobBasicInfo != null){
                        marbleVersion = jobBasicInfo.getMarbleVersion();
                        jobBasicInfo.setServerIp(serverInfo.getIp());
                        jobBasicInfo.setServerPort(serverInfo.getPort());
                        jobParam = jobBasicInfo.getParam();

                        //更新data map
                        jobExecutionContext.getMergedJobDataMap().put("JOB_INFO", JacksonUtil.obj2json(jobBasicInfo));
                    }
                }
                //与服务端创建连接并执行
                logger.info("{}, MarbleVersion:{}执行. {}", connectInfo.getServiceName(), marbleVersion, serverInfo);
                Set<ClassInfo> classInfoSet = new HashSet<>();
                classInfoSet.add(new ClassInfo(connectInfo.getServiceName(), "execute", jobParam));
                //根据得到的Marble-Version动态选择调用方式
                Result result = RPCClientFactory.getClientManager(marbleVersion).serviceInvoke(serverInfo.getIp(), serverInfo.getPort(), classInfoSet);
                if(!result.isSuccess()){
                    throw new JobExecutionException(result.getResultMsg());
                }
            }else{
                logger.error("Job execute failed. Thrift Connect Info is illegal. {} ", connectInfo);
            }
        }catch (Exception e) {
            logger.error("Job execute exception. Exception detail: ", e);
            throw new JobExecutionException(e);
        }
    }


}
