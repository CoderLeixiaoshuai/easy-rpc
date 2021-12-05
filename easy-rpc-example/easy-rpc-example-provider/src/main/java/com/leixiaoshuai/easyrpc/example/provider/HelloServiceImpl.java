package com.leixiaoshuai.easyrpc.example.provider;

import com.leixiaoshuai.easyrpc.annotation.ServiceExpose;
import com.leixiaoshuai.easyrpc.example.provider.api.HelloService;

/**
 * Hello World
 * 服务提供者，使用@ServiceExpose注解对外暴露服务
 *
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/29
 */
@ServiceExpose
public class HelloServiceImpl implements HelloService {
    public String sayHello(String name) {
        return "「来自雷小帅的问候」：hello " + name + " , 恭喜你学会了造RPC轮子！";
    }
}
