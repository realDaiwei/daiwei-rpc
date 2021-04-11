package io.daiwei.rpc.stub.net.common;

import io.daiwei.rpc.serializer.RpcSerializer;
import io.daiwei.rpc.stub.net.params.RpcRequest;
import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Daiwei on 2021/4/10
 */
public abstract class ConnectServer {

    protected Channel channel;

    public abstract void init(String address);

    public abstract void close();

    public abstract boolean isValid();

    public abstract void send(RpcRequest request);

    public abstract void sendAsync(RpcRequest request);


}
