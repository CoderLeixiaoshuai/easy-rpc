package com.leixiaoshuai.easyrpc.example.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 服务提供者启动入口
 *
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/29
 */
@SpringBootApplication
public class ProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }
}
