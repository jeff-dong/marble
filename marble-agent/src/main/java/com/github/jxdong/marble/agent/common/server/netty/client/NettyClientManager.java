package com.github.jxdong.marble.agent.common.server.netty.client;

import com.github.jxdong.marble.agent.common.util.ClogWrapper;
import com.github.jxdong.marble.agent.common.util.ClogWrapperFactory;
import com.github.jxdong.marble.agent.common.util.StringUtils;
import com.github.jxdong.marble.agent.entity.MarbleRequest;
import com.github.jxdong.marble.agent.entity.MarbleResponse;
import com.github.jxdong.marble.agent.entity.Result;
import com.google.common.base.Throwables;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import java.util.Map;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/10 9:21
 */
public class NettyClientManager {
    private static ClogWrapper logger = ClogWrapperFactory.getClogWrapper(NettyClientManager.class);

    /**
     * 调用服务端的 [类:方法]
     * @param host 服务端host
     * @param port 服务端port
     * @param data 传送的对象
     * @return Result
     */
    public Result serviceInvoke(final String requestNo, String host, int port, final Map<String, Object> data){
        if (StringUtils.isBlank(host) || port <= 0 || port > 65535 || data == null) {
            return Result.FAILURE("参数非法");
        }

        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
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
            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        }catch (Exception e){
            //TODO 告警
            logger.error("exception, {}", Throwables.getStackTraceAsString(e));
        }finally {
            workerGroup.shutdownGracefully();
        }
        return Result.SUCCESS();
    }

    //单例
    private NettyClientManager(){

    }

    public static NettyClientManager getInstance(){
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final NettyClientManager instance = new NettyClientManager();
    }
}

class NettyClientHandler extends ChannelInboundHandlerAdapter  {
    private static ClogWrapper logger = ClogWrapperFactory.getClogWrapper(NettyClientHandler.class);

    private String requestNo;
    private Map<String, Object> data;

    public NettyClientHandler(String requestNo, Map<String, Object> data){
        this.requestNo = requestNo;
        this.data = data;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        logger.REQNO(requestNo).info("begin to send package to server");

        final ChannelFuture future = ctx.writeAndFlush(new MarbleRequest(requestNo, data,false));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                logger.REQNO(requestNo).info("send package to server end");
                //ctx.close();
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            MarbleResponse response = (MarbleResponse) msg;
            logger.REQNO(response.getRequestNo()).info("response from server side: {}, updated to the DB", response);
        }catch (Exception e){
            logger.REQNO(requestNo).info("get response from server side exception: ", e);
        }
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}