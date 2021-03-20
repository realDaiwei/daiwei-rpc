package io.daiwei.rpc.skeleton;

import io.daiwei.rpc.api.RpcTargetResolver;
import io.daiwei.rpc.pojo.RpcFxReq;
import io.daiwei.rpc.pojo.RpcFxResp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by Daiwei on 2021/3/20
 */
public class RpcSkeletonServerStub {

    private final RpcTargetResolver resolver;

    public RpcSkeletonServerStub(RpcTargetResolver resolver) {
        this.resolver = resolver;
    }

    public RpcFxResp invoke(RpcFxReq request) {
        RpcFxResp resp = null;
        try {
            Object resolve = resolver.resolve(request.getServiceClass());
            Method method = findMethodFromClazz(resolve.getClass(), request.getMethod());
            Object res = method.invoke(resolve, request.getArgs());
            resp =  RpcFxResp.ok(res);
        } catch (IllegalAccessException | InvocationTargetException e) {
            resp = RpcFxResp.fail(e);
        }
        return resp;
    }

    public void register(Object obj) {
        Arrays.stream(obj.getClass().getInterfaces()).forEach(o -> resolver.register(o.getName(), obj));
    }

    private Method findMethodFromClazz(Class<?> clazz, String method) {
        return Arrays.stream(clazz.getMethods()).filter(m -> m.getName().equals(method)).findFirst().orElse(null);
    }
}
