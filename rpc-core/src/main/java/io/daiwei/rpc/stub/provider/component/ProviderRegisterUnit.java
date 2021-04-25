package io.daiwei.rpc.stub.provider.component;

import io.daiwei.rpc.exception.DaiweiRpcException;
import io.daiwei.rpc.register.RegisterConstant;
import io.daiwei.rpc.register.zk.ZkRpcRegister;
import io.daiwei.rpc.stub.net.NetConstant;
import io.daiwei.rpc.stub.provider.invoke.RpcProviderProxyPool;
import io.daiwei.rpc.util.NetUtil;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import sun.nio.ch.Net;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * Created by Daiwei on 2021/4/14
 */
@Slf4j
public class ProviderRegisterUnit extends ZkRpcRegister {

    private final int port;

    public ProviderRegisterUnit(String zkConnStr, int port) {
        this.zkConnStr = zkConnStr;
        this.port = port;
        this.init();
        this.start();
        this.registerListeners();
    }

    /**
     * 注册被调用的 proxy 到 proxyPool 中
     * @param clazz
     */
    @Override
    public void registerInvokeProxy(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        serverCheck(clazz);
        Class<?> clazzInterface = clazz.getInterfaces()[0];
        String clazzName = clazz.getCanonicalName() + "$$daiweiRpcProxyByByteBuddy";
        Object proxy = new ByteBuddy().subclass(clazz).name(clazzName)
                .method(ElementMatchers.any()).intercept(MethodCall.invokeSuper().withAllArguments())
                .make().load(ProviderRegisterUnit.class.getClassLoader()).getLoaded().newInstance();
        RpcProviderProxyPool.getInstance().addProxy(clazzInterface, proxy);
    }

    @Override
    public void registerService(int port, Class<?> clazz) {
        try {
            serverCheck(clazz);
            Class<?> service = clazz.getInterfaces()[0];
            if (null == client.checkExists().forPath(NetConstant.FILE_SEPARATOR + service.getCanonicalName())) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(NetConstant.FILE_SEPARATOR + service.getCanonicalName(), RegisterConstant.RPC_SERVICE.getBytes(StandardCharsets.UTF_8));
            }
            String addr = NetUtil.getIpAddress().concat(":").concat(String.valueOf(port));
            client.create().withMode(CreateMode.EPHEMERAL).forPath(NetConstant.FILE_SEPARATOR + service.getCanonicalName() + NetConstant.FILE_SEPARATOR + addr, RegisterConstant.RPC_PROVIDER.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            log.error("create zk node failed.", exception);
            exception.printStackTrace();
            throw new DaiweiRpcException("create zk node failed.");
        }
    }

    /**
     * 注册服务端的 zk监听器
     */
    @Override
    public void registerListeners() {
        CuratorCacheListener listener = CuratorCacheListener.builder().forPathChildrenCache(NetConstant.FILE_SEPARATOR, client, new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED
                        && client.getState() != CuratorFrameworkState.STOPPED && isSelfRemoved(event.getData().getPath())) {
                    client.create().withMode(CreateMode.EPHEMERAL).forPath(event.getData().getPath(), "provider".getBytes(StandardCharsets.UTF_8));
                }
            }
        }).build();
        registerListeners(Collections.singletonList(listener));
    }


    private void serverCheck(Class<?> clazz) {
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces.length == 0) {
            throw new DaiweiRpcException("not find interface of  " + clazz.getCanonicalName());
        }
        if (interfaces.length != 1) {
            throw new DaiweiRpcException("find more than one interfaces of " + clazz.getCanonicalName());
        }
    }

    private boolean isSelfRemoved(String zkDataPath) {
        String ipAndPort = zkDataPath.substring(zkDataPath.lastIndexOf(NetConstant.FILE_SEPARATOR));
        return NetConstant.FILE_SEPARATOR.concat(NetUtil.getIpAddress()).concat(":").concat(String.valueOf(port)).equals(ipAndPort);
    }
}
