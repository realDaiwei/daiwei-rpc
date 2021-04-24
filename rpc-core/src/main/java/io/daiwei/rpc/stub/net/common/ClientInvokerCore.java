package io.daiwei.rpc.stub.net.common;

import io.daiwei.rpc.serializer.RpcSerializer;
import io.daiwei.rpc.stub.common.ConnectionManager;
import io.daiwei.rpc.stub.net.Client;
import io.daiwei.rpc.stub.net.params.RpcFutureResp;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Daiwei on 2021/4/11
 */
@Slf4j
public abstract class ClientInvokerCore implements ConnectionManager {

    protected final Map<String, ConnectServer> clientServers = new ConcurrentHashMap<>();

    protected final List<String> subHealthList = new CopyOnWriteArrayList<>();

    protected final Map<String, RpcFutureResp> respPool = new ConcurrentHashMap<>();

    protected final Map<String, Object> lockMap = new HashMap<>();

    protected RpcSerializer serializer;

    public abstract Client getClient(String addr);

    public void setSerializer(RpcSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void removeConn(String conn) {
        if (this.clientServers.containsKey(conn)) {
            this.clientServers.get(conn).close();
        }
        this.clientServers.remove(conn);
        this.subHealthList.remove(conn);
    }

    public List<String> removeSubHealthUrl(List<String> urls) {
        List<String> availableUrls = new ArrayList<>(urls);
        availableUrls.removeAll(this.subHealthList);
        return availableUrls;
    }

    public void addUrlToSubHealth(String url) {
        this.subHealthList.add(url);
    }

    public void stopClientServer() {
        clientServers.values().forEach(connectServer -> {
            connectServer.close();
            log.info("connectServer[{}] close successfully", connectServer);
        });
        clientServers.values().stream().findFirst().ifPresent(ConnectServer::cleanStaticResource);
    }

    public void removeTimeoutRespFromPool(String reqId) {
        this.respPool.remove(reqId);
    }

}
