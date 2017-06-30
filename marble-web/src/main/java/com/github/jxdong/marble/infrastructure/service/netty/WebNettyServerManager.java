package com.github.jxdong.marble.infrastructure.service.netty;

import com.github.jxdong.marble.common.util.ClogWrapper;
import com.github.jxdong.marble.common.util.ClogWrapperFactory;
import com.github.jxdong.marble.common.util.JsonUtil;
import com.github.jxdong.marble.common.util.StringUtils;
import com.github.jxdong.marble.domain.model.enums.JobReqStatusEnum;
import com.github.jxdong.marble.global.util.SpringContextUtil;
import com.github.jxdong.marble.infrastructure.service.LogManager;
import com.github.jxdong.marble.agent.entity.MarbleRequest;
import com.github.jxdong.marble.agent.entity.MarbleResponse;
import com.github.jxdong.marble.agent.entity.Result;
import com.github.jxdong.marble.agent.entity.ResultCodeEnum;
import com.google.common.base.Throwables;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.Date;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2016/1/14 16:02
 */
public class WebNettyServerManager {
    private ClogWrapper logger = ClogWrapperFactory.getClogWrapper(WebNettyServerManager.class);
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    public void run(int port){

        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ObjectDecoder(1024*1024,
                                    ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                            //添加对象编码器 在服务器对外发送消息的时候自动将实现序列化的POJO对象编码
                            ch.pipeline().addLast(new ObjectEncoder());

                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)
            logger.info("Started the service on port - {}", port);

            // Wait until the server socket is closed.
            // shut down your server.
            f.channel().closeFuture().sync();
        }catch (Exception e){
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            logger.error("Start the service on port - {} exception. detail:{}", port, Throwables.getStackTraceAsString(e));
        }
    }

    //关闭netty
    public void stop(){
        try {
            bossGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }catch (Exception e){
            logger.error("Close netty exception. detail: {}", Throwables.getStackTraceAsString(e));
        }
    }

    public WebNettyServerManager(){

    }

    public static WebNettyServerManager getInstance(){
        return SingletonHolder.nettyServer;
    }

    public static class SingletonHolder{
        private static final WebNettyServerManager nettyServer = new WebNettyServerManager();
    }

    /**
     * @author <a href="djx_19881022@163.com">jeff</a>
     * @version 2016/1/14 16:06
     */
    public class NettyServerHandler extends ChannelInboundHandlerAdapter  {
        private ClogWrapper logger = ClogWrapperFactory.getClogWrapper(NettyServerHandler.class);

        public NettyServerHandler() {
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
           ctx.flush();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            final MarbleRequest request = (MarbleRequest) msg;
            String reqNo = request.getRequestNo();
            logger.REQNO(reqNo).info("[Received the request] request info: {}", msg);
            MarbleResponse response = new MarbleResponse(request.getRequestNo(), ResultCodeEnum.OTHER_ERROR, "未知错误");
            try {
                if(StringUtils.isBlank(request.getRequestNo())){
                    logger.addTag("REQ_NO", reqNo).error("[Received the request] illegal request info, no request no found. MSG: {}", msg);
                    return;
                }
                //解析执行结果
                Result execResult = request.getData()!=null?(Result)request.getData().get("EXEC_RESULT"):null;
                if(execResult!=null){
                    //更新请求记录
                    LogManager logManager = (LogManager) SpringContextUtil.getBean("logManager");

                    com.github.jxdong.marble.domain.model.Result result = logManager.updateExecuteResult(
                            request.getRequestNo(),
                            JobReqStatusEnum.SUCCESS.getCode(),
                            JobReqStatusEnum.SUCCESS.getDesc(),
                            execResult.getResultCode(),
                            execResult.getResultMsg(),
                            new Date());

                    logger.REQNO(reqNo).info("Job [ReqNo={}] request send success, update DB, result: {}", request.getRequestNo(), result);
                }

            } catch (Exception e) {
                logger.REQNO(reqNo).error("Netty server exec job exception, detail: {}", Throwables.getStackTraceAsString(e));
                response = new MarbleResponse(request.getRequestNo(), ResultCodeEnum.OTHER_ERROR, "异常：" + e.getMessage());
            } finally {
                //如果需要回写，返回执行结果
                //如果需要回写，返回执行结果
                if (request.isNeedResponse()) {
                    ctx.writeAndFlush(response);
                    logger.REQNO(reqNo).info("Deal with the Marble request result: {}", response);
                }
                logger.REQNO(reqNo).info("Netty server exec job end. response: {}", JsonUtil.toJsonString(response));
                ctx.close();
            }

        }
    }

}
