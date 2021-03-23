package io.daiwei.rpc.skeleton;

import io.daiwei.rpc.api.RpcTargetResolver;
import io.daiwei.rpc.exception.ProviderException;
import io.daiwei.rpc.exception.RpcException;
import io.daiwei.rpc.exception.RpcExceptionBuilder;
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
            resp = method == null ? RpcFxResp.fail(RpcExceptionBuilder.builder().msg(resolve.getClass() + "#" + request.getMethod() + " not find")
                    .real(new NullPointerException(resolve.getClass() + "#" + request.getMethod() +" is null")).build())
                    : RpcFxResp.ok(method.invoke(resolve, request.getArgs()));
        } catch (IllegalAccessException | InvocationTargetException e) {
            resp = RpcFxResp.fail(RpcExceptionBuilder.builder().msg("provider invoke actual failed.")
                    .real(e).build());
        }
        return resp;
    }

    private Method findMethodFromClazz(Class<?> clazz, String method) {
        return Arrays.stream(clazz.getMethods()).filter(m -> m.getName().equals(method)).findFirst().orElse(null);
    }
}
