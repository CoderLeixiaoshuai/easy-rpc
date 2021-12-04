package com.leixiaoshuai.easyrpc.server.registry;

import com.leixiaoshuai.easyrpc.common.ServiceInfo;

/**
 * 服务注册
 *
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/25
 */
public interface ServiceRegistry {
    /**
     * 注册服务信息
     *
     * @param serviceInfo 待注册的服务
     * @throws Exception 异常
     */
    void register(ServiceInfo serviceInfo) throws Exception;

    /**
     * 根据服务名称获取服务信息
     *
     * @param name 服务名称
     * @return 服务信息
     * @throws Exception 异常
     */
    ServiceInfo getServiceInstance(String name) throws Exception;
}
