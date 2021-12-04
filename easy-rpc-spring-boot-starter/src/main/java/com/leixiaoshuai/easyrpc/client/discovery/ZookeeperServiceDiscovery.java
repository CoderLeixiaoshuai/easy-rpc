package com.leixiaoshuai.easyrpc.client.discovery;

import com.alibaba.fastjson.JSON;
import com.leixiaoshuai.easyrpc.common.ServiceInfo;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 通过 Zookeeper 实现服务发现
 *
 * @Author 雷小帅（公众号：爱笑的架构师）
 * @Date 2021/11/24
 */
public class ZookeeperServiceDiscovery implements ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperServiceDiscovery.class);

    private ZkClient zkClient;

    public ZookeeperServiceDiscovery(String zkAddress) {
        zkClient = new ZkClient(zkAddress);
        zkClient.setZkSerializer(new ZkSerializer() {
            @Override
            public byte[] serialize(Object o) throws ZkMarshallingError {
                return String.valueOf(o).getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Object deserialize(byte[] bytes) throws ZkMarshallingError {
                return new String(bytes, StandardCharsets.UTF_8);
            }
        });
    }

    @Override
    public List<ServiceInfo> listServices(String serviceName) {
        String servicePath = "/com/leixiaoshuai/easyrpc/" + serviceName + "/service";
        final List<String> childrenNodes = zkClient.getChildren(servicePath);

        return Optional.ofNullable(childrenNodes)
                .orElse(new ArrayList<>())
                .stream()
                .map(node -> {
                    try {
                        // 将服务信息经过 URL 解码后反序列化为对象
                        String serviceInstanceJson = URLDecoder.decode(node, "UTF-8");
                        return JSON.parseObject(serviceInstanceJson, ServiceInfo.class);
                    } catch (UnsupportedEncodingException e) {
                        logger.error("Fail to decode", e);
                    }
                    return null;
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
