package com.leixiaoshuai.easyrpc.server.registry;

import com.alibaba.fastjson.JSON;
import com.leixiaoshuai.easyrpc.common.ServiceInterfaceInfo;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * 通过 zookeeper 提供服务注册能力
 *
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/28
 */
public class ZookeeperServiceRegistry extends DefaultServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperServiceRegistry.class);
    private ZkClient zkClient;

    public ZookeeperServiceRegistry(String zkAddress) {
        init(zkAddress);
    }

    private void init(String zkAddress) {
        // 初始化，与 Zookeeper 服务器建立连接
        zkClient = new ZkClient(zkAddress);
        // 设置序列化反序列化器
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
    public void register(ServiceInterfaceInfo serviceInterfaceInfo) throws Exception {
        logger.info("Registering service: {}", serviceInterfaceInfo);

        super.register(serviceInterfaceInfo);

        // 创建 ZK 永久节点（服务节点）
        String serviceName = serviceInterfaceInfo.getServiceName();
        String servicePath = "/com/leixiaoshuai/easyrpc/service/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath, true);
            logger.info("Created node: {}", servicePath);
        }

        // 创建 ZK 临时节点（实例节点）
        String uri = JSON.toJSONString(serviceInterfaceInfo);
        uri = URLEncoder.encode(uri, "UTF-8");
        String uriPath = servicePath + "/" + uri;
        if (zkClient.exists(uriPath)) {
            zkClient.delete(uriPath);
        }
        zkClient.createEphemeral(uriPath);
        logger.info("Created ephemeral node: {}", uriPath);
    }

}
