package io.daiwei.rpc.register.zk;

import io.daiwei.rpc.register.RegisterConstant;
import io.daiwei.rpc.register.RpcRegister;
import io.daiwei.rpc.stub.net.NetConstant;
import lombok.Data;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    }

    @Override
    public void stop() {
        if (this.client != null && CuratorFrameworkState.STOPPED != this.client.getState()) {
            this.client.close();
        }
    }


    @Override
    public void registerInvokeProxy(Class<?> clazz) throws Exception {}

    @Override
    public void registerService(int port, Class<?> clazz) throws Exception {}

    @Override
    public final void registerListeners(List<CuratorCacheListener> listeners) {
        CuratorCache cache = CuratorCache.builder(client, NetConstant.FILE_SEPARATOR).build();
        for (CuratorCacheListener listener : listeners) {
            cache.listenable().addListener(listener);
        }
        cache.start();
    }

    @Override
    public void registerListeners() {}

    @Override
    public List<String> findAvailableUrls(Class<?> clazz) {
        return null;
    }
}
