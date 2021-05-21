package io.daiwei.rpc.stub.net;

import io.daiwei.rpc.stub.net.params.RpcResponse;

/**
 * Created by Daiwei on 2021/3/28
 */
public interface Server {

    void start();

    void stop();

    boolean isActive();

    void sendAsync();

}
