package io.daiwei.rpc.rpc.init;

import io.daiwei.rpc.stub.provider.common.ServerInitRunnable;

/**
 * Created by Daiwei on 2021/5/3
 */
public class HelloInit implements ServerInitRunnable {

    @Override
    public void init() {
        System.out.println("hello init!");
    }
}
