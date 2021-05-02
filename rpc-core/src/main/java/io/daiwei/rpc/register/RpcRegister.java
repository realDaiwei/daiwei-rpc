package io.daiwei.rpc.register;

import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

import java.util.List;

/**
 * Created by Daiwei on 2021/4/18
 */
public interface RpcRegister {

    void init();

    void start();

    void stop();

    List<String> findAvailableUrls(Class<?> clazz);

    void registerInvokeProxy(Class<?> clazz) throws Exception;

    void registerService(int port, Class<?> clazz) throws Exception;

    void registerListeners(List<CuratorCacheListener> listeners);

    void registerListeners();

}

