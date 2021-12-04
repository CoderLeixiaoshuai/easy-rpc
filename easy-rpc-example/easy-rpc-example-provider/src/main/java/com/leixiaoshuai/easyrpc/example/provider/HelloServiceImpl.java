package com.leixiaoshuai.easyrpc.example.provider;

import com.leixiaoshuai.easyrpc.annotation.ServiceExpose;
import com.leixiaoshuai.easyrpc.example.provider.api.HelloService;

/**
 * Hello World
 *
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/29
 */
@ServiceExpose
public class HelloServiceImpl implements HelloService {
    public String sayHello(String name) {
        return "「来自服务提供者消息」：hello " + name + " , 恭喜你学会了造RPC轮子！";
    }
}
