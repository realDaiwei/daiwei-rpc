package io.daiwei.rpc.cache;

import com.google.common.cache.CacheBuilder;
import io.daiwei.rpc.api.AbstractRpcCache;
import io.daiwei.rpc.constant.RpcConstant;

import java.util.concurrent.TimeUnit;

/**
 * client 调用方的
 * @author daiwe
 * @version 1.0
 * @Date 2021/3/23 15:19
 */
public class InvokerStubCache extends AbstractRpcCache {

    private static volatile InvokerStubCache instance;

    private InvokerStubCache() {
        this.cache = CacheBuilder.newBuilder().expireAfterAccess(RpcConstant.CACHE_EXPIRE, TimeUnit.SECONDS).build();
    }

    public static InvokerStubCache getInstance() {
        if (instance == null) {
            synchronized (InvokerStubCache.class) {
                if (instance == null) {
                    instance = new InvokerStubCache();
                }
            }
        }
        return instance;
    }
}
