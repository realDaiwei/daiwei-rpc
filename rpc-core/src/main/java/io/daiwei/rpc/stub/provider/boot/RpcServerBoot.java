package io.daiwei.rpc.stub.provider.boot;

import io.daiwei.rpc.exception.DaiweiRpcException;
import io.daiwei.rpc.stub.provider.component.ProviderRegisterUnit;
import io.daiwei.rpc.stub.provider.component.ProviderServerUnit;
import io.daiwei.rpc.util.NetUtil;

/**
 *  server 启动boot
 * Created by Daiwei on 2021/4/15
 */
public class RpcServerBoot {

    private final ProviderRegisterUnit registerUnit;

    private final ProviderServerUnit serverStubUnit;

    private final int availablePort;

    private RpcServerBoot(int port, String zkConnStr) {
        this.availablePort = port;
        this.registerUnit = new ProviderRegisterUnit(zkConnStr);
        this.serverStubUnit = new ProviderServerUnit(port);
    }

    private RpcServerBoot(String zkConnStr) {
        int defaultPort = 7248;
        this.availablePort = NetUtil.findAvailablePort(defaultPort);
        this.registerUnit = new ProviderRegisterUnit(zkConnStr);
        this.serverStubUnit = new ProviderServerUnit(this.availablePort);
    }

    public void run() {
        serverStubUnit.afterSetProperties();
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public static ServerBuilder builder() {
        return new ServerBuilder();
    }

    private void stop() {
        this.registerUnit.stop();
        this.serverStubUnit.stop();
    }

    public static class ServerBuilder {

        private RpcServerBoot rpcServerBoot;

        private ServerBuilder() {}

        public ServerBuilder init(int serverPort, String zkConnStr) {
            if (this.rpcServerBoot != null) {
                throw new DaiweiRpcException("server already initialized!");
            }
            rpcServerBoot = new RpcServerBoot(serverPort, zkConnStr);
            return this;
        }

        public ServerBuilder init(String zkConnStr) {
            if (this.rpcServerBoot != null) {
                throw new DaiweiRpcException("server already initialized!");
            }
            rpcServerBoot = new RpcServerBoot(zkConnStr);
            return this;
        }

        public ServerBuilder registerService(Class<?> clazz) {
            try {
                this.rpcServerBoot.registerUnit.registerInvokeProxy(clazz);
                this.rpcServerBoot.registerUnit.registerService(this.rpcServerBoot.availablePort, clazz);
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
