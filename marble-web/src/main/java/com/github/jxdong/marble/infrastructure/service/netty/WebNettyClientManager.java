package com.github.jxdong.marble.infrastructure.service.netty;

import com.alibaba.fastjson.JSONObject;
import com.github.jxdong.marble.common.util.*;
import com.github.jxdong.marble.domain.model.Result;
import com.github.jxdong.marble.domain.model.enums.JobExecStatusEnum;
import com.github.jxdong.marble.domain.model.enums.JobReqStatusEnum;
import com.github.jxdong.marble.global.util.SpringContextUtil;
import com.github.jxdong.marble.infrastructure.service.LogManager;
import com.github.jxdong.marble.infrastructure.service.RPCClientManager;
import com.github.jxdong.marble.agent.entity.ClassInfo;
import com.github.jxdong.marble.agent.entity.MarbleRequest;
import com.github.jxdong.marble.agent.entity.MarbleResponse;
import com.google.common.base.Throwables;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.*;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/10 9:21
 */
public class WebNettyClientManager implements RPCClientManager {
    private ClogWrapper logger = ClogWrapperFactory.getClogWrapper(WebNettyClientManager.class);

    private static List<EventLoopGroup> workerGroups = new ArrayList<>();
    private static final int MAX_TRY_COUNT = 3;
    private static final int SOCKET_TIMEOUT = 5000;

    //关闭连接
    public void destroy() {
        if (workerGroups != null && workerGroups.size() > 0) {
            for (EventLoopGroup worker : workerGroups) {
                if (!worker.isShutdown()) {
                    worker.shutdownGracefully();
                }
            }
        }
    }

    /**
     * 连通性检查
     *
     * @param host IP
     * @param port 端口号
     * @return Result
     */
    @Override
    public Result checkConnectivity(String host, int port) {
        if (StringUtils.isBlank(host) || port <= 0 || port > 65535) {
            return Result.FAILURE("参数非法");
        }
        Result result = null;

        //尝试建立连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        workerGroups.add(workerGroup);
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(workerGroup)
                    .channel(NioSocketChannel.class)
                    //.option(ChannelOption.SO_RCVBUF, 1024 * 2048)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                        }
                    });
            // Start the client
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            result = channelFuture.isSuccess() ? Result.SUCCESS() : Result.FAILURE("Connect to " + host + ":" + port + " failed");
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            return Result.FAILURE(e.getMessage());
        } finally {
            workerGroup.shutdownGracefully();
        }
        return result;
    }

    @Override
    public Result serviceInvoke(final String requestNo, String host, int port,final Map<String, Object> dataParam) {
        if (StringUtils.isBlank(host) || port <= 0 || port > 65535 || dataParam == null || dataParam.size() == 0) {
            return Result.FAILURE("参数非法");
        }
        logger.REQNO(requestNo).info("Netty begin to RPC, HostInfo:{}-{}, Map Data:{}", host, port, dataParam);

        LogManager logManager = (LogManager) SpringContextUtil.getBean("logManager");

        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //参数复制
            final Map<String, Object> data = new HashMap<>();
            for(Map.Entry<String, Object> entry : dataParam.entrySet()){
                data.put(entry.getKey(), entry.getValue());
            }
            //设置本机IP发送到Netty服务端，用户回传执行结果
            data.put("IP_INFO", PropertyUtils.getLocalIP() + ":" + PropertyUtils.getMarbleServerPort());
            Bootstrap bootstrap = new Bootstrap()
                    .group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            //添加POJO对象解码器 禁止缓存类加载器
                            ch.pipeline().addLast(new ObjectDecoder(1024, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                            //设置发送消息编码器
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(new NettyClientHandler(requestNo, data));
                        }
                    });

            // Start the client.
            ChannelFuture f = bootstrap.connect(host, port).sync();
            //添加请求分类
            Result result = logManager.updateExecuteResult(
                    requestNo,
                    JobReqStatusEnum.SUCCESS.getCode(),
                    JobReqStatusEnum.SUCCESS.getDesc(), JobExecStatusEnum.REQUESTING.getCode(), JobExecStatusEnum.REQUESTING.getDesc(), null);

            logger.REQNO(requestNo).info("Netty RPC end, update result to DB, result: {}", requestNo, result);
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            //TODO 告警
            logger.REQNO(requestNo).error("Netty RPC exception, HostInfo:{}-{}, detail:{} ", host, port, Throwables.getStackTraceAsString(e));
            Result result = logManager.updateExecuteResult(
                    requestNo,
                    JobReqStatusEnum.FAILURE.getCode(),
                    JobReqStatusEnum.FAILURE.getDesc() + ":" + e.getMessage(), null, null, new Date());
            logger.REQNO(requestNo).info("Netty RPC exception,HostInfo:{}-{}, write the exception info into DB, result: {}", host, port, result);
        } finally {
            workerGroup.shutdownGracefully();
        }
        return Result.SUCCESS();
    }

    /**
     * 调用服务端的 [类:方法]
     *
     * @param host         服务端host
     * @param port         服务端port
     * @param classInfoSet 类对象set
     * @return Result
     */
    @Override
    public Result serviceInvoke(final String requestNo, String host, int port, final Set<ClassInfo> classInfoSet, final boolean isSync, final Long maxWaitTime) {
        if (StringUtils.isBlank(host) || port <= 0 || port > 65535 || classInfoSet == null || classInfoSet.size() == 0) {
            return Result.FAILURE("参数非法");
        }
        logger.REQNO(requestNo).info("Netty begin to RPC, HostInfo:{}-{}, IsSync:{}", host, port, isSync);

        LogManager logManager = (LogManager) SpringContextUtil.getBean("logManager");

        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            final Map<String, Object> data = new HashMap<>();
            data.put("CLASS_INFO", classInfoSet);
            data.put("JOB_IS_SYNC", isSync);
            data.put("JOB_MAX_WAIT_TIME", maxWaitTime);
            //设置本机IP发送到Netty服务端，用户回传执行结果
            data.put("IP_INFO", PropertyUtils.getLocalIP() + ":" + PropertyUtils.getMarbleServerPort());

            logger.REQNO(requestNo).info("Netty RPC, HostInfo:{}-{}, data:{}", host, port, data);

            Bootstrap bootstrap = new Bootstrap()
                    .group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            //添加POJO对象解码器 禁止缓存类加载器
                            ch.pipeline().addLast(new ObjectDecoder(1024, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                            //设置发送消息编码器
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(new NettyClientHandler(requestNo, data));
                        }
                    });

            // Start the client.
            ChannelFuture f = bootstrap.connect(host, port).sync();
            //
            Result result = logManager.updateExecuteResult(
                    requestNo,
                    JobReqStatusEnum.SUCCESS.getCode(),
                    JobReqStatusEnum.SUCCESS.getDesc(), JobExecStatusEnum.REQUESTING.getCode(), JobExecStatusEnum.REQUESTING.getDesc(), null);

            logger.REQNO(requestNo).info("Netty RPC end, update result to DB, result: {}", requestNo, result);
            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            //TODO 告警
            logger.REQNO(requestNo).error("Netty RPC exception, HostInfo:{}-{}, detail:{} ", host, port, Throwables.getStackTraceAsString(e));
            Result result = logManager.updateExecuteResult(
                    requestNo,
                    JobReqStatusEnum.FAILURE.getCode(),
                    JobReqStatusEnum.FAILURE.getDesc() + ":" + e.getMessage(), null, null, new Date());
            logger.REQNO(requestNo).info("Netty RPC exception,HostInfo:{}-{}, write the exception info into DB, result: {}", host, port, result);
            return Result.FAILURE("RPC失败:"+e.getMessage());
        } finally {
            workerGroup.shutdownGracefully();
        }
        return Result.SUCCESS();
    }

    //单例
    private WebNettyClientManager() {

    }

    public static WebNettyClientManager getInstance() {
        return SigletonHolder.instance;
    }

    private static class SigletonHolder {
        private static final WebNettyClientManager instance = new WebNettyClientManager();
    }
}

