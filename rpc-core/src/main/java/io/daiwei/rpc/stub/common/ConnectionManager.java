package io.daiwei.rpc.stub.common;

import io.daiwei.rpc.stub.net.Client;

/**
 * Created by Daiwei on 2021/4/22
 */
public interface ConnectionManager {

    Client getClient(String addr);

    void removeConn(String conn);
}
