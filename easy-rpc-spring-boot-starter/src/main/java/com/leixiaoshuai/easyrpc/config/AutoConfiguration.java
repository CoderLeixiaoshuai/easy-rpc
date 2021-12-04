package com.leixiaoshuai.easyrpc.config;

import com.leixiaoshuai.easyrpc.client.ClientProxyFactory;
import com.leixiaoshuai.easyrpc.client.discovery.ServiceDiscovery;
import com.leixiaoshuai.easyrpc.client.discovery.ZookeeperServiceDiscovery;
import com.leixiaoshuai.easyrpc.client.network.NettyRpcClient;
import com.leixiaoshuai.easyrpc.listener.DefaultRpcListener;
import com.leixiaoshuai.easyrpc.property.RpcProperties;
import com.leixiaoshuai.easyrpc.serialization.DefaultMessageProtocol;
import com.leixiaoshuai.easyrpc.server.network.NettyRpcServer;
import com.leixiaoshuai.easyrpc.server.network.RequestHandler;
import com.leixiaoshuai.easyrpc.server.network.RpcServer;
import com.leixiaoshuai.easyrpc.server.registry.ServiceRegistry;
import com.leixiaoshuai.easyrpc.server.registry.ZookeeperServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/28
 */
@Configuration
public class AutoConfiguration {

    // 监听器
    @Bean
    public DefaultRpcListener defaultRpcListener(@Autowired ServiceRegistry serviceRegistry,
                                                 @Autowired RpcServer rpcServer,
                                                 @Autowired ClientProxyFactory clientProxyFactory,
                                                 @Autowired RpcProperties rpcProperties) {
        return new DefaultRpcListener(serviceRegistry, rpcServer, clientProxyFactory, rpcProperties);
    }

    // 配置属性
    @Bean
    public RpcProperties rpcProperty() {
        return new RpcProperties();
    }

    // 客户端
    @Bean
    public ServiceDiscovery serviceDiscovery(@Autowired RpcProperties rpcProperties) {
        return new ZookeeperServiceDiscovery(rpcProperties.getZkAddress());
    }

    @Bean
    public ClientProxyFactory clientProxyFactory(@Autowired ServiceDiscovery serviceDiscovery) {
        return new ClientProxyFactory(serviceDiscovery, new DefaultMessageProtocol(), new NettyRpcClient());
    }

    // 服务端
    @Bean
    public ServiceRegistry serviceRegister(@Autowired RpcProperties rpcProperties) {
        return new ZookeeperServiceRegistry(rpcProperties.getZkAddress(), rpcProperties.getExposePort(),
                rpcProperties.getProtocol());
    }

    @Bean
    public RequestHandler requestHandler(@Autowired ServiceRegistry serviceRegistry) {
        return new RequestHandler(new DefaultMessageProtocol(), serviceRegistry);
    }

    @Bean
    public RpcServer rpcServer(@Autowired RpcProperties rpcProperties,
                               @Autowired RequestHandler requestHandler) {
        final Integer port = rpcProperties.getExposePort();
        final String protocol = rpcProperties.getProtocol();
        return new NettyRpcServer(port, protocol, requestHandler);
    }

}
