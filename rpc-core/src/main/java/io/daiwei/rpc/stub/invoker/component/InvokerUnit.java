package io.daiwei.rpc.stub.invoker.component;

import io.daiwei.rpc.serializer.RpcSerializer;
import io.daiwei.rpc.stub.common.RpcCallback;
import io.daiwei.rpc.stub.net.Client;
import io.daiwei.rpc.stub.net.common.InvokerClientCore;

import java.util.ArrayList;
import java.util.List;

/**
 * 调用单元
 * Created by Daiwei on 2021/4/11
 */
public class InvokerUnit {

    private final List<RpcCallback> stopCallbackList = new ArrayList<>();

    private final Class<? extends RpcSerializer> serializerClazz;

    private final Class<? extends InvokerClientCore> clientClazz;

    private InvokerClientCore clientCore;

    public InvokerUnit(Class<? extends RpcSerializer> serializerClazz, Class<? extends InvokerClientCore> clientClazz) {
        this.serializerClazz = serializerClazz;
        this.clientClazz = clientClazz;
    }

    public void addStopCallBack(RpcCallback callback) {
        this.stopCallbackList.add(callback);
    }

    public void stop() throws Exception{
        for (RpcCallback rpcCallback : this.stopCallbackList) {
            rpcCallback.run();
        }
        clientCore.stopClientServer();
    }

    public void start() throws Exception {
        this.clientCore = clientClazz.newInstance();
        RpcSerializer serializer = serializerClazz.newInstance();
        this.clientCore.setSerializer(serializer);
    }

    public Client getInvokeClient(String addr) {
        return this.clientCore.getClient(addr);
    }

}
