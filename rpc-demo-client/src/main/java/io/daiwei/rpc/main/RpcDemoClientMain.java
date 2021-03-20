package io.daiwei.rpc.main;

import io.daiwei.rpc.demo.pojo.User;
import io.daiwei.rpc.demo.service.UserService;
import io.daiwei.rpc.handler.HttpInvokeHandler;
import io.daiwei.rpc.stub.RpcInvokerFactory;

/**
 * Created by Daiwei on 2021/3/21
 */
public class RpcDemoClientMain {

    public static void main(String[] args) {
        RpcInvokerFactory factory = new RpcInvokerFactory(new HttpInvokeHandler());
        UserService service = factory.createStub(UserService.class);
        User user = service.findById(1);
        System.out.println(user);
    }
}
