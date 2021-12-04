package com.leixiaoshuai.easyrpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 定义注解：标识服务提供者，暴露服务接口
 * @Author 雷小帅（公众号：爱笑的架构师）
 * @Date 2021/11/25
 */
@Component
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceExpose {
    String value() default "";
}
