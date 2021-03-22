package io.daiwei.rpc.api;

import net.sf.cglib.proxy.MethodInterceptor;

/**
 * Created by Daiwei on 2021/3/20
 */
public interface RpcCglibInvokeHandler extends MethodInterceptor, RpcInvokeHandler {

}
