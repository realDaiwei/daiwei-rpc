package io.daiwei.rpc.basic;

import io.daiwei.rpc.service.UserService;
import io.daiwei.rpc.stub.invoker.factory.RpcInvokerFactory;
import io.daiwei.rpc.stub.invoker.refbean.RpcRefBean;

import java.util.Arrays;

/**
 * Created by Daiwei on 2021/4/11
 */
public class TestAppMain {

    public static void main(String[] args) {
        RpcRefBean rpcRefBean = new RpcRefBean();
        rpcRefBean.setFinalAddr("127.0.0.1:8808");
        rpcRefBean.setAccessToken("");
        rpcRefBean.setAvailAddr(Arrays.asList("127.0.0.1:8808"));
        rpcRefBean.setTargetFace(UserService.class);
        RpcInvokerFactory factory = new RpcInvokerFactory();
        UserService stub = (UserService) factory.createStub(rpcRefBean);
        stub.sayHello("daiwei");
        factory.stop();
    }
}
