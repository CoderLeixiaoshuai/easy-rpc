package com.leixiaoshuai.consumer.example.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 服务消费者启动入口
 *
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/12/01
 */
@SpringBootApplication
public class ConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
