package com.github.jxdong.marble.infrastructure.service;

import com.github.jxdong.common.util.StringUtils;
import com.github.jxdong.marble.domain.model.Result;
import com.github.jxdong.marble.entity.ClassInfo;
import com.github.jxdong.marble.entity.MarbleRequest;
import com.github.jxdong.marble.entity.MarbleResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/11/10 9:21
 */
public class NettyClientManager implements RPCClientManager {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientManager.class);
    private static final int MAX_TRY_COUNT = 3;
    private static final int SOCKET_TIMEOUT = 5000;

    /**
     * 连通性检查
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
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                        }
                    });
            // Start the client
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            result = channelFuture.isSuccess()?Result.SUCCESS():Result.FAILURE("Connect to "+host+":"+port+" failed");
            channelFuture.channel().closeFuture().sync();
        } catch(Exception e){
            return Result.FAILURE(e.getMessage());
        } finally {
            workerGroup.shutdownGracefully();
        }
        return result;
    }


    /**
     * 调用服务端的 [类:方法]
     * @param host 服务端host
     * @param port 服务端port
     * @param classInfoSet 类对象set
     * @return Result
     */
    @Override
    public Result serviceInvoke(String host, int port, final Set<ClassInfo> classInfoSet){
        if (StringUtils.isBlank(host) || port <= 0 || port > 65535 || classInfoSet == null || classInfoSet.size() == 0) {
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
                            ch.pipeline().addLast(new NettyClientHandler(classInfoSet));
                        }
                    });

            // Start the client.
            ChannelFuture f = bootstrap.connect(host, port).sync();
            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        }catch (Exception e){
            logger.error("Service({}:{}) invoke exception. ", host, port, e);
        }finally {
            workerGroup.shutdownGracefully();
        }
        return Result.SUCCESS();
    }

    //单例
    private NettyClientManager(){

    }

    public static NettyClientManager getInstance(){
        return SigletonHolder.instance;
    }

    private static class SigletonHolder {
        private static final NettyClientManager instance = new NettyClientManager();
    }
}

class NettyClientHandler extends ChannelHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    private Set<ClassInfo> classInfoset;

    public NettyClientHandler(Set<ClassInfo> classInfoset){
        this.classInfoset = classInfoset;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        logger.info("begin to send package to server");

        final ChannelFuture future = ctx.writeAndFlush(new MarbleRequest(classInfoset, true));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                logger.info("send package to server end");
                //ctx.close();
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            MarbleResponse response = (MarbleResponse) msg;
            logger.info("response from server side: {}", response);
        }catch (Exception e){
            logger.info("get response from server side exception: ", e);
        }
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}