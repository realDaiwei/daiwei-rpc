package io.daiwei.rpc.api;

import com.google.common.cache.Cache;

/**
 * @author daiwe
 * @version 1.0
 * @Date 2021/3/23 16:51
 */
public abstract class AbstractRpcCache implements RpcCache {

    protected Cache<Object, Object> cache;

    @Override
    public void put(Object k, Object v) {
        this.cache.put(k, v);
    }

    @Override
    public Object getIfPresent(Object k) {
        return this.cache.getIfPresent(k);
    }

    @Override
    public void invalidate(Object key) {
        this.cache.invalidate(key);
    }

    @Override
    public void invalidateAll() {
        this.cache.invalidateAll();
    }
}
