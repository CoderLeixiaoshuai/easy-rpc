package com.leixiaoshuai.easyrpc.server.registry;

import com.leixiaoshuai.easyrpc.common.ServiceInterfaceInfo;

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
     * @param serviceInterfaceInfo 待注册的服务(接口)信息
     * @throws Exception 异常
     */
    void register(ServiceInterfaceInfo serviceInterfaceInfo) throws Exception;

    /**
     * 根据服务名称和接口名称获取已注册的对象
     *
     * @param serviceName 服务名
     * @return 已注册的对象
     * @throws Exception 异常
     */
    ServiceInterfaceInfo getRegisteredObj(String serviceName) throws Exception;

}
