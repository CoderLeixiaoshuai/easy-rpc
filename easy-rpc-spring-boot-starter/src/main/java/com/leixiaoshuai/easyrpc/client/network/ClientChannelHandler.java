package com.leixiaoshuai.easyrpc.client.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/30
 */
public class ClientChannelHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ClientChannelHandler.class);

    private byte[] data;

    private byte[] response;

    private CountDownLatch countDownLatch;

    public ClientChannelHandler(byte[] data) {
        this.data = data;
        countDownLatch = new CountDownLatch(1);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 通道激活后客户端开始发送数据
        final ByteBuf buffer = Unpooled.buffer(data.length);
        buffer.writeBytes(data);
        logger.info("Client start to send message: {}", buffer);
        ctx.writeAndFlush(buffer);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("Client received massage: {}", msg);
        // 将 byteBuf 转成 byte[]
        ByteBuf buffer = (ByteBuf) msg;
        response = new byte[buffer.readableBytes()];
        buffer.readBytes(response);
        countDownLatch.countDown();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Catch exception: {}", cause.getMessage());
        ctx.close();
    }

    public byte[] response() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {

        }
        return response;
    }
}
