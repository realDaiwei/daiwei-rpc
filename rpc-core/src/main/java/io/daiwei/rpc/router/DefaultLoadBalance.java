package io.daiwei.rpc.router;

import io.daiwei.rpc.exception.DaiweiRpcException;
import io.daiwei.rpc.router.common.LoadBalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Daiwei on 2021/4/18
 */
public class DefaultLoadBalance implements LoadBalance {

    private final AtomicInteger cnt = new AtomicInteger();

    @Override
    public String select(List<String> urls) {
        if (!urls.isEmpty()) {
            return urls.get(cnt.getAndIncrement() % urls.size());
        } else {
            throw new DaiweiRpcException("no available server url");
        }
    }
}
