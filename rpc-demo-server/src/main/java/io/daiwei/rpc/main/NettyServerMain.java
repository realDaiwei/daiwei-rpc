package io.daiwei.rpc.main;

import io.daiwei.rpc.server.NettyHttpServer;

/**
 * Created by Daiwei on 2021/3/24
 */
public class NettyServerMain {

    public static void main(String[] args) {
        new NettyHttpServer().run();
    }
}
