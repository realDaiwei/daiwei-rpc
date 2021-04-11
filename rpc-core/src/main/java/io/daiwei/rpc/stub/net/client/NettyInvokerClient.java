package io.daiwei.rpc.stub.net.client;

import io.daiwei.rpc.stub.net.Client;
import io.daiwei.rpc.stub.net.common.InvokerClientCore;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Daiwei on 2021/4/10
 */
public class NettyInvokerClient extends InvokerClientCore {

    private final Lock lock = new ReentrantLock();

    @Override
    public Client getClient(String addr) {
        initClientServerIfAbsent(addr);
        return new NettyClient(this.clientServers.get(addr), respPool);
    }

    private void initClientServerIfAbsent(String addr) {
        if (this.clientServers.containsKey(addr)) {
            return;
        }
        setIfLockAbsent(addr);
        synchronized (this.lockMap.get(addr)) {
            if (this.clientServers.containsKey(addr)) {
                return;
            }
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
