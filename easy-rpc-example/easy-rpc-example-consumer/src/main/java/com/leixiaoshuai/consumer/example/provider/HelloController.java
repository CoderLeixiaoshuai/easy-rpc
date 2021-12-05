package com.leixiaoshuai.consumer.example.provider;

import com.leixiaoshuai.easyrpc.annotation.ServiceReference;
import com.leixiaoshuai.easyrpc.example.provider.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务消费者，用于模拟客户端调用远端服务
 *
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/12/1
 */
@RestController
public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    // 通过自定义注解注入远端服务
    @ServiceReference
    private HelloService helloService;

    @GetMapping("/hello/{name}")
    public String hello(@PathVariable String name) {
        // 这里调用的是远端服务的接口，感觉像是调本地服务一样
        final String rsp = helloService.sayHello(name);
        logger.info("Receive message from rpc server, msg: {}", rsp);
        return rsp;
    }
}
