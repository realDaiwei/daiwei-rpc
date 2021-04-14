package io.daiwei.rpc.stub.net.server;

import io.daiwei.rpc.stub.net.Server;
import io.daiwei.rpc.stub.net.common.ProviderInvokerCore;
import io.daiwei.rpc.stub.net.params.RpcResponse;

/**
 * Created by Daiwei on 2021/4/13
 */
public class NettyServerHub implements Server {

    private final NettyServer nettyServer;

    private final ProviderInvokerCore invokerCore;

    public NettyServerHub(NettyServer server, ProviderInvokerCore invokerCore) {
        this.nettyServer = server;
        this.invokerCore = invokerCore;
        invokerCore.setSendable(server);
    }

    @Override
    public void start() {
        if (this.nettyServer.isValid() && this.invokerCore != null) {
            this.nettyServer.run(this.invokerCore);
        }
    }

    @Override
    public void stop() {
        this.nettyServer.close();
    }

    @Override
    public void sendAsync(RpcResponse response) {
        nettyServer.sendAsync(response);
    }
}
