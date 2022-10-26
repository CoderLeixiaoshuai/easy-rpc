package com.leixiaoshuai.easyrpc.client.network;

import com.leixiaoshuai.easyrpc.common.ServiceInterfaceInfo;

/**
 * @Description 网络客户端：client侧
 * @Author 雷小帅（公众号：爱笑的架构师）
 * @Date 2021/11/24
 */
public interface RpcClient {
    /**
     * 发送消息
     *
     * @param data 待发送的消息
     * @param serviceInterfaceInfo 消息接收者
     * @return 已发送消息
     */
    byte[] sendMessage(byte[] data, ServiceInterfaceInfo serviceInterfaceInfo) throws InterruptedException;
}
