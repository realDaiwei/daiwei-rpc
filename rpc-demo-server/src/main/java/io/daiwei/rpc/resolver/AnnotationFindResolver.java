package io.daiwei.rpc.resolver;

import io.daiwei.rpc.anntataion.RpcService;
import io.daiwei.rpc.api.RpcTargetResolver;
import org.reflections.Reflections;

import java.util.*;

/**
 * Created by Daiwei on 2021/3/21
 */
public class AnnotationFindResolver implements RpcTargetResolver {

    private final String scanPackagePath;

    private final Map<String, Object> serviceMap;

    public AnnotationFindResolver(String scanPackagePath) {
        this.scanPackagePath = scanPackagePath;
        serviceMap = new HashMap<>();
    }

    @Override
    public Object resolve(String clazzName) {
        Object service = serviceMap.get(clazzName);
        Objects.requireNonNull(service);
        return service;
    }


    @Override
    public void register() {
        Objects.requireNonNull(this.scanPackagePath);
        Reflections reflections = new Reflections(this.scanPackagePath);
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(RpcService.class);
        for (Class<?> clazz : classSet) {
            try {
                Object o = clazz.newInstance();
                Arrays.stream(clazz.getInterfaces()).forEach(aClazz -> serviceMap.put(aClazz.getName(), o));
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
