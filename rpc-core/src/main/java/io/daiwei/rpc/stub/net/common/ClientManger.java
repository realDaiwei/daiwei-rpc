package io.daiwei.rpc.stub.net.common;

import io.daiwei.rpc.serializer.RpcSerializer;
import io.daiwei.rpc.stub.net.Client;
import io.daiwei.rpc.stub.net.params.RpcResponse;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daiwei on 2021/4/11
 */
public abstract class ClientManger {

    protected volatile Map<String, ConnectServer> clientServers;

    protected volatile Map<String, RpcResponse> respPool;

    protected final Map<String, Object> lockMap = new HashMap<>();

    protected RpcSerializer serializer;

    public abstract Client getClient(String addr);

    public void setSerializer(RpcSerializer serializer) {
        this.serializer = serializer;
    }

    public void stopClientServer() {
        if (this.clientServers != null) {
            clientServers.values().forEach(ConnectServer::close);
        }
    }
}
