package com.leixiaoshuai.easyrpc.listener;

import com.leixiaoshuai.easyrpc.client.ClientProxyFactory;
import com.leixiaoshuai.easyrpc.annotation.ServiceExpose;
import com.leixiaoshuai.easyrpc.annotation.ServiceReference;
import com.leixiaoshuai.easyrpc.common.ServiceInfo;
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
        // 如果是root application context就开始执行
        if (applicationContext.getParent() == null) {
            // 启动 rpc 服务端
            startRpcServer(applicationContext);

            // 注入依赖的远端rpc服务
            injectDependencyService(applicationContext);
        }
    }

    private void startRpcServer(ApplicationContext applicationContext) {
        final Map<String, Object> beans = applicationContext.getBeansWithAnnotation(ServiceExpose.class);
        if (beans.size() == 0) {
            return;
        }

        for (Object obj : beans.values()) {
            final Class<?> clazz = obj.getClass();
            final Class<?>[] interfaces = clazz.getInterfaces();
            // 这里假设只实现了一个接口
            final Class<?> interfaceClazz = interfaces[0];

            final String name = interfaceClazz.getName();
            String ip = "127.0.0.1";
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {

            }
            final Integer port = rpcProperties.getExposePort();
            final ServiceInfo serviceInfo = new ServiceInfo(name, ip, port, interfaceClazz, obj);

            try {
                // 注册服务
                serviceRegistry.register(serviceInfo);
            } catch (Exception e) {
                logger.error("Fail to register service: {}", e.getMessage());
            }
        }

        // 启动 rpc 服务器，开始监听端口
        rpcServer.start();
    }

    private void injectDependencyService(ApplicationContext applicationContext) {
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
