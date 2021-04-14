package io.daiwei.rpc.stub.net.common;

import io.daiwei.rpc.stub.common.RpcSendable;
import io.daiwei.rpc.stub.net.params.RpcRequest;
import io.daiwei.rpc.stub.net.params.RpcResponse;

/**
 * 服务端调用核型抽象
 * Created by Daiwei on 2021/4/14
 */
public abstract class ProviderInvokerCore {

    protected RpcSendable sendable;

    /**
     * 调用代理方法方法
     * @param clazz
     * @return
     */
    public abstract Object invoke(Class<?> clazz);

    /**
     * 有消息来了
     * @param request
     */
    public abstract void requestComingBellRing(RpcRequest request);

    public void setSendable(RpcSendable sendable) {
        this.sendable = sendable;
    }

}
