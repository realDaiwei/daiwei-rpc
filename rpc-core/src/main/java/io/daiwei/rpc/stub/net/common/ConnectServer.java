package io.daiwei.rpc.stub.net.common;

import io.daiwei.rpc.serializer.RpcSerializer;
import io.daiwei.rpc.stub.common.RpcSendable;
import io.daiwei.rpc.stub.net.client.ClientHandler;
import io.daiwei.rpc.stub.net.params.RpcFutureResp;
import io.daiwei.rpc.stub.net.params.RpcRequest;
import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Daiwei on 2021/4/10
 */
public abstract class ConnectServer implements RpcSendable {

    protected Channel channel;

    protected String host;

    protected Integer port;

    public abstract void init(String address, ClientHandler clientHandler);

    public abstract void close();

    public abstract boolean isValid();

    public abstract void send(RpcRequest request);

    public abstract String toString();

    public abstract void cleanStaticResource();

}
