package io.daiwei.rpc.stub.provider.component;

import io.daiwei.rpc.serializer.RpcSerializer;
import io.daiwei.rpc.serializer.impl.HessianSerializer;
import io.daiwei.rpc.stub.net.server.NettyServerHub;
import io.daiwei.rpc.stub.provider.invoke.ProviderInvoker;
import io.daiwei.rpc.util.NetUtil;

/**
 * Created by Daiwei on 2021/4/14
 */
public class ProviderServerUnit {

    private final NettyServerHub serverHub;

    public ProviderServerUnit(int port) {
        ProviderInvoker providerInvoker = new ProviderInvoker();
        HessianSerializer hessianSerializer = new HessianSerializer();
        this.serverHub = new NettyServerHub(port, providerInvoker, hessianSerializer);
    }

    public void afterSetProperties() {
        this.serverHub.start();
    }

    public void stop() {
        this.serverHub.stop();
    }

    public boolean isActive() {
        return this.serverHub.isActive();
    }
}
