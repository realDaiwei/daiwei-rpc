package io.daiwei.rpc.stub.net.server;

import io.daiwei.rpc.serializer.RpcSerializer;
import io.daiwei.rpc.serializer.impl.HessianSerializer;
import io.daiwei.rpc.stub.net.Server;
import io.daiwei.rpc.stub.net.common.ProviderInvokerCore;
import io.daiwei.rpc.stub.net.params.RpcResponse;
import io.daiwei.rpc.stub.provider.invoke.RpcProviderProxyPool;
import io.daiwei.rpc.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Daiwei on 2021/4/13
 */
@Slf4j
public class NettyServerHub implements Server {

    private final NettyServer nettyServer;

    private final ProviderInvokerCore invokerCore;

    public NettyServerHub(int port, ProviderInvokerCore invokerCore, RpcSerializer serializer) {
        this.nettyServer = new NettyServer(port, serializer);
        this.invokerCore = invokerCore;
    }

    @Override
    public void start() {
        if (!this.nettyServer.isValid() && this.invokerCore != null) {
            try {
                new Thread(() -> {
                    this.nettyServer.run(this.invokerCore);
                }, "daiwei-rpc-server-thread").start();
            } catch (Exception e) {
                log.error("error!", e);
            }
        }
    }

    @Override
    public void stop() {
        this.nettyServer.close();
    }

    @Override
    public boolean isActive() {
        return this.nettyServer.isValid();
    }

    @Override
    public void sendAsync() {}
}
