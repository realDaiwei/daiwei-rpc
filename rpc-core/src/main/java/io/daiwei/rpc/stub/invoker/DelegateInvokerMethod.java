package io.daiwei.rpc.stub.invoker;

import io.daiwei.rpc.serializer.RpcSerializer;
import io.daiwei.rpc.stub.net.Client;
import io.daiwei.rpc.stub.net.params.RpcFutureResp;
import io.daiwei.rpc.stub.net.params.RpcRequest;
import io.daiwei.rpc.stub.net.params.RpcResponse;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by Daiwei on 2021/3/31
 */
public class DelegateInvokerMethod {

    private static final AtomicInteger id = new AtomicInteger();

    private final RpcSerializer serializer;

    private final Client client;

    public DelegateInvokerMethod(RpcSerializer serializer, Client client) {
        this.client = client;
        this.serializer = serializer;
    }

    @RuntimeType
    public Object interceptor(@This Object target, @AllArguments Object[] args, @Origin Method method, @Super Object clazz) {
        Class<?> iface = target.getClass().getInterfaces()[0];
        RpcRequest request = RpcRequest.builder().requestId(String.valueOf(id.getAndIncrement()))
                .methodName(method.getName()).classTypes(iface).createTimeMillis(System.currentTimeMillis())
                .params(args).className(iface.getCanonicalName()).build();
        RpcFutureResp rpcFutureResp = client.sendAsync(request, this.serializer);
        try {
            RpcResponse rpcResponse = rpcFutureResp.get();
            return method.getReturnType().cast(rpcResponse.getData());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
