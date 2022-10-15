package com.leixiaoshuai.easyrpc.client;

import com.leixiaoshuai.easyrpc.client.discovery.ServiceDiscovery;
import com.leixiaoshuai.easyrpc.client.network.RpcClient;
import com.leixiaoshuai.easyrpc.common.ServiceInterfaceInfo;
import com.leixiaoshuai.easyrpc.exception.RpcException;
import com.leixiaoshuai.easyrpc.serialization.MessageProtocol;
import com.leixiaoshuai.easyrpc.serialization.RpcRequest;
import com.leixiaoshuai.easyrpc.serialization.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Random;

/**
 * 被 @ServiceReference 注解标记的接口变量，会自动注入一个代理对象；
 * 在调用方法时会自动执行代理对象的 invoke 方法
 *
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/30
 */
public class ClientProxyFactory {
    private static final Logger logger = LoggerFactory.getLogger(ClientProxyFactory.class);

    private ServiceDiscovery serviceDiscovery;

    private MessageProtocol messageProtocol;

    private RpcClient rpcClient;


    public ClientProxyFactory(ServiceDiscovery serviceDiscovery, MessageProtocol messageProtocol, RpcClient rpcClient) {
        this.serviceDiscovery = serviceDiscovery;
        this.messageProtocol = messageProtocol;
        this.rpcClient = rpcClient;
    }

    /**
     * 获取代理对象，绑定 invoke 行为
     *
     * @param clazz 接口 class 对象
     * @param <T>   类型
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxyInstance(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            final Random random = new Random();

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 第一步：通过服务发现机制选择一个服务提供者暴露的服务
                String serviceName = clazz.getName();
                ServiceInterfaceInfo serviceInterfaceInfo = serviceDiscovery.selectOneInstance(serviceName);
                logger.info("Rpc server instance list: {}", serviceInterfaceInfo);
                if (serviceInterfaceInfo == null) {
                    throw new RpcException("No rpc servers found.");
                }

                // 第二步：构造 rpc 请求对象
                final RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setServiceName(serviceName);
                rpcRequest.setMethodName(method.getName());
                rpcRequest.setParameterTypes(method.getParameterTypes());
                rpcRequest.setParameters(args);

                // 第三步：编码请求消息， TODO: 这里可以配置多种编码方式
                byte[] data = messageProtocol.marshallingReqMessage(rpcRequest);

                // 第四步：调用 rpc client 开始发送消息
                byte[] byteResponse = rpcClient.sendMessage(data, serviceInterfaceInfo);

                // 第五步：解码响应消息
                final RpcResponse rpcResponse = messageProtocol.unmarshallingRespMessage(byteResponse);

                // 第六步：解析返回结果进行处理
                if (rpcResponse.getException() != null) {
                    throw rpcResponse.getException();
                }
                return rpcResponse.getRetValue();
            }
        });
    }
}
