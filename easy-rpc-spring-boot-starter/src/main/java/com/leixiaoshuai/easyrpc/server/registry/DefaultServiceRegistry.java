package com.leixiaoshuai.easyrpc.server.registry;

import com.leixiaoshuai.easyrpc.common.ServiceInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 本地服务注册器
 *
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/25
 */
public class DefaultServiceRegistry implements ServiceRegistry {
    protected String protocol;
    protected Integer port;
    private Map<String, ServiceInfo> serviceInfoHashMap = new HashMap<>();

    @Override
    public void register(ServiceInfo serviceInfo) throws Exception {
        if (serviceInfo == null) {
            throw new IllegalArgumentException("param.invalid");
        }
        final String name = serviceInfo.getServiceName();
        serviceInfoHashMap.put(name, serviceInfo);
    }

    @Override
    public ServiceInfo getServiceInstance(String name) throws Exception {
        return serviceInfoHashMap.get(name);
    }
}
