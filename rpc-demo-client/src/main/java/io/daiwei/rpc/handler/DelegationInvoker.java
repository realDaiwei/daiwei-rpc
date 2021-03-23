package io.daiwei.rpc.handler;

import com.alibaba.fastjson.JSON;
import io.daiwei.rpc.constant.RpcConstant;
import io.daiwei.rpc.exception.ConsumerException;
import io.daiwei.rpc.exception.ProviderException;
import io.daiwei.rpc.exception.RpcException;
import io.daiwei.rpc.exception.RpcExceptionBuilder;
import io.daiwei.rpc.pojo.RpcFxReq;
import io.daiwei.rpc.pojo.RpcFxResp;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by Daiwei on 2021/3/22
 */
public class DelegationInvoker {

    @RuntimeType
    public Object intercept(@This Object target, @AllArguments Object[] args, @Origin Method method) {
        Class<?> proxyClass = target.getClass().getInterfaces()[0];
        RpcFxReq req = new RpcFxReq(proxyClass.getName(), method.getName(), args);
        RpcFxResp resp = null;
        try {
            resp = HttpInvokerPoster.post(req);
            return resp.getCode() == RpcConstant.SUCCESS ? JSON.parseObject(resp.getData().toString(), method.getReturnType())
                    : handleException(resp.getException());
        } catch (IOException e) {
//            e.printStackTrace();
            RpcException rpcException = RpcExceptionBuilder.builder().msg("http invoke failed")
                    .wrapper(ConsumerException.class).real(e).build();
            rpcException.printError();
            rpcException.printStack();
            rpcException.throwWrapped();
        }
        return null;
    }

    /**
     * 处理策略这个版本简单处理
     * @param e
     * @return
     */
    public Object handleException(Exception e) {
        RpcException rpcException = RpcExceptionBuilder.builder().msg("invoke failed")
                .wrapper(ProviderException.class).real(e).build();
        rpcException.printError();
        rpcException.printStack();
        return null;
    }




}
