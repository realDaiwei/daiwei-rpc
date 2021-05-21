package io.daiwei.rpc.stub.invoker.component;

import io.daiwei.rpc.serializer.RpcSerializer;
import io.daiwei.rpc.stub.net.Client;
import io.daiwei.rpc.stub.net.common.ClientInvokerCore;
import io.daiwei.rpc.stub.net.common.ConnectServer;

import java.util.List;
import java.util.Map;

/**
 * 调用单元
 * Created by Daiwei on 2021/4/11
 */
public class InvokerUnit {

    private final Class<? extends RpcSerializer> serializerClazz;

    private final Class<? extends ClientInvokerCore> clientClazz;

    private ClientInvokerCore clientCore;

    public InvokerUnit(Class<? extends RpcSerializer> serializerClazz, Class<? extends ClientInvokerCore> clientClazz) {
        this.serializerClazz = serializerClazz;
        this.clientClazz = clientClazz;
    }

    public void stop() throws Exception{
        clientCore.stopClientServer();
    }

    public void afterSetProperties() throws Exception {
        this.clientCore = clientClazz.newInstance();
        RpcSerializer serializer = serializerClazz.newInstance();
        this.clientCore.setSerializer(serializer);
    }

    public Client getInvokeClient(String addr) {
        return this.clientCore.getClient(addr);
    }

    public ClientInvokerCore getClientCore() {
        return this.clientCore;
    }

    public List<String> filterSubHealth(List<String> urls) {
        return this.clientCore.removeSubHealthUrl(urls);
    }
}
