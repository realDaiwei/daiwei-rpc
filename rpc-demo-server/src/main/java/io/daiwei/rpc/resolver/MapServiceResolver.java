package io.daiwei.rpc.resolver;

import io.daiwei.rpc.api.RpcTargetResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daiwei on 2021/3/20
 */
public class MapServiceResolver implements RpcTargetResolver {

    private final Map<String, Object> serviceMap;

    public MapServiceResolver() {
        this.serviceMap = new HashMap<>();
    }

    @Override
    public Object resolve(String clazzName) {
        return serviceMap.get(clazzName);
    }

    @Override
    public void register(String name, Object service) {
        serviceMap.put(name, service);
    }
}
