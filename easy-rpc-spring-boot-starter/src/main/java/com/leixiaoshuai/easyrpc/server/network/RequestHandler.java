package com.leixiaoshuai.easyrpc.server.network;

import com.leixiaoshuai.easyrpc.common.ServiceInfo;
import com.leixiaoshuai.easyrpc.serialization.MessageProtocol;
import com.leixiaoshuai.easyrpc.serialization.RpcRequest;
import com.leixiaoshuai.easyrpc.serialization.RpcResponse;
import com.leixiaoshuai.easyrpc.server.registry.ServiceRegistry;
import com.leixiaoshuai.easyrpc.server.registry.ZookeeperServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/25
 */
public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperServiceRegistry.class);

    private final MessageProtocol protocol;

    private final ServiceRegistry serviceRegistry;

    public RequestHandler(MessageProtocol protocol, ServiceRegistry serviceRegistry) {
        this.protocol = protocol;
        this.serviceRegistry = serviceRegistry;
    }

    public byte[] handleRequest(byte[] data) throws Exception {
        // 请求消息解码
        final RpcRequest rpcRequest = protocol.unmarshallingReqMessage(data);
        final String serviceName = rpcRequest.getServiceName();
        final ServiceInfo serviceInfo = serviceRegistry.getServiceInstance(serviceName);
        RpcResponse response = new RpcResponse();
        if (serviceInfo == null) {
            response.setStatus("Not Found");
            return protocol.marshallingRespMessage(response);
        }

        // 通过反射技术调用目标方法
        try {
            final Method method = serviceInfo.getClazz().getMethod(rpcRequest.getMethod(), rpcRequest.getParameterTypes());
            final Object retValue = method.invoke(serviceInfo.getObj(), rpcRequest.getParameters());
            response.setStatus("Success");
            response.setRetValue(retValue);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            response.setStatus("Fail");
            response.setException(e);
        }
        return protocol.marshallingRespMessage(response);
    }
}
