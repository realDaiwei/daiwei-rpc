package io.daiwei.rpc.stub.invoker.component;

import io.daiwei.rpc.exception.DaiweiRpcException;
import io.daiwei.rpc.register.RegisterConstant;
import io.daiwei.rpc.register.zk.ZkRpcRegister;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 注册单元
 * Created by Daiwei on 2021/4/11
 */
public class InvokerRegisterUnit extends ZkRpcRegister {

    private final Map<String, List<String>> availPathMap;

    public InvokerRegisterUnit(String zkConnStr) {
        this.zkConnStr = zkConnStr;
        this.init();
        this.start();
        availPathMap = new ConcurrentHashMap<>();
    }

    @Override
    public List<String> pullAvailableUrls(Class<?> clazz) {
        String clazzName = clazz.getCanonicalName();
        if (availPathMap.containsKey(clazzName)) {
            return availPathMap.get(clazzName);
        }
        List<String> availPath = new ArrayList<>();
        try {
            Stat stat = client.checkExists().forPath("/" + clazzName);
            if (stat != null) {
                availPath = client.getChildren().forPath("/" + clazzName);
                if (availPath != null && availPath.size() != 0) {
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



}
