package com.leixiaoshuai.easyrpc.server.network;

/**
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/25
 */
public abstract class RpcServer {
    protected int port;

    protected String protocol;

    protected RequestHandler requestHandler;

    public RpcServer(int port, String protocol, RequestHandler requestHandler) {
        this.port = port;
        this.protocol = protocol;
        this.requestHandler = requestHandler;
    }

    /**
     * 启动服务
     */
    public abstract void start();

    /**
     * 停止服务
     */
    public abstract void stop();

}
