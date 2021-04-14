package io.daiwei.rpc.util;

import io.daiwei.rpc.exception.DaiweiRpcException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Daiwei on 2021/4/10
 */
public class ThreadPoolUtil {

    private static final int CORE_SIZE = 4;

    private static final int MAX_SIZE = Runtime.getRuntime().availableProcessors();

    private static final long KEEP_ALIVE_TIME = 60;

    private static final Integer QUEUE_CAPACITY = 4096;

    private volatile static ExecutorService DEFAULT_CLIENT_EXECUTOR;

    private volatile static ExecutorService DEFAULT_SERVER_EXECUTOR;

    private static final List<ExecutorService> EXECUTOR_POOLS = new CopyOnWriteArrayList<>();

    private ThreadPoolUtil() {}

    public static ExecutorService createPool(String poolName) {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(CORE_SIZE, MAX_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY), r -> new Thread(r, poolName + "-worker"), (r, executor) -> {
            throw new DaiweiRpcException("thread pool[" + poolName + "] is exhausted.");
        });
        EXECUTOR_POOLS.add(poolExecutor);
        return poolExecutor;
    }

    public static ExecutorService defaultRpcClientExecutor() {
        if (DEFAULT_CLIENT_EXECUTOR == null) {
            synchronized (ThreadPoolUtil.class) {
                if (DEFAULT_CLIENT_EXECUTOR == null) {
                    DEFAULT_CLIENT_EXECUTOR = createPool("rpc-default-client-pool");
                    return DEFAULT_CLIENT_EXECUTOR;
                }
            }
        }
        return DEFAULT_CLIENT_EXECUTOR;
    }

    public static ExecutorService defaultRpcProviderExecutor() {
        if (DEFAULT_SERVER_EXECUTOR == null) {
            synchronized (ThreadPoolUtil.class) {
                if (DEFAULT_SERVER_EXECUTOR == null) {
                    DEFAULT_SERVER_EXECUTOR = createPool("rpc-default-server-pool");
                    return DEFAULT_SERVER_EXECUTOR;
                }
            }
        }
        return DEFAULT_SERVER_EXECUTOR;
    }

    public static void shutdownExistsPools() {
        EXECUTOR_POOLS.forEach(ExecutorService::shutdownNow);
    }
}
