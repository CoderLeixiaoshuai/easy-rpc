package com.leixiaoshuai.easyrpc.serialization;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/26
 */
@Data
public class RpcResponse implements Serializable {
    private String status;
    private Map<String, String> headers = new HashMap<>();
    private Object retValue;
    private Exception exception;
}
