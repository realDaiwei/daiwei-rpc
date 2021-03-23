package io.daiwei.rpc.handler;

import io.daiwei.rpc.api.RpcInvokeHandler;
import io.daiwei.rpc.cache.InvokerStubCache;
import io.daiwei.rpc.exception.ConsumerException;
import io.daiwei.rpc.exception.RpcException;
import io.daiwei.rpc.exception.RpcExceptionBuilder;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.Map;

/**
 * 基于 ByteBuddy 字节码生成动态创建调用桩
 * Created by Daiwei on 2021/3/21
 */
public class HttpByteBuddyInvokeHandler implements RpcInvokeHandler {

    @Override
    public <T> T create(Class<T> clazz) {
        InvokerStubCache cache = InvokerStubCache.getInstance();
        try {
            Object service = cache.getIfPresent(clazz.getName());
            if (null == service) {
                T stub = new ByteBuddy().subclass(clazz).method(ElementMatchers.any())
                        .intercept(MethodDelegation.to(new DelegationInvoker())).make().load(this.getClass().getClassLoader())
                        .getLoaded().newInstance();
                cache.put(clazz.getName(), stub);
                return stub;
            }
            return clazz.cast(service);
        } catch (InstantiationException | IllegalAccessException e) {
            RpcException rpcException = RpcExceptionBuilder.builder().wrapper(ConsumerException.class)
                    .msg("invoke stub create failed！").real(e).build();
            rpcException.printError();
            rpcException.printStack();
        }
        return null;
    }


}
