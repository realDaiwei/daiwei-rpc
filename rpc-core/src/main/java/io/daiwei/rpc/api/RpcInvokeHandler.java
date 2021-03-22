package io.daiwei.rpc.api;

/**
 * Created by Daiwei on 2021/3/21
 */
public interface RpcInvokeHandler {

    <T> T create(Class<T> clazz);


}
