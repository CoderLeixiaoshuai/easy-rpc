package com.leixiaoshuai.easyrpc.exception;

/**
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/30
 */
public class RpcException extends RuntimeException{
    public RpcException(String message) {
        super(message);
    }
}
