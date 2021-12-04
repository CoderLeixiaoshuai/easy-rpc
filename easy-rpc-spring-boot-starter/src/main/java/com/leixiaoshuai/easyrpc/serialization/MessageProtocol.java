package com.leixiaoshuai.easyrpc.serialization;

/**
 * The interface Message protocol.
 *
 * @author 雷小帅 （公众号：爱笑的架构师）
 * @since 2021 /11/26
 */
public interface MessageProtocol {


    /**
     * 编码请求消息
     *
     * @param request the request
     * @return the byte [ ]
     * @throws Exception the exception
     */
    byte[] marshallingReqMessage(RpcRequest request) throws Exception;

    /**
     * 解码请求消息
     *
     * @param data
     * @return
     * @throws Exception
     */
    RpcRequest unmarshallingReqMessage(byte[] data) throws Exception;

    /**
     * 编码响应消息
     *
     * @param response the response
     * @return the byte [ ]
     * @throws Exception the exception
     */
    byte[] marshallingRespMessage(RpcResponse response) throws Exception;

    /**
     * 解码响应消息
     *
     * @param data the data
     * @return the rpc response
     * @throws Exception the exception
     */
    RpcResponse unmarshallingRespMessage(byte[] data) throws Exception;
}
