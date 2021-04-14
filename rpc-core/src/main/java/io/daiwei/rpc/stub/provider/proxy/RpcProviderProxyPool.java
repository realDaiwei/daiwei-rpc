package io.daiwei.rpc.stub.provider.proxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * service proxy object pool (singleton)
 * Created by Daiwei on 2021/4/14
 */
public class RpcProviderProxyPool {

    private final Map<Class<?>, Object> proxyMap;

    private volatile static RpcProviderProxyPool POOL;

    private RpcProviderProxyPool() {
        this.proxyMap = new ConcurrentHashMap<>();
    }

    public static RpcProviderProxyPool getInstance() {
        if (POOL == null) {
            synchronized (RpcProviderProxyPool.class) {
                if (POOL == null) {
                    POOL = new RpcProviderProxyPool();
                }
            }
        }
        return POOL;
    }

    public void addProxy(Class<?> clazz, Object proxy) {
        this.proxyMap.putIfAbsent(clazz, proxy);
    }

    public void exists(Class<?> clazz) {
        this.proxyMap.containsKey(clazz);
    }

    public Object getProxy(Class<?> clazz) {
        return this.proxyMap.get(clazz);
    }

}
