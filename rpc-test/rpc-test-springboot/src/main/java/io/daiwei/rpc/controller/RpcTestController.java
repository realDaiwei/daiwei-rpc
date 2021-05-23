package io.daiwei.rpc.controller;

import io.daiwei.rpc.entity.ReturnT;
import io.daiwei.rpc.entity.User;
import io.daiwei.rpc.router.DefaultRouter;
import io.daiwei.rpc.service.UserService;
import io.daiwei.rpc.stub.invoker.annotation.RpcRef;
import io.daiwei.rpc.stub.provider.annotation.RpcService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Daiwei on 2021/5/2
 */
@RestController
@RequestMapping("/rpc")
public class RpcTestController {

    @RpcRef(routerClass = DefaultRouter.class)
    private UserService userService;

    @GetMapping("/user")
    public ReturnT<User> findByUserId() {
        User byUser = userService.findByUser(10L);
        return ReturnT.ok(byUser);
    }

}
