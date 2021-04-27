package io.daiwei.rpc.basic;

import io.daiwei.rpc.entity.User;
import io.daiwei.rpc.service.UserService;
import io.daiwei.rpc.service.impl.UserServiceImpl;
import io.daiwei.rpc.stub.invoker.factory.RpcInvokerFactory;
import io.daiwei.rpc.stub.invoker.refbean.RpcRefBean;
import io.daiwei.rpc.stub.net.params.RpcResponse;
import io.daiwei.rpc.stub.provider.boot.RpcServerBoot;
import io.netty.bootstrap.ServerBootstrap;

import java.util.Arrays;

/**
 * Created by Daiwei on 2021/4/11
 */
public class TestAppMain {

    public static void main(String[] args) throws InterruptedException {
//        rpcRefBean.setFinalAddr("127.0.0.1:7248");
//        rpcRefBean.setAccessToken("");
////        rpcRefBean.setAvailAddr(Arrays.asList("127.0.0.1:7248"));
//        rpcRefBean.setTargetFace(UserService.class);
//        UserService stub = (UserService) factory.createStub(rpcRefBean);
//        long start = System.currentTimeMillis();
        RpcInvokerFactory factory = new RpcInvokerFactory("127.0.0.1:2181");
        UserService userService = factory.createStubByClass(UserService.class);
        for (int i = 0; i < 10000; i++) {
            User user = userService.findByUser(10L);
            System.out.println(user);
//            Thread.sleep(1000);
        }
//        for (int i = 0; i < 10; i++) {
//            System.out.println(user);
//        }
//        System.out.println(System.currentTimeMillis() - start);
        Thread.sleep(2 * 60 * 1000);
//        start = System.currentTimeMillis();
//        UserServiceImpl userService  = new UserServiceImpl();
        for (int i = 0; i < 1000; i++) {
            User user = userService.findByUser(10L);
            System.out.println(user);
        }
//        System.out.println(System.currentTimeMillis() - start);

//        User byUser = userService.findByUser(10L);
//        System.out.println(byUser);
//        while(true) {}
//        factory.stop();
        Thread.sleep(300000);
    }
}
