package com.leixiaoshuai.easyrpc.server.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.leixiaoshuai.easyrpc.common.ServiceInterfaceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通过 Nacos 提供服务注册能力
 *
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2022/5/29
 */
public class NacosServiceRegistry extends DefaultServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);
    private NamingService naming;

    public NacosServiceRegistry(String serverList) {
        // 使用工厂类创建注册中心对象，构造参数为 Nacos Server 的 ip 地址，连接 Nacos 服务器
        try {
            naming = NamingFactory.createNamingService(serverList);
        } catch (NacosException e) {
            logger.error("Nacos init error", e);
        }
        // 打印 Nacos Server 的运行状态
        logger.info("Nacos server status: {}", naming.getServerStatus());
    }

    @Override
    public void register(ServiceInterfaceInfo serviceInterfaceInfo) throws Exception {
        super.register(serviceInterfaceInfo);
        // 注册当前服务实例
        String serviceName = serviceInterfaceInfo.getServiceName();
        naming.registerInstance(serviceName, buildInstance(serviceInterfaceInfo));
    }

    private Instance buildInstance(ServiceInterfaceInfo serviceInterfaceInfo) {
        // 将实例信息注册到 Nacos 中心
        Instance instance = new Instance();
        instance.setIp(serviceInterfaceInfo.getIp());
        instance.setPort(serviceInterfaceInfo.getPort());
        // TODO add more metadata
        return instance;
    }

}
