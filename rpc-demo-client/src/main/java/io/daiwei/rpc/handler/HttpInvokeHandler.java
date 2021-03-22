package io.daiwei.rpc.handler;

import com.alibaba.fastjson.JSON;
import io.daiwei.rpc.api.RpcCglibInvokeHandler;
import io.daiwei.rpc.pojo.RpcFxReq;
import io.daiwei.rpc.pojo.RpcFxResp;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by Daiwei on 2021/3/20
 */
public class HttpInvokeHandler implements RpcCglibInvokeHandler {

    private final Enhancer enhancer;

    private Class<?> proxyClass;

    public HttpInvokeHandler() {
        enhancer = new Enhancer();
    }

    @Override
    public <T> T create(Class<T> clazz) {
        enhancer.setCallback(this);
        enhancer.setSuperclass(clazz);
        this.proxyClass = clazz;
        return clazz.cast(enhancer.create());
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        RpcFxReq rpcFxReq = new RpcFxReq(this.proxyClass.getName(), method.getName(), objects);
        RpcFxResp response = HttpInvokerPoster.post(rpcFxReq);
        return JSON.parseObject(response.getData().toString(), method.getReturnType());
    }


}
