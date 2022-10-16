package com.leixiaoshuai.easyrpc.server.network;

import com.leixiaoshuai.easyrpc.common.ServiceInterfaceInfo;
import com.leixiaoshuai.easyrpc.serialization.MessageProtocol;
import com.leixiaoshuai.easyrpc.serialization.RpcRequest;
import com.leixiaoshuai.easyrpc.serialization.RpcResponse;
import com.leixiaoshuai.easyrpc.server.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/25
 */
@Slf4j
public class RequestHandler {
    private final MessageProtocol protocol;

    private final ServiceRegistry serviceRegistry;

    public RequestHandler(MessageProtocol protocol, ServiceRegistry serviceRegistry) {
        this.protocol = protocol;
        this.serviceRegistry = serviceRegistry;
    }

    public byte[] handleRequest(byte[] data) throws Exception {
        // 请求消息解码
        RpcRequest rpcRequest = protocol.unmarshallingReqMessage(data);
        String serviceName = rpcRequest.getServiceName();
        ServiceInterfaceInfo serviceInterfaceInfo = serviceRegistry.getRegisteredObj(serviceName);

        RpcResponse response = new RpcResponse();
        if (serviceInterfaceInfo == null) {
            response.setStatus("Not Found");
            return protocol.marshallingRespMessage(response);
        }

        try {
            // 通过反射技术调用目标方法
            final Method method = serviceInterfaceInfo.getClazz().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            final Object retValue = method.invoke(serviceInterfaceInfo.getObj(), rpcRequest.getParameters());
            response.setStatus("Success");
            response.setRetValue(retValue);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            response.setStatus("Fail");
            response.setException(e);
        }
        return protocol.marshallingRespMessage(response);
    }
}
