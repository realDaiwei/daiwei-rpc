package io.daiwei.rpc.stub.net;

import io.daiwei.rpc.serializer.RpcSerializer;
import io.daiwei.rpc.stub.net.params.RpcFutureResp;
import io.daiwei.rpc.stub.net.params.RpcRequest;

/**
 * 客户端基础类
 * Created by Daiwei on 2021/3/28
 */
public abstract class Client {

    public abstract RpcFutureResp sendAsync(RpcRequest request);
}
