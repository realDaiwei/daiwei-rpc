package io.daiwei.rpc.stub.provider.boot;

import io.daiwei.rpc.exception.DaiweiRpcException;
import io.daiwei.rpc.stub.provider.component.ProviderRegisterUnit;
import io.daiwei.rpc.stub.provider.component.ProviderServerUnit;

/**
 *  server 启动boot
 * Created by Daiwei on 2021/4/15
 */
public class RpcServerBoot {

    private final ProviderRegisterUnit registerUnit;

    private final ProviderServerUnit serverStubUnit;

    private RpcServerBoot(int port) {
        this.registerUnit = new ProviderRegisterUnit();
        this.serverStubUnit = new ProviderServerUnit(port);
    }

    private RpcServerBoot() {
        int defaultPort = 7248;
        this.registerUnit = new ProviderRegisterUnit();
        this.serverStubUnit = new ProviderServerUnit(defaultPort);
    }

    public void run() {
        serverStubUnit.afterSetProperties();
    }

    public static ServerBuilder builder() {
        return new ServerBuilder();
    }

    public static class ServerBuilder {

        private RpcServerBoot rpcServerBoot;

        private ServerBuilder() {}

        public ServerBuilder init(int port) {
            if (this.rpcServerBoot != null) {
                throw new DaiweiRpcException("server already initialized!");
            }
            rpcServerBoot = new RpcServerBoot(port);
            return this;
        }

        public ServerBuilder init() {
            if (this.rpcServerBoot != null) {
                throw new DaiweiRpcException("server already initialized!");
            }
            rpcServerBoot = new RpcServerBoot();
            return this;
        }

        public ServerBuilder registerService(Class<?> clazz) {
            if (this.rpcServerBoot.registerUnit == null) {
                throw new DaiweiRpcException("init server first!");
            }
            try {
                this.rpcServerBoot.registerUnit.registerInvokeProxy(clazz);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return this;
        }

        public RpcServerBoot build() {
            return this.rpcServerBoot;
        }

    }



}
