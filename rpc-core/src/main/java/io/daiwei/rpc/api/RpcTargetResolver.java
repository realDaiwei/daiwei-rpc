package io.daiwei.rpc.api;

/**
 * Created by Daiwei on 2021/3/20
 */
public interface RpcTargetResolver {

    Object resolve(String clazzName);

    void register(String name, Object service);

}
