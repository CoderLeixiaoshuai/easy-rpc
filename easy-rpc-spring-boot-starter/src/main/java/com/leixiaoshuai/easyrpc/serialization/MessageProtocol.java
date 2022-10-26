package com.leixiaoshuai.easyrpc.serialization;

/**
 * The interface Message protocol.
 *
 * @author 雷小帅 （公众号：爱笑的架构师）
 * @since 2021 /11/26
 */
public interface MessageProtocol {
    // 服务端

    /**
     * 解码请求消息
     *
     * @param data 客户端请求数据，字节数组格式
     * @return rpc 请求对象
     * @throws Exception 异常
     */
    RpcRequest unmarshallingReqMessage(byte[] data) throws Exception;

    /**
     * 编码响应消息
     *
     * @param response 服务端待响应对象
     * @return byte[] 对象编码后的字节数组
     * @throws Exception 异常
     */
    byte[] marshallingRespMessage(RpcResponse response) throws Exception;

    // 客户端

    /**
     * 编码请求消息
     *
     * @param request 客户端请求对象
     * @return 对象编码后的字节数组
     * @throws Exception 异常
     */
    byte[] marshallingReqMessage(RpcRequest request) throws Exception;

    /**
     * 解码响应消息
     *
     * @param data 服务端响应待解码字节数组
     * @return 服务端响应的对象
     * @throws Exception 异常
     */
    RpcResponse unmarshallingRespMessage(byte[] data) throws Exception;
}
