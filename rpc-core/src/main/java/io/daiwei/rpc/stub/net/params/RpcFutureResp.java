package io.daiwei.rpc.stub.net.params;

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

    public void RespBellRing(RpcResponse resp) {
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
        try {
            get(0, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return resp;
    }

    @Override
    public RpcResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            lock.lock();
            while (!isDone()) {
               if (timeout <= 0) timeout = 0;
                boolean await = fin.await(timeout, unit);
                if (!await || isDone()) {
                   break;
               }
            }
            if (!isDone()) {
                log.error("daiwei-rpc process timeout.");
                throw new TimeoutException("invoke timeout");
            }
        } finally {
            lock.unlock();
        }
        return resp;
    }
}
