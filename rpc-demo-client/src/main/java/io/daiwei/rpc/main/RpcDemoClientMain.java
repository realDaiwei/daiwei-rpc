package io.daiwei.rpc.main;

import io.daiwei.rpc.demo.pojo.User;
import io.daiwei.rpc.demo.service.UserService;
import io.daiwei.rpc.handler.HttpByteBuddyInvokeHandler;
import io.daiwei.rpc.stub.RpcInvokerFactory;

/**
 * Created by Daiwei on 2021/3/21
 */
public class RpcDemoClientMain {

    public static void main(String[] args) {
        HttpByteBuddyInvokeHandler handler = new HttpByteBuddyInvokeHandler();
        RpcInvokerFactory factory = new RpcInvokerFactory(handler);
        UserService service = factory.createStub(UserService.class);
        User user = service.findById(1);
        System.out.println(user);
    }
}
