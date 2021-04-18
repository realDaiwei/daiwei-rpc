package io.daiwei.rpc.stub.net.common;

import io.daiwei.rpc.serializer.RpcSerializer;
import io.daiwei.rpc.stub.net.Client;
import io.daiwei.rpc.stub.net.client.NettyClient;
import io.daiwei.rpc.stub.net.client.NettyClientServer;
import io.daiwei.rpc.stub.net.client.NettyInvokerClient;
import io.daiwei.rpc.stub.net.params.RpcFutureResp;
import io.daiwei.rpc.stub.net.params.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Daiwei on 2021/4/11
 */
@Slf4j
public abstract class ClientInvokerCore {

    protected final Map<String, ConnectServer> clientServers = new ConcurrentHashMap<>();

    protected final Map<String, RpcFutureResp> respPool = new ConcurrentHashMap<>();

    protected final Map<String, Object> lockMap = new HashMap<>();

    protected RpcSerializer serializer;

    public abstract Client getClient(String addr);

    public void setSerializer(RpcSerializer serializer) {
        this.serializer = serializer;
    }

    public void stopClientServer() {
        clientServers.values().forEach(connectServer -> {
            connectServer.close();
            log.info("connectServer[{}] close successfully", connectServer);
        });
        clientServers.values().stream().findFirst().ifPresent(ConnectServer::cleanStaticResource);

    }
}
