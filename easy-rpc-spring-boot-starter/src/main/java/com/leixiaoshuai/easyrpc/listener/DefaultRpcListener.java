package com.leixiaoshuai.easyrpc.listener;

import com.leixiaoshuai.easyrpc.annotation.ServiceExpose;
import com.leixiaoshuai.easyrpc.annotation.ServiceReference;
import com.leixiaoshuai.easyrpc.client.ClientProxyFactory;
import com.leixiaoshuai.easyrpc.common.ServiceInterfaceInfo;
import com.leixiaoshuai.easyrpc.property.RpcProperties;
import com.leixiaoshuai.easyrpc.server.network.RpcServer;
import com.leixiaoshuai.easyrpc.server.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * 定义监听器，用于初始化 RPC 服务端或者客户端
 *
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/28
 */
public class DefaultRpcListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(DefaultRpcListener.class);

    private final ServiceRegistry serviceRegistry;

    private final RpcServer rpcServer;

    private final ClientProxyFactory clientProxyFactory;

    private RpcProperties rpcProperties;

    public DefaultRpcListener(ServiceRegistry serviceRegistry, RpcServer rpcServer,
                              ClientProxyFactory clientProxyFactory, RpcProperties rpcProperties) {
        this.serviceRegistry = serviceRegistry;
        this.rpcServer = rpcServer;
        this.clientProxyFactory = clientProxyFactory;
        this.rpcProperties = rpcProperties;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        final ApplicationContext applicationContext = event.getApplicationContext();
        // 如果是 root application context就开始执行
        if (applicationContext.getParent() == null) {
            // 初始化 rpc 服务端
            initRpcServer(applicationContext);
            // 初始化 rpc 客户端
            initRpcClient(applicationContext);
        }
    }

    private void initRpcServer(ApplicationContext applicationContext) {
        // 1.1 扫描服务端@ServiceExpose注解，并将服务接口信息注册到注册中心
        final Map<String, Object> beans = applicationContext.getBeansWithAnnotation(ServiceExpose.class);
        if (beans.size() == 0) {
            // 没发现注解
            return;
        }

        for (Object beanObj : beans.values()) {
            // 注册服务实例接口信息
            registerInstanceInterfaceInfo(beanObj);
        }

        // 1.2 启动网络通信服务器，开始监听指定端口
        rpcServer.start();
    }

    private void registerInstanceInterfaceInfo(Object beanObj) {
        final Class<?>[] interfaces = beanObj.getClass().getInterfaces();
        if (interfaces.length <= 0) {
            // 注解类未实现接口
            return;
        }

        // 暂时只考虑实现了一个接口的场景
        Class<?> interfaceClazz = interfaces[0];
        String serviceName = interfaceClazz.getName();
        String ip = getLocalAddress();
        Integer port = rpcProperties.getExposePort();

        try {
            // 注册服务
            serviceRegistry.register(new ServiceInterfaceInfo(serviceName, ip, port, interfaceClazz, beanObj));
        } catch (Exception e) {
            logger.error("Fail to register service: {}", e.getMessage());
        }
    }

    private String getLocalAddress() {
        String ip = "127.0.0.1";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
        }
        return ip;
    }

    private void initRpcClient(ApplicationContext applicationContext) {
        // 遍历容器中所有的 bean
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Class<?> clazz = applicationContext.getType(beanName);
            if (clazz == null) {
                continue;
            }

            // 遍历每个 bean 的成员属性，如果成员属性被 @ServiceReference 注解标记，说明依赖rpc远端接口
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                final ServiceReference annotation = field.getAnnotation(ServiceReference.class);
                if (annotation == null) {
                    // 如果该成员属性没有标记该注解，继续找一下
                    continue;
                }

                // 终于找到被注解标记的成员属性了
                Object beanObject = applicationContext.getBean(beanName);
                Class<?> fieldClass = field.getType();
                field.setAccessible(true);
                try {
                    // 注入代理对象值
                    field.set(beanObject, clientProxyFactory.getProxyInstance(fieldClass));
                } catch (IllegalAccessException e) {
                    logger.error("Fail to inject service, bean.name: {}, error.msg: {}", beanName, e.getMessage());
                }
            }
        }
    }
}
