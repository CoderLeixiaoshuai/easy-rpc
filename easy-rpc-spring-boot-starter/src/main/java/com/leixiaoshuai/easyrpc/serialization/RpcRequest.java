package com.leixiaoshuai.easyrpc.serialization;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/26
 */
@Data
@ToString
public class RpcRequest implements Serializable {
    private String serviceName;
    private String methodName;
    private Map<String, String> headers = new HashMap<>();
    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
