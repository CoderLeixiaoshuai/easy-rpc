package com.leixiaoshuai.easyrpc.example.provider.api;

/**
 * Hello World
 *
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/29
 */
public interface HelloService {
    /**
     * 打招呼
     *
     * @param name 名称
     * @return 问候语
     */
    String sayHello(String name);
}
