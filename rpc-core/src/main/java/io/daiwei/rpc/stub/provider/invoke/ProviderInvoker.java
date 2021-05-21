package io.daiwei.rpc.stub.provider.invoke;

import io.daiwei.rpc.exception.DaiweiRpcException;
import io.daiwei.rpc.stub.net.NetConstant;
import io.daiwei.rpc.stub.net.common.ProviderInvokerCore;
import io.daiwei.rpc.stub.net.params.RpcRequest;
import io.daiwei.rpc.stub.net.params.RpcResponse;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by Daiwei on 2021/4/14
 */
public class ProviderInvoker extends ProviderInvokerCore {

    @Override
    public Object invoke(RpcRequest req) throws Exception {
        Object proxy = RpcProviderProxyPool.getInstance().getProxy(req.getClassType());
        if (proxy == null) {
            throw new DaiweiRpcException("cannot find service["+ req.getClassType().getName() +"] proxy object.");
        }
        Class<?>[] classesType = Arrays.stream(req.getParams()).map(Object::getClass).toArray(Class<?>[]::new);
        Method method = proxy.getClass().getMethod(req.getMethodName(), classesType);
        return method.invoke(proxy, req.getParams());
    }

    @Override
    public RpcResponse requestComingBellRing(RpcRequest request) {
        RpcResponse.RpcResponseBuilder builder = RpcResponse.builder().requestId(request.getRequestId());
        try {
            Object res = invoke(request);
            builder.data(res).code(0).msg("success");
        } catch (Exception exception) {
            builder.data(null).code(-1).msg(exception.getMessage()).exception(exception);
            exception.printStackTrace();
        }
        return builder.build();
    }

    @Override
    public boolean valid(RpcRequest req) {
        return  (req.getRequestId().startsWith(NetConstant.HEART_BEAT_REQ_ID) || req.getRequestId().startsWith(NetConstant.IDLE_CHANNEL_CLOSE_REQ_ID))
                || (req.getCreateTimeMillis() + req.getTimeout() >= System.currentTimeMillis() && req.getRequestId() != null
                && req.getRequestId().length() != 0 && req.getClassName() != null && req.getClassName().length() != 0
                && req.getMethodName() != null && req.getMethodName().length() != 0 && req.getClassType() != null
                && req.getParams() != null);
    }
}
