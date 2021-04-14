package io.daiwei.rpc.stub.provider.invoke;

import io.daiwei.rpc.stub.common.RpcSendable;
import io.daiwei.rpc.stub.net.common.ProviderInvokerCore;
import io.daiwei.rpc.stub.net.params.RpcRequest;
import io.daiwei.rpc.stub.net.params.RpcResponse;
import io.daiwei.rpc.stub.provider.proxy.RpcProviderProxyPool;

/**
 * Created by Daiwei on 2021/4/14
 */
public class ProviderInvoker extends ProviderInvokerCore {

    @Override
    public Object invoke(Class<?> clazz) {
//        RpcProviderProxyPool.getInstance().getProxy()
        return null;
    }


    @Override
    public void requestComingBellRing(RpcRequest request) {
//        invoke();

//        sendBack();
    }


}
