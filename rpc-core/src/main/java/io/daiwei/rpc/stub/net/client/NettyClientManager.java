package io.daiwei.rpc.stub.net.client;

import io.daiwei.rpc.stub.net.Client;
import io.daiwei.rpc.stub.net.common.ClientManger;
import io.daiwei.rpc.stub.net.common.ConnectServer;
import io.daiwei.rpc.stub.net.params.RpcFutureResp;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Daiwei on 2021/4/10
 */
public class NettyClientManager extends ClientManger {

    private final Lock lock = new ReentrantLock();

    public NettyClientManager() {
        this.clientServers = new ConcurrentHashMap<>();
    }

    @Override
    public Client getClient(String addr) {
        initClientServerIfAbsent(addr);
        ConnectServer connectServer = this.clientServers.get(addr);
        RpcFutureResp resp = new RpcFutureResp();
        // 注册到回掉池
        return null;
    }

    private void initClientServerIfAbsent(String addr) {
        if (this.clientServers.containsKey(addr)) {
            return;
        }
        setIfLockAbsent(addr);
        Object lock = this.lockMap.get(addr);
        synchronized (lock) {
            NettyClientServer clientServer = new NettyClientServer(this.serializer);
            // 初始化一个client 客户端
            clientServer.init(addr);
            this.clientServers.put(addr, clientServer);
        }
    }

    private void setIfLockAbsent(String addr) {
        if (this.lockMap.containsKey(addr)) {
            return;
        }
        try {
            lock.lock();
            if (!this.lockMap.containsKey(addr)) {
                this.lockMap.put(addr, new Object());
            }
        } finally {
            lock.unlock();
        }

    }

}
