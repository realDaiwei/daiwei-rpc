package io.daiwei.rpc.stub.net.params;

import io.daiwei.rpc.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Daiwei on 2021/4/10
 */
@Slf4j
public class RpcFutureResp implements Future<RpcResponse> {

    private RpcResponse resp;

    private final Lock lock = new ReentrantLock();

    private final Condition fin = lock.newCondition();

    public void RespBackBellRing(RpcResponse resp) {
        this.resp = resp;
        try {
            lock.lock();
            fin.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return resp != null;
    }

    @Override
    public RpcResponse get() throws InterruptedException, ExecutionException {
        get(-1, TimeUnit.MILLISECONDS);
        return resp;
    }

    @Override
    public RpcResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
        try {
            lock.lock();
            while (!isDone()) {
               if (timeout <= 0) {
                   fin.await();
               } else {
                   boolean await = fin.await(timeout, unit);
                   if (!await || isDone()) {
                       break;
                   }
               }
            }
            if (!isDone()) {
                log.error("daiwei-rpc invoke timeout.");
                resp = RpcResponse.builder().exception(new TimeoutException("inoke timeout")).build();
            }
        } finally {
            lock.unlock();
        }
        return resp;
    }
}
