package com.leixiaoshuai.easyrpc.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 默认使用 Java 自带序列化和反序列化工具
 *
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/28
 */
public class DefaultMessageProtocol implements MessageProtocol {
    @Override
    public byte[] marshallingReqMessage(RpcRequest request) throws Exception {
        return serialize(request);
    }

    @Override
    public RpcRequest unmarshallingReqMessage(byte[] data) throws Exception {
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
        return (RpcRequest) in.readObject();
    }

    @Override
    public byte[] marshallingRespMessage(RpcResponse response) throws Exception {
        return serialize(response);
    }

    @Override
    public RpcResponse unmarshallingRespMessage(byte[] data) throws Exception {
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
        return (RpcResponse) in.readObject();
    }

    private byte[] serialize(Object obj) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(obj);
        return baos.toByteArray();
    }
}
