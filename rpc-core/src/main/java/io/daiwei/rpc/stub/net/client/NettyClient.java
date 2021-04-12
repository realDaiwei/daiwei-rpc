package io.daiwei.rpc.stub.net.client;

import io.daiwei.rpc.stub.net.Client;
import io.daiwei.rpc.stub.net.common.ConnectServer;
import io.daiwei.rpc.stub.net.params.RpcFutureResp;
import io.daiwei.rpc.stub.net.params.RpcRequest;
import io.daiwei.rpc.stub.net.params.RpcResponse;

import java.util.Map;

/**
 * Created by Daiwei on 2021/4/11
 */
public class NettyClient extends Client {

    private final ConnectServer connectServer;

    private final Map<String, RpcFutureResp> respPool;

    public NettyClient(ConnectServer connectServer, Map<String, RpcFutureResp> respPool) {
        this.connectServer = connectServer;
        this.respPool = respPool;
    }

    @Override
    public RpcFutureResp sendAsync(RpcRequest request) {
        RpcFutureResp resp = new RpcFutureResp();
        respPool.put(request.getRequestId(), resp);
        connectServer.sendAsync(request);
        return resp;
    }

    @Override
    public void cleanAfterInvoke(RpcRequest request) {
        respPool.remove(request.getRequestId());
    }
}
