package com.leixiaoshuai.easyrpc.client.discovery;

import com.leixiaoshuai.easyrpc.common.ServiceInfo;

import java.util.List;

/**
 * @Description 客户端服务发现
 * @Author 雷小帅（公众号：爱笑的架构师）
 * @Date 2021/11/24
 */
public interface ServiceDiscovery {

    /**
     * 通过服务名称获取服务提供者暴露的服务列表
     * @param serviceName 服务名称
     * @return 服务列表
     */
    List<ServiceInfo> listServices(String serviceName);
}
