package io.daiwei.rpc.stub.provider.component;

import io.daiwei.rpc.exception.DaiweiRpcException;
import io.daiwei.rpc.stub.provider.invoke.RpcProviderProxyPool;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * Created by Daiwei on 2021/4/14
 */
public class ProviderRegisterUnit {

    /**
     * 注册被调用的 proxy 到 proxyPool 中
     * @param clazz
     */
    public void registerInvokeProxy(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces.length == 0) {
            throw new DaiweiRpcException("not find interface of  " + clazz.getCanonicalName());
        }
        if (interfaces.length != 1) {
            throw new DaiweiRpcException("find more than one interfaces of " + clazz.getCanonicalName());
        }
        Class<?> clazzInterface = clazz.getInterfaces()[0];
        String clazzName = clazz.getCanonicalName() + "$$daiweiRpxProxyByByteBuddy";
        Object proxy = new ByteBuddy().subclass(clazz).name(clazzName)
                .method(ElementMatchers.any()).intercept(MethodCall.invokeSuper().withAllArguments())
                .make().load(ProviderRegisterUnit.class.getClassLoader()).getLoaded().newInstance();
        RpcProviderProxyPool.getInstance().addProxy(clazzInterface, proxy);
    }
}
