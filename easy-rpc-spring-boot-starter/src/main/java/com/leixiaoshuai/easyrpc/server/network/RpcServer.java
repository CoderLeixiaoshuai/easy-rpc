package com.leixiaoshuai.easyrpc.server.network;

/**
 * 定义 RPC 服务端接口
 *
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2022/10/8
 */
public interface RpcServer {
    /**
     * 启动服务
     */
    void start();

    /**
     * 停止服务
     */
    void stop();
}
