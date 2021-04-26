package io.daiwei.rpc.stub.net.common;

import io.daiwei.rpc.stub.net.params.RpcRequest;
import io.daiwei.rpc.stub.net.params.RpcResponse;

/**
 * 服务端调用核型抽象
 * Created by Daiwei on 2021/4/14
 */
public abstract class ProviderInvokerCore {

    /**
     * 调用代理方法方法
     * @param req
     * @return
     */
    public abstract Object invoke(RpcRequest req) throws Exception;

    /**
     * 有消息来了
     * @param req
     */
    public abstract RpcResponse requestComingBellRing(RpcRequest req);

    /**
     *
     * @param req
     * @return
     */
    public abstract boolean valid(RpcRequest req);

}
