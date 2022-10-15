package com.leixiaoshuai.easyrpc.client.discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.leixiaoshuai.easyrpc.common.ServiceInterfaceInfo;
import com.leixiaoshuai.easyrpc.server.registry.NacosServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2022/6/16
 */
@Slf4j
public class NacosServiceDiscovery implements ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);
    private NamingService namingService;

    public NacosServiceDiscovery(String serverList) {
        // 使用工厂类创建注册中心对象，构造参数为 Nacos Server 的 ip 地址，连接 Nacos 服务器
        try {
            namingService = NamingFactory.createNamingService(serverList);
        } catch (NacosException e) {
            log.error("Nacos client init error", e);
        }
        // 打印 Nacos Server 的运行状态
        logger.info("Nacos server status: {}", namingService.getServerStatus());
    }

    @Override
    public ServiceInterfaceInfo selectOneInstance(String serviceName) {
        Instance instance;
        try {
            // 调用 nacos 提供的接口，随机挑选一个服务实例，负载均衡的算法依赖 nacos 的实现
            instance = namingService.selectOneHealthyInstance(serviceName);
        } catch (NacosException e) {
            logger.error("Nacos exception", e);
            return null;
        }

        // 封装实例对象返回
        ServiceInterfaceInfo serviceInterfaceInfo = new ServiceInterfaceInfo();
        serviceInterfaceInfo.setServiceName(instance.getServiceName());
        serviceInterfaceInfo.setIp(instance.getIp());
        serviceInterfaceInfo.setPort(instance.getPort());
        return serviceInterfaceInfo;
    }


}
