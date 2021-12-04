package com.leixiaoshuai.easyrpcexampleconsumer;

import com.leixiaoshuai.easyrpc.annotation.ServiceReference;
import com.leixiaoshuai.easyrpc.example.provider.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/12/1
 */
@RestController
public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @ServiceReference
    private HelloService helloService;

    @GetMapping("/hello/{name}")
    public String hello(@PathVariable String name) {
        final String rsp = helloService.sayHello(name);
        logger.info("Receive message from rpc server, msg: {}", rsp);
        return rsp;
    }
}
