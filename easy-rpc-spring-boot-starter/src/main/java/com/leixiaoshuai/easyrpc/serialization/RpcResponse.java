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
    // 调用成功或失败
    private String status;
    // 返回值对象
    private Object retValue;
    private Map<String, String> headers = new HashMap<>();
    // 如果失败，返回异常对象
    private Exception exception;
}
