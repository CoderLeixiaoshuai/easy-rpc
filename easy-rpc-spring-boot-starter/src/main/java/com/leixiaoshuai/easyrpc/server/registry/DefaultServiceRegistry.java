package com.leixiaoshuai.easyrpc.server.registry;

import com.leixiaoshuai.easyrpc.common.ServiceInterfaceInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 本地服务注册器，用于本地缓存注册的对象，方便后续获取
 *
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/25
 */
public class DefaultServiceRegistry implements ServiceRegistry {
    private final Map<String, ServiceInterfaceInfo> localMap = new HashMap<>();
    protected String protocol;
    protected Integer port;

    @Override
    public void register(ServiceInterfaceInfo serviceInterfaceInfo) throws Exception {
        if (serviceInterfaceInfo == null) {
            throw new IllegalArgumentException("param.invalid");
        }

        String serviceName = serviceInterfaceInfo.getServiceName();
        localMap.put(serviceName, serviceInterfaceInfo);
    }

    @Override
    public ServiceInterfaceInfo getRegisteredObj(String serviceName) throws Exception {
        return localMap.get(serviceName);
    }
}
