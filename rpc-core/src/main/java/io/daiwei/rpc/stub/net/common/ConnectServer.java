package io.daiwei.rpc.stub.net.common;

import io.daiwei.rpc.stub.net.client.ClientHandler;
import io.daiwei.rpc.stub.net.params.RpcRequest;
import io.netty.channel.Channel;

/**
 * Created by Daiwei on 2021/4/10
 */
public abstract class ConnectServer {

    protected Channel channel;

    protected String host;

    protected Integer port;

    public abstract void init(String address, ClientHandler clientHandler);

    public abstract void close();

    public abstract boolean isValid();

    public abstract void sendAsync(RpcRequest request);

    public abstract void send(RpcRequest request);

    public abstract String toString();

    public abstract void cleanStaticResource();

}
