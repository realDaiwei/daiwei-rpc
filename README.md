# daiwei-rpc framework

daiwei-rpc 是一个轻量级高性能的 rpc 框架

### 开发背景

我认为学习一个东西最好的方式，就是动手做一遍。把坑都走一遍，有些东西也就忘不了。我想利用闲暇时间动手自己写一套中间件，但现在所有的中间件几乎都是分布式的，所以这第一步就选择了rpc。写这个rpc 的过程中，我遇到了很多困难，但也收获不少。因此我以秦金卫老师在极客大学开设的《Java 进阶训练营》中的 rpc 设计要求和何小锋老师在极客时间开设的专栏 《RPC实战与核心原理》中提到的设计点为设计需求来完成 daiwei-rpc 的设计与开发。 

### 项目基础架构图

![](https://gitee.com/realDaiwei/img/raw/master/20210503182853.png)



整体系统架构分为四个部分，上层的register server 注册中心，下层的网络部分，左边 invoker 部分， 右边是 provider。 register server使用的 zookeeper 作为注册中心，下层使用的是 Netty + TCP 作为网络层。左边的的 invoker 模块 包括 invoke unit 和 register unit， register unit 处理 zookeep 中的数据并监听注册上来的服务，来及时更新可用的服务地址。invoke unit 是client 的调用部分，在这个部分通过封装的 netty 客户端对服务端进行调用。客户端通过 invoker unit 和 register unit 的协作，为每个不同的接口创建动态代理对象。右边的部分是 server 模块，server 模块从上到下依此是 register unit 注册模块，service 目标服务，proxy stub 代理注册桩，proxy pool 代理服务池 内部存放的是代理服务 proxy，proxy invoker 代理对象的 invoker，通过 这个统一的服务调用 代理池中的代理对象，最下面是封装收发数据的 netty 网络层。 

### 已实现功能

+ 基于ZooKeeper 服务的注册和发现。
+ 心跳探活，空闲自动断开。
+ 异常重试，故障转移 failover。
+ 优雅启停。
+ 上线预热保护。
+ 基于服务端硬件使用情况和可用率的健康检测。
+ 整合 spring（xml 和 starter）
+ 实现权重随机的负载策略
+ 基于 version 的 多版本路由策略。
+ 基于 spi 可拓展的过滤链和路由策略 （参数路由）。
+ ......

### 规划开发功能及开发方向

+ 泛化调用。

+ 基于 redis 的注册中心。

+ 基于 hessian 的额扩展性协议。
+ 实现基于etcd/nacos/apollo等基座的配置/注册/元数据中心。
+ 熔断限流 服务治理

### 项目结构

daiwei-rpc

​	|— rpc-core  		rpc 的核心模块 

​    |— rpc-spring 	  rpc spring 整合包，提供 xml 的配置方式

​    |— rpc-springboot-start 	rpc core 整合 springboot 的 starter 

​	|— rpc-test 			rpc 测试代码

​		|— rpc-test-demo    rpc frameless 的整合方式

​		|— rpc-test-entity    rpc 公用 entity 和 service

​		|— rpc-test-spring-demo 	rpc 整合 spring 的 demo

​		|— rpc-test-springboot        rpc 整合 springboot

> ⚠️  特别注意 项目中使用 zookeeper 客户端  curator-framework 版本为 5.1.0，启动项目时请使用 zookeeper 3.6.x（推荐 3.6.2），否则会存在zookeeper 节点监听问题。

