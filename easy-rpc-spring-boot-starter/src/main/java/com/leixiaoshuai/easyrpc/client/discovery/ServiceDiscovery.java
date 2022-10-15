package com.leixiaoshuai.easyrpc.client.discovery;

import com.leixiaoshuai.easyrpc.common.ServiceInterfaceInfo;

/**
 * @Description 客户端服务发现
 * @Author 雷小帅（公众号：爱笑的架构师）
 * @Date 2021/11/24
 */
public interface ServiceDiscovery {

    /**
     * 通过服务名称随机选择一个健康的实例
     * @param serviceName 服务名称
     * @return 实例对象
     */
    ServiceInterfaceInfo selectOneInstance(String serviceName);

}
