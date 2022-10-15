package com.leixiaoshuai.easyrpc.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/28
 */

@ConfigurationProperties(prefix = "leixiaoshuai.easy.rpc")
@Data
public class RpcProperties {
    private Integer exposePort = 6666;

    private String register;

    private String registerAddress;

    private String protocol = "java";

}
