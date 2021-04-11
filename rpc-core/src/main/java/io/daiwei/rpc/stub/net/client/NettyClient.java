package io.daiwei.rpc.stub.net.client;

import io.daiwei.rpc.stub.net.Client;
import io.daiwei.rpc.stub.net.params.RpcFutureResp;
import io.daiwei.rpc.stub.net.params.RpcRequest;

/**
 * Created by Daiwei on 2021/4/11
 */
public class NettyClient extends Client {

    private NettyClientServer connectServer;

    private RpcFutureResp resp;

    public NettyClient() {

    }

    @Override
    public RpcFutureResp sendAsync(RpcRequest request) {
        connectServer.sendAsync(request);
        return resp;
    }
}
