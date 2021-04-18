package io.daiwei.rpc.register;

import java.util.List;

/**
 * Created by Daiwei on 2021/4/18
 */
public interface RpcRegister {

    void init();

    void start();

    void stop();

    List<String> pullAvailableUrls(Class<?> clazz);

    void registerInvokeProxy(Class<?> clazz) throws Exception;

    void registerService(int port, Class<?> clazz) throws Exception;

}

