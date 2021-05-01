package io.daiwei.rpc.stub.invoker.component;

import io.daiwei.rpc.exception.DaiweiRpcException;
import io.daiwei.rpc.register.RegisterConstant;
import io.daiwei.rpc.register.zk.ZkRpcRegister;
import io.daiwei.rpc.stub.common.ConnectionManager;
import io.daiwei.rpc.stub.net.NetConstant;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.zookeeper.data.Stat;

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

    private final Map<String, String> zkData;

    protected final ConnectionManager connectionManager;

    public InvokerRegisterUnit(String zkConnStr, ConnectionManager connectionManager) {
        this.zkConnStr = zkConnStr;
        this.availPathMap = new ConcurrentHashMap<>();
        this.zkData = new ConcurrentHashMap<>();
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
        List<String> availPath = new CopyOnWriteArrayList<>();
        try {
            Stat stat = client.checkExists().forPath(NetConstant.FILE_SEPARATOR + clazzName);
            if (stat != null) {
                availPath = new CopyOnWriteArrayList<>(client.getChildren().forPath(NetConstant.FILE_SEPARATOR + clazzName));
                if (!availPath.isEmpty()) {
                    availPathMap.putIfAbsent(clazzName, availPath);
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
        CuratorCacheListener listener = CuratorCacheListener.builder().forPathChildrenCache(NetConstant.FILE_SEPARATOR, client, (client, event) -> {
            if (!Arrays.asList(PathChildrenCacheEvent.Type.CHILD_REMOVED, PathChildrenCacheEvent.Type.CHILD_ADDED).contains(event.getType())) {
                return;
            }
            String[] str = event.getData().getPath().split(NetConstant.FILE_SEPARATOR);
            String data = new String(event.getData().getData(), StandardCharsets.UTF_8);
            if (RegisterConstant.RPC_SERVICE.equals(data)) {
                if (!availPathMap.containsKey(str[1])) {
                    availPathMap.put(str[1], new CopyOnWriteArrayList<>());
                }
                return;
            }
            if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                availPathMap.get(str[1]).remove(str[2]);
                this.connectionManager.removeConn(str[2]);
                zkData.remove(str[2]);
            } else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
                zkData.put(str[2], data);
                List<String> urls = availPathMap.get(str[1]);
                if (!urls.contains(str[2])) {
                    urls.add(str[2]);
                }
            }
        }).build();
        registerListeners(Collections.singletonList(listener));
    }

    public Map<String, String> getZkData() {
        return this.zkData;
    }

}
