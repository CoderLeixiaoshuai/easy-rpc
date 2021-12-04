package com.leixiaoshuai.easyrpc.annotation;

import java.lang.annotation.*;

/**
 * 定义注解：注入远端服务
 *
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/26
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceReference {
}
