package com.github.jxdong.marble.infrastructure.service;

import com.github.jxdong.marble.common.util.StringUtils;
import com.github.jxdong.marble.domain.model.Result;
import com.github.jxdong.marble.domain.model.enums.JobReqStatusEnum;
import com.github.jxdong.marble.global.util.SpringContextUtil;
import com.github.jxdong.marble.agent.entity.ClassInfo;
import com.github.jxdong.marble.agent.common.server.thrift.ThriftAuto;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/10 9:21
 */
public class ThriftManager implements RPCClientManager{
    private static final Logger logger = LoggerFactory.getLogger(ThriftManager.class);
    private static final int MAX_TRY_COUNT = 3;
    private static final int SOCKET_TIMEOUT = 5000;

    //测试服务器端口号的连通性
    public Result checkConnectivity(String host, int port){
        logger.info("begin connect check...");
        if(StringUtils.isBlank(host) || port<=0 || port >65535){
            return Result.FAILURE("参数非法");
        }
        TTransport transport = null;
        try {
            transport = new TFramedTransport(new TSocket(host, port, SOCKET_TIMEOUT));
            if(!transport.isOpen()){
                transport.open();
            }
            return Result.SUCCESS();
        }catch (Exception e){
            logger.warn("test server connectivity exception.", e);
            return Result.FAILURE("连接到服务器("+host+":"+port+")失败");
        }finally {
            if(transport != null){
                transport.close();
            }
        }
    }

    @Override
    public Result serviceInvoke(String requestNo, String host, int port, Map<String, Object> data) {
        return Result.FAILURE("此版本暂时不支持（请更新Marble到2.0以上）");
    }

    @Override
    public Result serviceInvoke(String requestNo, String host, int port, Set<ClassInfo> classInfoSet, boolean isSync, Long maxWaitTime) {
        if(StringUtils.isBlank(host) || port<=0 || port>65535 || classInfoSet==null || classInfoSet.size()==0){
            return Result.FAILURE("参数非法");
        }
        ClassInfo classInfo= classInfoSet.iterator().next();
        Result result = connect2Server(host, port, classInfo.getClassName(), classInfo.getMathodParam(), 1);

        LogManager logManager = (LogManager) SpringContextUtil.getBean("logManager");

        int reqResCode = result.isSuccess()? JobReqStatusEnum.SUCCESS.getCode():JobReqStatusEnum.FAILURE.getCode();
        String reqResMsg = result.isSuccess()? JobReqStatusEnum.SUCCESS.getDesc():JobReqStatusEnum.FAILURE.getDesc()+": "+ result.getResultMsg();

        Result updateResult = logManager.updateExecuteResult(requestNo, reqResCode, reqResMsg, null, null, new Date());
        logger.info("Thrift Update exec log result: {}", updateResult);
        return result;
    }

    /**
     * 连接到服务器并执行
     * @param host host
     * @param port port
     * @param service schedulerId-jobId
     */
    private Result connect2Server(String host, int port, String service, String param, int tryCount){
        if(StringUtils.isBlank(host) || port<1 || port>65535 || StringUtils.isBlank(service)){
            throw new IllegalArgumentException("illegal arguments for 'connect2Server'");
        }
        logger.info("[{}] connecting to server({}) on port({})", tryCount, host, port);

        TTransport transport = null;
        String curServiceName = "";
        try{
            transport = new TFramedTransport(new TSocket(host, port, SOCKET_TIMEOUT));
            if(!transport.isOpen()){
                transport.open();
            }
            logger.info("connected to server({}) on port({})", host, port);

            TProtocol protocol = new TCompactProtocol(transport);
            logger.info("begin to execute service({})", service);
            curServiceName = service;
            ThriftAuto.Client client = new ThriftAuto.Client(new TMultiplexedProtocol(protocol, service));
            client.send_execute(param);
            Thread.sleep(100);
            logger.info("execute service({}) end", service);
            return Result.SUCCESS();
        } catch (TTransportException e) {
            logger.error("connect to server({}-{}) exception. detail: ", host, port, e);
            //连接失败继续尝试
            if(tryCount < MAX_TRY_COUNT){
                connect2Server(host, port, service,param, ++tryCount);
            }
            return Result.FAILURE("连接到["+host+"]:["+port+"] 失败 (尝试了 "+(tryCount+1)+" 次).");
        }catch (TException e){
            logger.error("execute services({}) exception. detail: ",service, e);
            Result.FAILURE("执行服务(" + curServiceName + ") 异常.");
        }catch (Exception e){
            logger.error("connect or execute services({}) exception. detail: ", service, e);
        }finally {
            if(transport != null){
                transport.close();
            }
        }
        return Result.FAILURE("执行服务("+curServiceName+") 异常.");
    }

    //单例
    private ThriftManager(){

    }

    public static ThriftManager getInstance(){
        return SigletonHolder.instance;
    }

    private static class SigletonHolder {
        private static final ThriftManager instance = new ThriftManager();
    }
}
