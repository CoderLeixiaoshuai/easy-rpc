package com.leixiaoshuai.easyrpc.client.network;

import com.leixiaoshuai.easyrpc.common.ServiceInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @Description 使用 Netty 实现网络客户端消息发送
 * @Author 雷小帅（公众号：爱笑的架构师）
 * @Date 2021/11/25
 */
public class NettyRpcClient implements RpcClient {
    @Override
    public byte[] sendMessage(byte[] data, ServiceInfo serviceInfo) throws InterruptedException {
        final String ip = serviceInfo.getIp();
        final Integer port = serviceInfo.getPort();

        ClientChannelHandler clientChannelHandler = new ClientChannelHandler(data);
        // 初始化 netty 客户端
        final NioEventLoopGroup eventGroup = new NioEventLoopGroup();
        final Bootstrap bootstrap = new Bootstrap()
                .group(eventGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        final ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(clientChannelHandler);
                    }
                });

        bootstrap.connect(ip, port).sync();
        return clientChannelHandler.response();
    }
}
