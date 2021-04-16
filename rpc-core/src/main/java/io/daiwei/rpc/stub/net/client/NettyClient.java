package io.daiwei.rpc.stub.net.client;

import io.daiwei.rpc.stub.net.Client;
import io.daiwei.rpc.stub.net.common.ConnectServer;
import io.daiwei.rpc.stub.net.params.RpcFutureResp;
import io.daiwei.rpc.stub.net.params.RpcRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Daiwei on 2021/4/11
 */
public class NettyClient implements Client {

    private final ConnectServer connectServer;

    private final Map<String, RpcFutureResp> respPool;


    public NettyClient(ConnectServer connectServer, Map<String, RpcFutureResp> respPool) {
        this.connectServer = connectServer;
        this.respPool = respPool;
    }

    @Override
    public RpcFutureResp sendAsync(RpcRequest request) {
        // TODO: 2021/4/13 做一个 failover
        RpcFutureResp resp = new RpcFutureResp();
        this.respPool.put(request.getRequestId(), resp);
        connectServer.sendAsync(request);
        return resp;
    }

    @Override
    public void cleanAfterInvoke(RpcRequest request) {
        this.respPool.remove(request.getRequestId());
    }
}
