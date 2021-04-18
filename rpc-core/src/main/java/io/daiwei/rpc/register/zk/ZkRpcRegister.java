package io.daiwei.rpc.register.zk;

import io.daiwei.rpc.register.RegisterConstant;
import io.daiwei.rpc.register.RpcRegister;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daiwei on 2021/4/18
 */
public abstract class ZkRpcRegister implements RpcRegister {

    protected String zkConnStr;

    protected CuratorFramework client;

    @Override
    public void init() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.builder().connectString(zkConnStr).namespace(RegisterConstant.DAIWEI_RPC_NAME_SPACE)
                .retryPolicy(retryPolicy).build();
    }

    @Override
    public void start() {
        client.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> client.close()));
    }

    @Override
    public void stop() {
        if (this.client != null) {
            this.client.close();
        }
    }

    @Override
    public List<String> pullAvailableUrls(Class<?> clazz) {
        return new ArrayList<>();
    }

    @Override
    public void registerInvokeProxy(Class<?> clazz) throws Exception {}

    @Override
    public void registerService(int port, Class<?> clazz) throws Exception {}
}
