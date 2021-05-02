package io.daiwei.rpc.basic;

import io.daiwei.rpc.service.impl.UserServiceImpl;
import io.daiwei.rpc.stub.provider.boot.RpcServerBoot;

/**
 * Created by Daiwei on 2021/4/24
 */
public class ServerTest3Main {

    public static void main(String[] args) {
        RpcServerBoot.builder().init("127.0.0.1:2181").registerService(UserServiceImpl.class)
                .build().run();
    }
}
