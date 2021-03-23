package io.daiwei.rpc.api;

/**
 * @author daiwe
 * @version 1.0
 * @Date 2021/3/23 16:10
 */
public interface RpcCache {

    void put(Object k, Object v);

    Object getIfPresent(Object k);

    void invalidate(Object key);

    void invalidateAll();
}
