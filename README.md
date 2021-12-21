# easy-RPC Framework 介绍

## 为什么要学习造轮子

> 我觉得从个人成长上说，如果一个程序员能清楚的了解RPC框架所具备的要素，掌握RPC框架中涉及的服务注册发现、负载均衡、序列化协议、RPC通信协议、Socket通信、异步调用、熔断降级等技术，可以全方位的提升基本素质。虽然也有相关源码，但是只看源码容易眼高手低，动手写一个才是自己真正掌握这门技术的最优路径。

## easy-RPC 框架是什么？

easy-RPC 是一款低性能的服务框架，主要用于学习造轮子！

学习本项目可以让你从零开始实现一个类似 Dubbo 服务框架 mini 版RPC。

如果你认真学下来，可以掌握以下的技术：

1. 底层网络层基于 netty，学完 netty 入门没有问题；
2. 使用自定义注解，学完可以了解注解的基本运行机制；
3. 服务注册基于 zookeeper，学完 zk 入门没有问题；
4. 会用到反射机制；
5. 会用到动态代理技术；
6. 教你如何定义一个 xxx-spring-boot-starter，了解spring boot自动配置机制；
7. 学会如何自定义配置项，并绑定到 bean；
8. 学习监听 spring 容器的事件；
9. ……等等

有没有一点心动呢？！

## 学习教程（拼命更新中，欢迎催更……）

- [「从零开始造 RPC 轮子系列」01 我为什么要去造一个轮子？](https://mp.weixin.qq.com/s/b_qUjmofSzr70BeMBZNtTg)
- [「从零开始造 RPC 轮子系列」02 演示轮子，是驴是马拉出来遛遛](https://mp.weixin.qq.com/s/yqCv53O6A7RRH0Hs1lCKuQ)
- [「从零开始造 RPC 轮子系列」03 完事具备，只差一个环境搭建](https://mp.weixin.qq.com/s/Pnr3JT5FzBU3j5HgouFiiw)
- [「从零开始造 RPC 轮子系列」03 手绘 4 张图带你入门 RPC 服务框架](https://mp.weixin.qq.com/s?__biz=MzIwODI1OTk1Nw==&mid=2650326772&idx=1&sn=1bb58a7f5258c12d0e66bf6bc227045a&chksm=8f09b8eeb87e31f89891a40e7daa78a380018917ec881b5dfa25b6e144dd59d0944afc0bc00f&token=454027978&lang=zh_CN#rd)

# 快速开始

## 环境准备

- JDK8 或以上
- Maven 3
- Zookeeper 单机或者集群实例
- IntelliJ IDEA

## 编译安装源码


> 敲黑板：以下指导文档涉及到的演示代码已存放在easy-rpc-example这个目录下。

下载源码

```bash
git clone git@github.com:CoderLeixiaoshuai/easy-rpc.git
```

编译安装 jar 包到本地仓库 

```bash
mvn clean install
```

## 新建 Spring Boot Maven 工程

在本地新建两个工程，用于模拟客户端和服务端。

![image-20211204154920063](https://cdn.jsdelivr.net/gh/CoderLeixiaoshuai/assets/202105/20211204154920.png)

## 引入依赖

```xml
<dependency>
    <groupId>com.leixiaoshuai</groupId>
    <artifactId>easy-rpc-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## 服务端配置

### 暴露接口

定义一个服务接口

```java
/**
 * Hello World
 *
 * @author 雷小帅（公众号搜：爱笑的架构师）
 * @since 2021/11/29
 */
public interface HelloService {
    /**
     * 打招呼
     *
     * @param name 名称
     * @return 问候语
     */
    String sayHello(String name);
}
```

实现接口，使用自定义注解@ServiceExpose 暴露一个服务接口

```java
/**
 * Hello World
 *
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/29
 */
@ServiceExpose
public class HelloServiceImpl implements HelloService {
    public String sayHello(String name) {
        return "「来自雷小帅的问候」：hello " + name + " , 恭喜你学会了造RPC轮子！";
    }
}
```

### 配置注册中心地址

当前项目只支持 zookeeper 作为注册中心。服务端（provider）使用 zookeeper 为了暴露服务接口。

![image-20211204160719928](https://cdn.jsdelivr.net/gh/CoderLeixiaoshuai/assets/202105/20211204160720.png)

```properties
# application.properties

# rpc 服务暴露的端口
leixiaoshuai.easy.rpc.expose-port=6666
# zookeeper 注册中心地址
leixiaoshuai.easy.rpc.zk-address=127.0.0.1:2181
```

## 客户端配置

### 注入远端服务

使用自定义注解 @ServiceReference 自动注入服务端暴露的接口服务

```java
/**
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/12/1
 */
@RestController
public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @ServiceReference
    private HelloService helloService;

    @GetMapping("/hello/{name}")
    public String hello(@PathVariable String name) {
        final String rsp = helloService.sayHello(name);
        logger.info("Receive message from rpc server, msg: {}", rsp);
        return rsp;
    }
}
```

### 配置注册中心地址

客户端配置 zookeeper 是为了订阅发现服务端暴露的服务接口

![image-20211204163254215](https://cdn.jsdelivr.net/gh/CoderLeixiaoshuai/assets/202105/20211204163254.png)

```properties
# application.properties

# zookeeper 实例地址
leixiaoshuai.easy.rpc.zk-address=127.0.0.1:2181
```

## 启动测试

### 运行服务端（服务提供者）

```java
/**
 * 服务提供者启动入口
 *
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/11/29
 */
@SpringBootApplication
public class ProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }
}
```

出现下面的日志，说明启动成功

![image-20211204181718250](https://cdn.jsdelivr.net/gh/CoderLeixiaoshuai/assets/202105/20211204181718.png)

### 运行客户端（服务消费者）

```java
/**
 * 服务消费者启动入口
 * 
 * @author 雷小帅（公众号：爱笑的架构师）
 * @since 2021/12/01
 */
@SpringBootApplication
public class ConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
```

出现下面的日志，说明启动成功

![image-20211204185740073](https://cdn.jsdelivr.net/gh/CoderLeixiaoshuai/assets/202105/20211204185740.png)

### 测试

使用浏览器输入请求地址测试：

```text
http://127.0.0.1:8081/hello/输入测试字符串
```

返回下面的字符串就说明运行成功

![image-20211204185847139](https://cdn.jsdelivr.net/gh/CoderLeixiaoshuai/assets/202105/20211204185847.png)

## FAQ

**1、zookeeper 连接失败**

![image-20211204174618073](https://cdn.jsdelivr.net/gh/CoderLeixiaoshuai/assets/202105/20211204174618.png)

解决方法：

（1）在本地机器或者在服务器上安装运行 zookeeper 实例；

（2）在配置文件中正确配置 zookeeper 地址；

# 答疑或技术交流群

如果你对从零开始手写 RPC 项目感兴趣，或者你遇到问题了，或者你想进群跟其他小伙伴一起交流，都非常欢迎联系我。

微信搜索公众号：`爱笑的架构师`，回复暗号：`rpc`

<div align="center">
    <img src="https://cdn.jsdelivr.net/gh/smileArchitect/assets@main/202012/20201205221844.png"></img>
</div>


等你来~

