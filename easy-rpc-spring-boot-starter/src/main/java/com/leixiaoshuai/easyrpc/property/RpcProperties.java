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
    private String applicationName = "rpc-server";

    private Integer exposePort = 6666;

    private String zkAddress = "127.0.0.1:2181";

    private String protocol = "java";
}
