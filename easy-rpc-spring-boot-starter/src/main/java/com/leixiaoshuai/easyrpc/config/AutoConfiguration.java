package com.leixiaoshuai.easyrpc.config;

import com.leixiaoshuai.easyrpc.client.ClientProxyFactory;
import com.leixiaoshuai.easyrpc.client.discovery.NacosServiceDiscovery;
import com.leixiaoshuai.easyrpc.client.discovery.ServiceDiscovery;
import com.leixiaoshuai.easyrpc.client.discovery.ZookeeperServiceDiscovery;
import com.leixiaoshuai.easyrpc.client.network.NettyRpcClient;
import com.leixiaoshuai.easyrpc.listener.DefaultRpcListener;
import com.leixiaoshuai.easyrpc.property.RpcProperties;
import com.leixiaoshuai.easyrpc.serialization.DefaultMessageProtocol;
import com.leixiaoshuai.easyrpc.serialization.MessageProtocol;
import com.leixiaoshuai.easyrpc.server.network.NettyRpcServer;
import com.leixiaoshuai.easyrpc.server.network.RequestHandler;
import com.leixiaoshuai.easyrpc.server.network.RpcServer;
import com.leixiaoshuai.easyrpc.server.registry.DefaultServiceRegistry;
import com.leixiaoshuai.easyrpc.server.registry.NacosServiceRegistry;
import com.leixiaoshuai.easyrpc.server.registry.ServiceRegistry;
import com.leixiaoshuai.easyrpc.server.registry.ZookeeperServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/28
 */
@Configuration
@Slf4j
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
    public RpcProperties rpcProperties() {
        return new RpcProperties();
    }

    // 客户端
    @Bean
    public ServiceDiscovery serviceDiscovery(@Autowired RpcProperties rpcProperties) {
        final String register = rpcProperties.getRegister();
        if ("nacos".equalsIgnoreCase(register)) {
            log.info("Nacos Discovery active.");
            return new NacosServiceDiscovery(rpcProperties.getRegisterAddress());
        } else if ("zookeeper".equalsIgnoreCase(rpcProperties.getRegister())) {
            log.info("zookeeper Discovery active.");
            return new ZookeeperServiceDiscovery(rpcProperties.getRegisterAddress());
        }
        return null;
    }

    @Bean
    public ClientProxyFactory clientProxyFactory(@Autowired ServiceDiscovery serviceDiscovery) {
        return new ClientProxyFactory(serviceDiscovery, new DefaultMessageProtocol(), new NettyRpcClient());
    }

    // 服务端
    @Bean
    public ServiceRegistry serviceRegister(@Autowired RpcProperties rpcProperties) {
        // 根据参数配置自动选择注册中心
        final String register = rpcProperties.getRegister();
        if ("nacos".equalsIgnoreCase(register)) {
            log.info("Nacos register active.");
            return new NacosServiceRegistry(rpcProperties.getRegisterAddress());
        } else if ("zookeeper".equalsIgnoreCase(rpcProperties.getRegister())) {
            log.info("zookeeper register active.");
            return new ZookeeperServiceRegistry(rpcProperties.getRegisterAddress());
        } else {
            log.info("Default register active.");
            return new DefaultServiceRegistry();
        }
    }

    @Bean
    public RequestHandler requestHandler(@Autowired RpcProperties rpcProperties,
                                         @Autowired ServiceRegistry serviceRegistry) {
        final String protocol = rpcProperties.getProtocol();

        MessageProtocol messageProtocol = new DefaultMessageProtocol();
        // TODO 暂时只支持 java 自带的序列化方式，大家可以自行进行扩展，比如：fastjson 等
        if ("java".equalsIgnoreCase(protocol)) {
            messageProtocol = new DefaultMessageProtocol();
        }

        return new RequestHandler(messageProtocol, serviceRegistry);
    }

    @Bean
    public RpcServer rpcServer(@Autowired RpcProperties rpcProperties,
                               @Autowired RequestHandler requestHandler) {
        return new NettyRpcServer(rpcProperties.getExposePort(), requestHandler);
    }

}
