package io.daiwei.rpc.basic;

import io.daiwei.rpc.entity.Order;
import io.daiwei.rpc.entity.User;
import io.daiwei.rpc.service.OrderService;
import io.daiwei.rpc.service.UserService;
import io.daiwei.rpc.stub.invoker.factory.RpcInvokerFactory;

/**
 * Created by Daiwei on 2021/4/11
 */
public class TestApp1Main {

    public static void main(String[] args) throws InterruptedException {
        RpcInvokerFactory factory = new RpcInvokerFactory("127.0.0.1:2181");
        UserService userService = factory.createStubByClass(UserService.class);
        OrderService orderService = factory.createStubByClass(OrderService.class);
        for (int i = 0; i < 10000; i++) {
            User user = userService.findByUser(10L);
            System.out.println(user);
            Order order = orderService.findById(10L);
            System.out.println(order);
            Thread.sleep(1000);
        }
        Thread.sleep(300000);

//        factory.stop();
    }
}
