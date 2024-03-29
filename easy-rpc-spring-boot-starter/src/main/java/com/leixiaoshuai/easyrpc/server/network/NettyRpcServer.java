package com.leixiaoshuai.easyrpc.server.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/26
 */
public class NettyRpcServer implements RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyRpcServer.class);

    private int port;

    private RequestHandler requestHandler;

    private Channel channel;

    public NettyRpcServer(int port, RequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
    }

    @Override
    public void start() {
        // 创建两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建服务端的启动对象
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    // 设置两个线程组
                    .group(bossGroup, workerGroup)
                    // 设置服务端通道实现类型
                    .channel(NioServerSocketChannel.class)
                    // 服务端用于接收进来的连接，也就是boosGroup线程, 线程队列大小
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // child 通道，worker 线程处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 给 pipeline 管道设置自定义的处理器
                        @Override
                        public void initChannel(SocketChannel channel) {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new ChannelRequestHandler());
                        }
                    });

            // 绑定端口号，同步启动服务
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            logger.info("[easy-rpc]Rpc Server started on port: {}", port);
            channel = channelFuture.channel();
            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("server error.", e);
        } finally {
            // 释放线程组资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {
        channel.close();
    }

    private class ChannelRequestHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            logger.info("channel active: {}", ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            logger.info("Server receive a massage: {}", msg);
            final ByteBuf msgBuf = (ByteBuf) msg;
            final byte[] reqBytes = new byte[msgBuf.readableBytes()];
            msgBuf.readBytes(reqBytes);
            // 调用请求处理器开始处理客户端请求
            final byte[] respBytes = requestHandler.handleRequest(reqBytes);
            logger.info("Send response massage: {}", respBytes);
            final ByteBuf resBuf = Unpooled.buffer(respBytes.length);
            resBuf.writeBytes(respBytes);
            ctx.writeAndFlush(resBuf);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            logger.error("Catch exception", cause);
            ctx.close();
        }
    }
}
