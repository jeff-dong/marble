package com.github.jxdong.marble.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2016/1/14 16:02
 */
public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
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
            logger.error("Start the service on port - {} exception", port, e);
        }
    }

    //关闭netty
    public void stop(){
        try {
            bossGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }catch (Exception e){
            logger.error("Close netty exception. detail: ", e);
        }
    }

    private NettyServer(){

    }

    public static NettyServer getInstance(){
        return SigletonHolder.nettyServer;
    }

    public static class SigletonHolder{
        private static final NettyServer nettyServer = new NettyServer();
    }

    public static void main(String args[]) throws Exception{
        new Thread(){
            @Override
            public void run() {
                NettyServer.getInstance().run(8080);
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                NettyServer.getInstance().run(9093);
            }
        }.start();
    }
}
