package io.daiwei.rpc.stub.invoker.factory;

import io.daiwei.rpc.router.common.Filter;
import io.daiwei.rpc.router.common.LoadBalance;
import io.daiwei.rpc.router.common.Router;
import io.daiwei.rpc.spi.RpcSpiPluginLoader;
import io.daiwei.rpc.stub.invoker.component.InvokerUnit;
import io.daiwei.rpc.stub.invoker.refbean.RpcRefBean;
import io.daiwei.rpc.stub.net.Client;
import io.daiwei.rpc.stub.net.NetConstant;
import io.daiwei.rpc.stub.net.params.RpcFutureResp;
import io.daiwei.rpc.stub.net.params.RpcRequest;
import io.daiwei.rpc.stub.net.params.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Daiwei on 2021/3/31
 */
@Slf4j
public class DelegateInvokerMethod {

    private final InvokerUnit invokerUnit;

    private final LoadBalance loadBalance;

    private final Class<?> routerClass;

    private List<String> urls;

    private final long timeout;

    private final Integer retryTimes;

    private final List<Class<?>> retryException;

    public DelegateInvokerMethod(RpcRefBean refBean, LoadBalance loadBalance, InvokerUnit invokerUnit) {
        this.invokerUnit = invokerUnit;
        this.loadBalance = loadBalance;
        this.routerClass = refBean.getRouterClass();
        this.urls = refBean.getAvailUrls();
        this.timeout = refBean.getTimeout();
        this.retryTimes = refBean.getRetryTimes();
        this.retryException = refBean.getRetryExceptions();
        this.retryException.addAll(NetConstant.RPC_NEED_RETRY_EXS);
    }

    @RuntimeType
    public Object interceptor(@This Object target, @AllArguments Object[] args, @Origin Method method) {
        Class<?> iface = target.getClass().getInterfaces()[0];
        RpcRequest request = RpcRequest.builder().methodName(method.getName()).classType(iface)
                .params(args).className(iface.getCanonicalName()).timeout(this.timeout).build();
        this.urls = filterAndRoute(this.urls, routerClass, method, args);
        List<String> healthUrls = invokerUnit.filterSubHealth(this.urls);
        RpcResponse rpcResponse = null;
        Client client = null;
        int retryTimes = 0;
        try {
            while (rpcResponse == null || rpcResponse.getException() != null) {
                String url = loadBalance.select(healthUrls, this.urls);
                client = invokerUnit.getInvokeClient(url);
                String requestId = UUID.randomUUID().toString().replace("-", "");
                request.setRequestId(requestId);
                request.setCreateTimeMillis(System.currentTimeMillis());
                RpcFutureResp rpcFutureResp = client.send(request);
                rpcResponse = rpcFutureResp.get(request.getTimeout(), TimeUnit.MILLISECONDS);
                if (rpcResponse.getException() == null || retryTimes++ >= this.retryTimes
                        || !this.retryException.contains(rpcResponse.getException().getClass())) {
                    if (retryTimes > 0) {
                        log.debug("[daiwei-rpc] rpc auto failover failed invoke");
                    }
                    if (retryTimes < this.retryTimes) {
                        invokerUnit.getClientCore().invokeSuccess(url);
                    }
                    break;
                }
                cleanAfterInvokeFailed(requestId, url, healthUrls);
            }
            if (rpcResponse.getException() != null) {
                throw new ExecutionException(request.getClassName() + " invoke failed", rpcResponse.getException());
            }
            if (!void.class.equals(method.getReturnType()) && rpcResponse.getData() != null) {
                return method.getReturnType().cast(rpcResponse.getData());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.cleanAfterInvoke(request);
            }
        }
        return null;
    }


    private void cleanAfterInvokeFailed(String requestId, String url, List<String> healthUrls) {
        invokerUnit.getClientCore().removeTimeoutRespFromPool(requestId);
        invokerUnit.getClientCore().invokeFailed(url);
        healthUrls.remove(url);
    }

    private List<String> filterAndRoute(List<String> urls, Class<?> clazz, Method method, Object[] args) {
        List<Filter> filterList = RpcSpiPluginLoader.getFilterList();
        List<String> res = new ArrayList<>();
        if (!filterList.isEmpty()) {
            for (Filter filter : filterList) {
                for (String url : urls) {
                    if (filter.filter(url, method, args)) {
                        res.add(url);
                    }
                }
            }
        } else {
            res = urls;
        }
        Router router = RpcSpiPluginLoader.getRouterByClass(clazz);
        if (router != null) {
            res = router.route(res, method, args);
        }
        return res;
    }
}
