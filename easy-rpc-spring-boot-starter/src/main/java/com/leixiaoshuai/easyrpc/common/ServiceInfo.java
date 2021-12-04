package com.leixiaoshuai.easyrpc.common;

import lombok.Data;

/**
 * 服务提供者对外暴露的信息
 * @Author 雷小帅（公众号：爱笑的架构师）
 * @Date 2021/11/24
 */
@Data
public class ServiceInfo {
    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * ip 地址
     */
    private String ip;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * class 对象
     *
     */
    private Class<?> clazz;

    /**
     * bean 对象
     */
    private Object obj;

    public ServiceInfo() {
    }

    public ServiceInfo(String serviceName, String ip, Integer port) {
        this.serviceName = serviceName;
        this.ip = ip;
        this.port = port;
    }

    public ServiceInfo(String serviceName, String ip, Integer port, Class<?> clazz, Object obj) {
        this.serviceName = serviceName;
        this.ip = ip;
        this.port = port;
        this.clazz = clazz;
        this.obj = obj;
    }
}
