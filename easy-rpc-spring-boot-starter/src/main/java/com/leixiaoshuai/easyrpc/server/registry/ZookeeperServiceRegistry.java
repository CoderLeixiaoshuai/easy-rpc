package com.leixiaoshuai.easyrpc.server.registry;

import com.alibaba.fastjson.JSON;
import com.leixiaoshuai.easyrpc.common.ServiceInfo;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 通过 zookeeper 提供服务注册能力
 *
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/28
 */
public class ZookeeperServiceRegistry extends DefaultServiceRegistry implements ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperServiceRegistry.class);
    private final ZkClient zkClient;

    public ZookeeperServiceRegistry(String zkAddress, Integer port, String protocol) {
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

        this.port = port;
        this.protocol = protocol;
    }

    @Override
    public void register(ServiceInfo serviceInfo) throws Exception {
        logger.info("Registering service: {}", serviceInfo);
        super.register(serviceInfo);

        String uri = JSON.toJSONString(serviceInfo);
        uri = URLEncoder.encode(uri, "UTF-8");

        String servicePath = "/com/leixiaoshuai/easyrpc/" + serviceInfo.getServiceName() + "/service";
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath, true);
        }

        String uriPath = servicePath + "/" + uri;
        if (zkClient.exists(uriPath)) {
            zkClient.delete(uriPath);
        }
        zkClient.createEphemeral(uriPath);
    }

}
