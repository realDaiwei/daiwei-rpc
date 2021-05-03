package io.daiwei.rpc.stub.provider.boot;

import io.daiwei.rpc.exception.DaiweiRpcException;
import io.daiwei.rpc.stub.provider.component.ProviderRegisterUnit;
import io.daiwei.rpc.stub.provider.component.ProviderServerUnit;
import io.daiwei.rpc.util.NetUtil;
import io.daiwei.rpc.util.ThreadPoolUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *  server 启动boot
 * Created by Daiwei on 2021/4/15
 */
public class RpcServerBoot {

    private final ProviderRegisterUnit registerUnit;

    private final ProviderServerUnit serverStubUnit;

    private final int availablePort;

    private final List<RegisterService> clazzList;

    private RpcServerBoot(int port, String zkConnStr) {
        this.availablePort = port;
        this.clazzList = new ArrayList<>();
        this.registerUnit = new ProviderRegisterUnit(zkConnStr, this.availablePort);
        this.serverStubUnit = new ProviderServerUnit(port);
    }

    private RpcServerBoot(String zkConnStr) {
        int defaultPort = 7248;
        this.availablePort = NetUtil.findAvailablePort(defaultPort);
        this.clazzList = new ArrayList<>();
        this.registerUnit = new ProviderRegisterUnit(zkConnStr, this.availablePort);
        this.serverStubUnit = new ProviderServerUnit(this.availablePort);
    }

    public void run() {
        start();
    }

    public void runAsync() {
        ThreadPoolUtil.defaultRpcProviderExecutor().execute(this::start);
    }

    public static ServerBuilder builder() {
        return new ServerBuilder();
    }

    public void stop() {
        this.registerUnit.stop();
        this.serverStubUnit.stop();
    }

    private void start() {
        serverStubUnit.afterSetProperties();
        try {
            for (RegisterService service : clazzList) {
                registerUnit.registerInvokeProxy(service.getClazz());
                registerUnit.registerService(this.availablePort, service.getClazz(), service.getVersion());
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
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

        public ServerBuilder registerService(Class<?> clazz, String version) {
            this.rpcServerBoot.clazzList.add(new RegisterService(clazz, version));
            return this;
        }

        public RpcServerBoot build() {
            return this.rpcServerBoot;
        }
    }

    private static class RegisterService {

        private final Class<?> clazz;

        private final String version;

        public RegisterService(Class<?> clazz, String version) {
            this.clazz = clazz;
            this.version = version;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public String getVersion() {
            return version;
        }
    }
}
