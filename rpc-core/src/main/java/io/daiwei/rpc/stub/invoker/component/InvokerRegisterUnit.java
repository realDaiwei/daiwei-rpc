package io.daiwei.rpc.stub.invoker.component;

import io.daiwei.rpc.exception.DaiweiRpcException;
import io.daiwei.rpc.register.RegisterConstant;
import io.daiwei.rpc.register.zk.ZkRpcRegister;
import io.daiwei.rpc.stub.common.ConnectionManager;
import io.daiwei.rpc.stub.net.client.NettyClientServer;
import io.daiwei.rpc.stub.net.common.ConnectServer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 注册单元
 * Created by Daiwei on 2021/4/11
 */
public class InvokerRegisterUnit extends ZkRpcRegister {

    private final Map<String, List<String>> availPathMap;

    protected final ConnectionManager connectionManager;

    public InvokerRegisterUnit(String zkConnStr, ConnectionManager connectionManager) {
        this.zkConnStr = zkConnStr;
        this.availPathMap = new ConcurrentHashMap<>();
        this.connectionManager = connectionManager;
    }

    public void afterSetProperties() {
        this.init();
        this.start();
        this.registerListeners();
    }

    @Override
    public List<String> pullAvailableUrls(Class<?> clazz) {
        String clazzName = clazz.getCanonicalName();
        if (availPathMap.containsKey(clazzName)) {
            return availPathMap.get(clazzName);
        }
        List<String> availPath = new ArrayList<>();
        try {
            Stat stat = client.checkExists().forPath(File.separator + clazzName);
            if (stat != null) {
                availPath = client.getChildren().forPath(File.separator + clazzName);
                for (String s : availPath) {
                    client.getData().usingWatcher((Watcher) watchedEvent -> {
                        if (availPathMap.containsKey(clazzName)) {
                            availPathMap.get(clazzName).remove(s);
                        }
                    }).forPath(File.separator + clazzName + File.separator + s);
                }
                if (availPath.size() != 0) {
                    CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(availPath);
                    availPathMap.putIfAbsent(clazzName, list);
                } else {
                    throw new DaiweiRpcException("no available remote service found for " + clazzName +".");
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return availPath;
    }

    @Override
    public void registerListeners() {
        CuratorCacheListener listener = CuratorCacheListener.builder().forPathChildrenCache(File.separator, client, (client, event) -> {
            if (!Arrays.asList(PathChildrenCacheEvent.Type.CHILD_REMOVED, PathChildrenCacheEvent.Type.CHILD_ADDED).contains(event.getType())) {
                return;
            }
            String[] str = event.getData().getPath().split(File.separator);
            if (RegisterConstant.RPC_SERVICE.equals(new String(event.getData().getData(), StandardCharsets.UTF_8))) {
                if (!availPathMap.containsKey(str[1])) {
                    availPathMap.put(str[1], new CopyOnWriteArrayList<>());
                }
                return;
            }
            if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                availPathMap.get(str[1]).remove(str[2]);
                this.connectionManager.removeConn(str[2]);
            } else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
                List<String> urls = availPathMap.get(str[1]);
                if (!urls.contains(str[2])) {
                    urls.add(str[2]);
                }
            }
        }).build();
        registerListeners(Collections.singletonList(listener));
    }
}
