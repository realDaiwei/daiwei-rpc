package io.daiwei.rpc.stub.invoker.factory;

import io.daiwei.rpc.exception.DaiweiRpcException;
import io.daiwei.rpc.router.common.LoadBalance;
import io.daiwei.rpc.stub.invoker.component.InvokerUnit;
import io.daiwei.rpc.stub.invoker.refbean.RpcRefBean;
import io.daiwei.rpc.stub.net.Client;
import io.daiwei.rpc.stub.net.params.RpcFutureResp;
import io.daiwei.rpc.stub.net.params.RpcRequest;
import io.daiwei.rpc.stub.net.params.RpcResponse;
import net.bytebuddy.implementation.bind.annotation.*;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Daiwei on 2021/3/31
 */
public class DelegateInvokerMethod {

    private final InvokerUnit invokerUnit;

    private final LoadBalance loadBalance;

    private final List<String> urls;

    private final long timeout;

    public DelegateInvokerMethod(RpcRefBean refBean, LoadBalance loadBalance, InvokerUnit invokerUnit) {
        this.invokerUnit = invokerUnit;
        this.loadBalance = loadBalance;
        this.urls = refBean.getAvailUrls();
        this.timeout = refBean.getTimeout();
    }

    @RuntimeType
    public Object interceptor(@This Object target, @AllArguments Object[] args, @Origin Method method) {
        Class<?> iface = target.getClass().getInterfaces()[0];
        String requestId = UUID.randomUUID().toString().replace("-", "");
        RpcRequest request = RpcRequest.builder().requestId(requestId)
                .methodName(method.getName()).classType(iface).createTimeMillis(System.currentTimeMillis())
                .params(args).className(iface.getCanonicalName()).timeout(timeout).build();
        List<String> healthUrls = invokerUnit.filterSubHealth(this.urls);
        Client client = invokerUnit.getInvokeClient(loadBalance.select(healthUrls));
        RpcFutureResp rpcFutureResp = client.sendAsync(request);
        try {
            RpcResponse rpcResponse = rpcFutureResp.get();
            if (rpcResponse.getException() != null) {
                throw new ExecutionException(request.getClassName() + "invoke failed", rpcResponse.getException());
            }
            if (!void.class.equals(method.getReturnType()) && rpcResponse.getData() != null) {
                return method.getReturnType().cast(rpcResponse.getData());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            client.cleanAfterInvoke(request);
        }
        return null;
    }
}