class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private ClogWrapper logger = ClogWrapperFactory.getClogWrapper(NettyClientHandler.class);

    private Set<ClassInfo> classInfoset;
    private String requestNo;
    private boolean isSync;
    private Long maxWaitTime;
    private Map<String, Object> data;

    public NettyClientHandler(String requestNo, Map<String, Object> data) { // Set<ClassInfo> classInfoset, boolean isSync, final Long maxWaitTime
        this.requestNo = requestNo;
        this.data = data;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        logger.REQNO(requestNo).info("Netty begin to send package to server");

        final ChannelFuture future = ctx.writeAndFlush(new MarbleRequest(requestNo, data, true));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                logger.REQNO(requestNo).info("Netty send package to server end");
                //ctx.close();
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String reqNo = null;
        try {
            MarbleResponse response = (MarbleResponse) msg;
            if (response != null) {
                reqNo = response.getRequestNo();

                logger.REQNO(reqNo).info("Netty received the response from server side. Response Info:{}, updated to the DB", response);
                Result execResult = Result.FAILURE("执行状态未知");
                try {
                    execResult = JSONObject.parseObject(response.getResponseInfo(), Result.class);
                    //解析详细信息
                    if (execResult.getOtherInfo() != null && execResult.getOtherInfo().size() > 0) {
                        String msgDetail = null;
                        for (Map.Entry<String, Object> entry : execResult.getOtherInfo().entrySet()) {
                            Result tempRes = JsonUtil.parseObject(entry.getValue().toString(), Result.class);
                            if(tempRes != null){
                                msgDetail = tempRes.getResultMsg();
                            }
                            break;
                        }
                        if(StringUtils.isNotBlank(msgDetail)){
                            execResult.setResultMsg(msgDetail);
                        }
                    }
                } catch (Exception e) {
                    logger.REQNO(reqNo).error("Netty received the response. Convert the response to Result exception, detail: {}", Throwables.getStackTraceAsString(e));
                }

                LogManager logManager = (LogManager) SpringContextUtil.getBean("logManager");
                logManager.updateExecuteResult(response.getRequestNo(),
                        JobReqStatusEnum.SUCCESS.getCode(),
                        JobReqStatusEnum.SUCCESS.getDesc(),
                        execResult.getResultCode(),
                        execResult.getResultMsg(),
                        new Date());
            }
        } catch (Exception e) {
            logger.REQNO(reqNo).info("Netty received the response. Get response from server side exception: {}", e);
        }
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}